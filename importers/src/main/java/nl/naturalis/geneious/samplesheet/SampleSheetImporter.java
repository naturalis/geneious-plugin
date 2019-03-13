package nl.naturalis.geneious.samplesheet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.QueryUtils;
import nl.naturalis.geneious.util.SpreadSheetReader;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class SampleSheetImporter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetImporter.class);

  private static final String DUMMY_NUCLEOTIDE_SEQUENCE = "NNNNNNNNNN";
  private static final String DUMMY_PLATE_ID = "AA000";
  private static final String DUMMY_MARKER = "Dum";

  private final SampleSheetImportConfig config;

  SampleSheetImporter(SampleSheetImportConfig input) {
    this.config = input;
  }

  /**
   * Enriches the documents selected within the GUI with data from the sample sheet. Documents and sample sheet records are linked using
   * their extract ID. In addition, if requested, this routine will create dummy documents from sample sheet records if their extract ID
   * does not exist yet.
   */
  void process() {
    try {
      List<String[]> rows = loadSampleSheet(config.getFile());
      if (rows != null) {
        if (config.isCreateDummies()) {
          enrichOrCreateDummies(rows);
        } else {
          enrichSelectedDocuments(rows);
        }
      }
    } catch (Throwable t) {
      guiLogger.fatal("Unexpected error while importing sample sheet", t);
    } finally {
      GuiLogManager.showLogAndClose("Sample sheet import log");
    }
  }

  private void enrichOrCreateDummies(List<String[]> rows) {
    Map<String, AnnotatedPluginDocument> lookups = createLookupTable();
    Set<String> newExtractIds;
    try {
      newExtractIds = getNewExtractIds(rows, lookups);
    } catch (DatabaseServiceException e) {
      guiLogger.fatal("Error while executing query", e);
      return;
    }
    List<AnnotatedPluginDocument> apds = new ArrayList<AnnotatedPluginDocument>(rows.size());
    int good = 0, bad = 0, enriched = 0, dummies = 0;
    SampleSheetRow row;
    for (int i = 1; i < rows.size(); i++) {
      if ((row = new SampleSheetRow(i, rows.get(i))).isEmpty()) {
        final int rowNum = i;
        guiLogger.debugf(() -> format("Ignoring empty record (line %s)", rowNum));
        ++bad;
        continue;
      }
      NaturalisNote note;
      try {
        note = row.extractNote();
      } catch (InvalidRowException e) {
        guiLogger.error(e.getMessage());
        ++bad;
        continue;
      }
      ++good;
      note.setPcrPlateId(DUMMY_PLATE_ID);
      note.setMarker(DUMMY_MARKER);
      AnnotatedPluginDocument apd = lookups.get(note.getExtractId());
      if (apd == null) {
        if (newExtractIds.contains(note.getExtractId())) {
          guiLogger.debugf(() -> format("Creating dummy document for extract ID %s", note.getExtractId()));
          NucleotideSequenceDocument nsd = createDummyDocument(note);
          apd = DocumentUtilities.createAnnotatedPluginDocument(nsd);
          note.overwrite(apd);
          apds.add(apd);
          ++dummies;
        }
      } else {
        guiLogger.debugf(() -> format("Enriching document with extract ID %s", note.getExtractId()));
        note.overwrite(apd);
        apds.add(apd);
        ++enriched;
      }
    }
    DocumentUtilities.addGeneratedDocuments(apds, false);
    guiLogger.info("Number of valid records in sample sheet: %s", good);
    guiLogger.info("Number of empty/bad records in sample sheet: %s", bad);
    guiLogger.info("Number of documents selected: %s", config.getSelectedDocuments().length);
    guiLogger.info("Number of documents enriched: %s", enriched);
    guiLogger.info("Number of dummy documents created: %s", dummies);
    guiLogger.info("Import completed successfully");
  }

  private void enrichSelectedDocuments(List<String[]> rows) {
    Map<String, AnnotatedPluginDocument> lookups = createLookupTable();
    List<AnnotatedPluginDocument> apds = new ArrayList<>(config.getSelectedDocuments().length);
    int good = 0, bad = 0, enriched = 0;
    SampleSheetRow row;
    for (int i = 1; i < rows.size(); i++) {
      if ((row = new SampleSheetRow(i, rows.get(i))).isEmpty()) {
        ++bad;
        continue;
      }
      NaturalisNote note;
      try {
        note = row.extractNote();
      } catch (InvalidRowException e) {
        guiLogger.error(e.getMessage());
        ++bad;
        continue;
      }
      ++good;
      AnnotatedPluginDocument apd = lookups.get(note.getExtractId());
      if (apd != null) {
        guiLogger.debugf(() -> format("Enriching document with extract ID %s", note.getExtractId()));
        note.setPcrPlateId(DUMMY_PLATE_ID);
        note.setMarker(DUMMY_MARKER);
        note.overwrite(apd);
        apds.add(apd);
        ++enriched;
      }
    }
    DocumentUtilities.addGeneratedDocuments(apds, true);
    guiLogger.info("Number of valid records in sample sheet: %s", good);
    guiLogger.info("Number of empty/bad records in sample sheet: %s", bad);
    guiLogger.info("Number of documents selected: %s", config.getSelectedDocuments().length);
    guiLogger.info("Number of documents enriched: %s", enriched);
    guiLogger.info("Import completed successfully");
  }

  /*
   * Scans the sample sheet for new extract IDs. If the user had chosen to create dummies, we must do so if: [1] the extract ID of a sample
   * sheet row does not exist anywhere in the target database; [2] the extract ID does not correspond to any of the documents selected by
   * the user in the GUI. The second condition is implied by the first condition, because the selected documents obviously were somewhre in
   * the target database. But since Geneious hands us the selected records for free, we can discard them when constructing the database
   * query, thus making the query a bit more light-weight.
   */
  private static Set<String> getNewExtractIds(List<String[]> rows,
      Map<String, AnnotatedPluginDocument> selected) throws DatabaseServiceException {
    guiLogger.debug(() -> "Marking rows with new extract IDs (will become dummy documents)");
    Set<String> allIdsInSheet = new HashSet<>(rows.size(), 1F);
    Set<String> nonSelectedIds = new HashSet<>(rows.size(), 1F);
    int colno = SampleSheetRow.getColumnNumber(SMPL_EXTRACT_ID);
    for (String[] row : rows) {
      if (colno < row.length && StringUtils.isNotBlank(row[colno])) {
        String id = "e" + row[colno];
        allIdsInSheet.add(id);
        if (!selected.keySet().contains(id)) {
          nonSelectedIds.add(id);
        }
      }
    }
    guiLogger.debug(() -> "Searching database for the provided extract IDs");
    List<AnnotatedPluginDocument> apds = QueryUtils.findByExtractID(nonSelectedIds);
    Set<String> oldIds = new HashSet<>(apds.size(), 1F);
    apds.forEach(apd -> oldIds.add(SMPL_EXTRACT_ID.getValue(apd).toString()));
    allIdsInSheet.removeAll(oldIds);
    allIdsInSheet.removeAll(selected.keySet());
    guiLogger.debugf(() -> format("Found %s new extract IDs in sample sheet", allIdsInSheet.size()));
    return allIdsInSheet;
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
    guiLogger.info("Loading sample sheet: %s", sampleSheet.getAbsolutePath());
    List<String[]> rows;
    try {
      if (sampleSheet.getName().endsWith(".xls") || sampleSheet.getName().endsWith(".xlsx")) {
        SpreadSheetReader ssr = new SpreadSheetReader(sampleSheet);
        ssr.setSheetNumber(config.getSheetNumber() - 1);
        ssr.setSkipRows(config.getSkipLines());
        rows = ssr.readAllRows();
      } else {
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        TsvParser parser = new TsvParser(settings);
        rows = parser.parseAll(sampleSheet);
      }
      guiLogger.debugf(() -> format("Number of rows in sample sheet: %s", rows.size()));
      return rows;
    } catch (Throwable t) {
      guiLogger.fatal("Error loading sample sheet", t);
      return null;
    }
  }

  /*
   * Create a lookup table that maps the extract IDs of the selected documents to the selected documents themselves.
   */
  private Map<String, AnnotatedPluginDocument> createLookupTable() {
    int numSelected = config.getSelectedDocuments().length;
    Map<String, AnnotatedPluginDocument> map = new HashMap<>(numSelected, 1F);
    for (AnnotatedPluginDocument doc : config.getSelectedDocuments()) {
      String val = (String) SMPL_EXTRACT_ID.getValue(doc);
      if (val != null) {
        map.put(val, doc);
      }
    }
    return map;
  }

}
