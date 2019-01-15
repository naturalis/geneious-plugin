package nl.naturalis.geneious.samplesheet;

import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_ID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.Condition;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import edu.emory.mathcs.backport.java.util.Collections;
import jebl.util.ProgressListener;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.SpreadSheetReader;

class SampleSheetProcessor {

  private static final String DUMMY_NUCLEOTIDE_SEQUENCE = "NNNNNNNNNN";
  private static final String DUMMY_PLATE_ID = "AA000";
  private static final String DUMMY_MARKER = "Dum";

  private final SampleSheetProcessInput input;
  private final GuiLogger logger;

  SampleSheetProcessor(SampleSheetProcessInput input) {
    this.logger = new GuiLogger();
    this.input = input;
  }

  /**
   * Enriches the documents selected within the GUI with data from the sample sheet. Documents and sample sheet records are linked using
   * their extract ID. In addition, if requested, this routine will create dummy documents from sample sheet records if their extract ID
   * does not exist yet.
   */
  void process() {
    try {
      if (input.isCreateDummies()) {
        processAndCreateDummies();
      } else {
        processWithoutDummies();
      }
    } finally {
      logger.showLog("Sample sheet import log");
    }
  }

  private void processAndCreateDummies() {
    Map<String, AnnotatedPluginDocument> selectedDocsLookupTable = makeLookupTable();
    List<String[]> rows = loadSampleSheet(input.getFile());
    Set<String> nonExistentExtractIds;
    try {
      nonExistentExtractIds = getNonExistentExtractIds(rows, selectedDocsLookupTable);
    } catch (DatabaseServiceException e) {
      logger.fatal("Error while executing query", e);
      return;
    }
    List<AnnotatedPluginDocument> apds = new ArrayList<AnnotatedPluginDocument>(rows.size());
    int numValidRows = 0;
    int numBadRows = 0;
    int numEnrichments = 0;
    int numDummies = 0;
    for (int i = 1; i < rows.size(); i++) {
      SampleSheetRow row = new SampleSheetRow(i, rows.get(i));
      if (row.isEmpty()) {
        numBadRows++;
        continue;
      }
      NaturalisNote note;
      try {
        note = row.extractNote();
      } catch (InvalidRowException e) {
        logger.error(e.getMessage());
        numBadRows++;
        continue;
      }
      numValidRows++;
      note.setPcrPlateId(DUMMY_PLATE_ID);
      note.setMarker(DUMMY_MARKER);
      AnnotatedPluginDocument apd = selectedDocsLookupTable.get(note.getExtractId());
      if (apd == null) {
        if (nonExistentExtractIds.contains(note.getExtractId())) {
          logger.debug("Creating dummy document for extract ID %s", note.getExtractId());
          NucleotideSequenceDocument nsd = createDummyDocument(note);
          apd = DocumentUtilities.createAnnotatedPluginDocument(nsd);
          note.attach(apd);
          apds.add(apd);
          numDummies++;
        }
      } else {
        logger.debug("Enriching document with extract ID %s", note.getExtractId());
        note.attach(apd);
        apds.add(apd);
        numEnrichments++;
      }
    }
    DocumentUtilities.addGeneratedDocuments(apds, false);
    logger.info("Number of valid records in sample sheet: %s", numValidRows);
    logger.info("Number of empty/bad records in sample sheet: %s", numBadRows);
    logger.info("Number of documents selected: %s", input.getSelectedDocuments().length);
    logger.info("Number of documents enriched: %s", numEnrichments);
    logger.info("Number of dummy documents created: %s", numDummies);
    logger.info("Import completed successfully");
  }

  private void processWithoutDummies() {
    Map<String, AnnotatedPluginDocument> selectedDocLookupTable = makeLookupTable();
    List<String[]> rows = loadSampleSheet(input.getFile());
    List<AnnotatedPluginDocument> apds = new ArrayList<>(input.getSelectedDocuments().length);
    int numValidRows = 0;
    int numBadRows = 0;
    int numEnrichments = 0;
    for (int i = 1; i < rows.size(); i++) {
      SampleSheetRow sampleSheetRow = new SampleSheetRow(i, rows.get(i));
      if (sampleSheetRow.isEmpty()) {
        numBadRows++;
        continue;
      }
      NaturalisNote note;
      try {
        note = sampleSheetRow.extractNote();
      } catch (InvalidRowException e) {
        logger.error(e.getMessage());
        numBadRows++;
        continue;
      }
      numValidRows++;
      AnnotatedPluginDocument apd = selectedDocLookupTable.get(note.getExtractId());
      if (apd != null) {
        logger.debug("Enriching document with extract ID %s", note.getExtractId());
        note.setPcrPlateId(DUMMY_PLATE_ID);
        note.setMarker(DUMMY_MARKER);
        note.attach(apd);
        apds.add(apd);
        numEnrichments++;
      }
    }
    DocumentUtilities.addGeneratedDocuments(apds, false);
    logger.info("Number of valid records in sample sheet: %s", numValidRows);
    logger.info("Number of empty/bad records in sample sheet: %s", numBadRows);
    logger.info("Number of documents selected: %s", input.getSelectedDocuments().length);
    logger.info("Number of documents enriched: %s", numEnrichments);
    logger.info("Import completed successfully");
  }

  /*
   * If the user checks the option to create dummies, we must do so if: [1] the extract id of a sample sheet record does not exist anywhere
   * in the database; [2] the extract id does not correspond to one of the records selected by the user in the GUI. The latter is of course
   * implied by the former, because the selected records obviously also came from the database. But since Geneious hands us the selected
   * records for free, we can discard them when constructing the database query, thus making the query a bit more light-weight.
   */
  private Set<String> getNonExistentExtractIds(List<String[]> sampleSheetRows,
      Map<String, AnnotatedPluginDocument> selectedDocuments) throws DatabaseServiceException {
    logger.info(
        "Filtering sample sheet records with non-existent extract IDs (will become dummy documents)");
    Set<String> sampleSheetExtractIds = new HashSet<>(sampleSheetRows.size());
    List<Query> queries = new ArrayList<>(sampleSheetRows.size());
    DocumentField extractIdField = NaturalisField.EXTRACT_ID.createQueryField();
    for (String[] row : sampleSheetRows) {
      if (StringUtils.isBlank(row[3])) {
        continue;
      }
      String extractId = "e" + row[3];
      sampleSheetExtractIds.add(extractId);
      if (selectedDocuments.keySet().contains(extractId)) {
        /*
         * Then we already know we should NOT create a dummy document for this extract ID, so we don't create a needless WHERE clause for
         * it.
         */
        continue;
      }
      Query query = Query.Factory.createFieldQuery(extractIdField, Condition.EQUAL, extractId);
      queries.add(query);
    }
    // Create a big, fat OR query containing the individual queries
    Query[] queryArray = queries.toArray(new Query[queries.size()]);
    Query query = Query.Factory.createOrQuery(queryArray, Collections.emptyMap());
    DatabaseService ds = (DatabaseService) PluginUtilities
        .getGeneiousService("geneious@jdbc:mysql:__145.136.241.66:3306_geneious");
    List<AnnotatedPluginDocument> apds = ds.retrieve(query, ProgressListener.EMPTY);
    /*
     * So these are documents whose extract ID corresonds to at least one sample sheet record. Now get the remaining sample sheet records
     * for which we DO have to create a dummy document.
     */
    Set<String> dbExtractIds = new HashSet<>(apds.size());
    for (AnnotatedPluginDocument apd : apds) {
      dbExtractIds.add(EXTRACT_ID.getValue(apd).toString());
    }
    sampleSheetExtractIds.removeAll(dbExtractIds);
    sampleSheetExtractIds.removeAll(selectedDocuments.keySet());
    return sampleSheetExtractIds;
  }

  private static NucleotideSequenceDocument createDummyDocument(NaturalisNote note) {
    String seqName = note.getExtractId() + ".dum";
    String descr = "Dummy sequence";
    String sequence = DUMMY_NUCLEOTIDE_SEQUENCE;
    Date timestamp = new Date();
    URN urn = URN.generateUniqueLocalURN("Dummy");
    return new DefaultNucleotideSequence(seqName, descr, sequence, timestamp, urn);
  }

  private List<String[]> loadSampleSheet(File sampleSheet) {
    logger.info("Loading sample sheet: %s", sampleSheet.getAbsolutePath());
    if (sampleSheet.getName().endsWith(".xls") || sampleSheet.getName().endsWith(".xlsx")) {
      SpreadSheetReader ssr = new SpreadSheetReader(sampleSheet);
      ssr.setSheetNumber(input.getSheetNum() - 1);
      ssr.setSkipRows(input.getSkipLines());
      try {
        return ssr.readAllRows();
      } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
      }
    }
    TsvParserSettings settings = new TsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    TsvParser parser = new TsvParser(settings);
    List<String[]> rows = parser.parseAll(sampleSheet);
    return rows;
  }

  /*
   * Create a lookup table that maps the extract IDs of the selected documents to the selected documents themselves.
   */
  private Map<String, AnnotatedPluginDocument> makeLookupTable() {
    logger.debug("Creating lookup table for selected documents");
    Map<String, AnnotatedPluginDocument> map =
        new HashMap<>(input.getSelectedDocuments().length + 1, 1.0F);
    for (AnnotatedPluginDocument doc : input.getSelectedDocuments()) {
      String val = (String) EXTRACT_ID.getValue(doc);
      if (val != null) {
        map.put(val, doc);
      }
    }
    return map;
  }

}
