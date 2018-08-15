package nl.naturalis.geneious;

import static nl.naturalis.geneious.notes.NaturalisField.EXTRACT_ID;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.Condition;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import edu.emory.mathcs.backport.java.util.Collections;
import jebl.util.ProgressListener;
import nl.naturalis.geneious.gui.SampleSheetSelector;
import nl.naturalis.geneious.notes.NaturalisField;
import nl.naturalis.geneious.notes.NaturalisNote;

public class SampleSheetImporter extends DocumentAction implements SampleSheetProcessor {

  private static final String DUMMY_NUCLEOTIDE_SEQUENCE = "NNNNNNNNNN";
  private static final String DUMMY_PLATE_ID = "AA000";
  private static final String DUMMY_MARKER = "Dum";

  public SampleSheetImporter() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    SampleSheetSelector sss = new SampleSheetSelector(this);
    sss.show();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Samples [V2]").setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools)
        .setInMainToolbar(true).setInPopupMenu(true).setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  /**
   * Enriches the selected documents with data from the sample sheet. Documents and sample sheet
   * records are linked using their extract ID. In addition, if requested by the user, this routine
   * will create dummy documents from sample sheet records IF their extract ID does not exist yet in
   * the database.
   */
  @Override
  public void process(File sampleSheet, List<AnnotatedPluginDocument> selectedDocuments,
      boolean createDummies) {
    if (createDummies) {
      processWithDummies(sampleSheet, selectedDocuments);
    }
    else {
      processWithoutDummies(sampleSheet, selectedDocuments);
    }
  }

  public static void processWithDummies(File sampleSheet, List<AnnotatedPluginDocument> selectedDocuments) {
    Map<String, AnnotatedPluginDocument> selectedDocLookupTable = makeLookupTable(selectedDocuments);
    List<String[]> rows = loadSampleSheet(sampleSheet);
    Set<String> dummyEnabledExtractIds;
    try {
      dummyEnabledExtractIds = getExtractIdsForWhichToCreateDummies(rows, selectedDocLookupTable);
    } catch (DatabaseServiceException e) {
      throw new RuntimeException(e); // TODO: handle
    }
    List<AnnotatedPluginDocument> apds = new ArrayList<AnnotatedPluginDocument>(rows.size());
    for (int i = 1; i < rows.size(); i++) {
      SampleSheetRow sampleSheetRow = new SampleSheetRow(i, rows.get(i));
      if (sampleSheetRow.isEmpty()) {
        continue;
      }
      NaturalisNote note;
      try {
        note = sampleSheetRow.extractNote();
      } catch (SampleSheetRowException e) {
        System.err.println(e.getMessage());
        continue; // TODO log error message
      }
      note.setPcrPlateId(DUMMY_PLATE_ID);
      note.setMarker(DUMMY_MARKER);
      AnnotatedPluginDocument apd = selectedDocLookupTable.get(note.getExtractId());
      if (apd == null) {
        if (dummyEnabledExtractIds.contains(note.getExtractId())) {
          NucleotideSequenceDocument nsd = createDummyDocument(note);
          apd = DocumentUtilities.createAnnotatedPluginDocument(nsd);
          note.attach(apd);
          apds.add(apd);
        }
      }
      else {
        note.attach(apd);
        apds.add(apd);
      }
    }
    DocumentUtilities.addGeneratedDocuments(apds, false);
  }

  private static void processWithoutDummies(File sampleSheet,
      List<AnnotatedPluginDocument> selectedDocuments) {
    Map<String, AnnotatedPluginDocument> selectedDocLookupTable = makeLookupTable(selectedDocuments);
    List<String[]> rows = loadSampleSheet(sampleSheet);
    List<AnnotatedPluginDocument> apds = new ArrayList<AnnotatedPluginDocument>(selectedDocuments.size());
    for (int i = 1; i < rows.size(); i++) {
      SampleSheetRow sampleSheetRow = new SampleSheetRow(i, rows.get(i));
      if (sampleSheetRow.isEmpty()) {
        continue;
      }
      NaturalisNote note;
      try {
        note = sampleSheetRow.extractNote();
      } catch (SampleSheetRowException e) {
        System.err.println(e.getMessage());
        continue; // TODO log error message
      }
      AnnotatedPluginDocument apd = selectedDocLookupTable.get(note.getExtractId());
      if (apd != null) {
        note.setPcrPlateId(DUMMY_PLATE_ID);
        note.setMarker(DUMMY_MARKER);
        note.attach(apd);
        apds.add(apd);
      }
    }
    DocumentUtilities.addGeneratedDocuments(apds, false);
  }

  /*
   * If the user checks the option to create dummies, we must do so if: [1] the extract id of a sample
   * sheet record does not exist anywhere in the database; [2] the extract id does not correspond to
   * one of the records selected by the user in the GUI. The latter is of course implied by the
   * former, because the selected records obviously also came from the database. But since Geneious
   * hands us the selected records for free, we can discard them when constructing the database query,
   * thus making the query a bit more light-weight.
   */
  private static Set<String> getExtractIdsForWhichToCreateDummies(List<String[]> sampleSheetRows,
      Map<String, AnnotatedPluginDocument> selectedDocuments) throws DatabaseServiceException {
    Set<String> sampleSheetExtractIds = new HashSet<>(sampleSheetRows.size());
    List<Query> queries = new ArrayList<>(sampleSheetRows.size());
    DocumentField extractIdField = NaturalisField.EXTRACT_ID.createQueryField();
    for (String[] row : sampleSheetRows) {
      if (StringUtils.isBlank(row[3])) {
        continue; // TODO: log something
      }
      String extractId = "e" + row[3];
      sampleSheetExtractIds.add(extractId);
      if (selectedDocuments.keySet().contains(extractId)) {
        /*
         * Then we already know we should NOT create a dummy document for this extract ID, so we don't
         * create a needless WHERE clause for it.
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
     * So these are documents whose extract ID corresonds to at least one sample sheet record. Now get
     * the remaining sample sheet records for which we DO have to create a dummy document.
     */
    Set<String> dbExtractIds = new HashSet<>(apds.size());
    for (AnnotatedPluginDocument apd : apds) {
      dbExtractIds.add(getExtractId(apd));
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

  private static List<String[]> loadSampleSheet(File sampleSheet) {
    TsvParserSettings settings = new TsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    TsvParser parser = new TsvParser(settings);
    List<String[]> rows = parser.parseAll(sampleSheet);
    return rows;
  }

  /*
   * Get the value of the EXTRACT_ID note within the specified document.
   */
  private static String getExtractId(AnnotatedPluginDocument apd) {
    DocumentNotes notes = apd.getDocumentNotes(false);
    DocumentNote note = notes.getNote(NaturalisField.EXTRACT_ID.getNoteType().getCode());
    return note.getFieldValue(NaturalisField.EXTRACT_ID.getCode()).toString();
  }

  /*
   * Create a lookup table that maps the extract IDs of the selected documents to the selected
   * documents themselves.
   */
  private static Map<String, AnnotatedPluginDocument> makeLookupTable(List<AnnotatedPluginDocument> docs) {
    Map<String, AnnotatedPluginDocument> map = new HashMap<>(docs.size() + 1, 1.0F);
    for (AnnotatedPluginDocument doc : docs) {
      String val = (String) EXTRACT_ID.getValueForDocument(doc);
      if (val != null) {
        map.put(val, doc);
      }
    }
    return map;
  }

}
