package nl.naturalis.geneious.samplesheet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.WrappedException;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.DummySequenceDocument;
import nl.naturalis.geneious.util.QueryUtils;
import nl.naturalis.geneious.util.SpreadSheetReader;
import nl.naturalis.geneious.util.StoredDocument;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class SampleSheetImporter extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetImporter.class);

  private final UserInput input;

  SampleSheetImporter(UserInput input) {
    this.input = input;
  }

  /**
   * Enriches the documents selected within the GUI with data from the sample sheet. Documents and sample sheet records are linked using
   * their extract ID. In addition, if requested, this routine will create dummy documents from sample sheet records if their extract ID
   * does not exist yet.
   */
  @Override
  protected Void doInBackground() {
    importSampleSheet();
    return null;
  }

  private void importSampleSheet() {
    try {
      List<String[]> rows = loadSampleSheet(input.getFile());
      if (input.isCreateDummies()) {
        enrichOrCreateDummies(rows);
      } else {
        enrichOnly(rows);
      }
    } catch (Throwable t) {
      guiLogger.fatal(t.getMessage(), t);
    }
  }

  private void enrichOrCreateDummies(List<String[]> rows) throws DatabaseServiceException {
    // Create a lookup table for the selected documents (using extract ID as key)
    Map<String, StoredDocument> selectedDocuments = createLookupTable();
    // Find new extract IDs in sample sheet (rows containing them will become dummies)
    Set<String> newExtractIds = getNewExtractIds(rows, selectedDocuments.keySet());
    List<AnnotatedPluginDocument> updatesOrDummies = new ArrayList<AnnotatedPluginDocument>(rows.size());
    int good = 0, bad = 0, enriched = 0, dummies = 0;
    for (int i = 0; i < rows.size(); ++i) {
      SampleSheetRow row = new SampleSheetRow(i, rows.get(i));
      if (row.isEmpty()) {
        final int rowNum = i;
        guiLogger.debugf(() -> format("Ignoring empty record (line %s)", (rowNum + input.getSkipLines())));
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
      StoredDocument document = selectedDocuments.get(note.getExtractId());
      if (document == null) { // sample sheet row does not correspond to a user-selected document
        if (newExtractIds.contains(note.get(SMPL_EXTRACT_ID))) {
          guiLogger.debugf(() -> format("Creating dummy document for extract ID %s", note.getExtractId()));
          updatesOrDummies.add(new DummySequenceDocument(note).wrap());
          ++dummies;
        }
      } else {
        guiLogger.debugf(() -> format("Enriching document with extract ID %s", note.getExtractId()));
        note.saveTo(document);
        updatesOrDummies.add(document.getGeneiousDocument());
        ++enriched;
      }
    }
    DocumentUtilities.addGeneratedDocuments(updatesOrDummies, false);
    guiLogger.info("Number of valid records in sample sheet: %s", good);
    guiLogger.info("Number of empty/bad records in sample sheet: %s", bad);
    guiLogger.info("Number of documents selected: %s", input.getSelectedDocuments().length);
    guiLogger.info("Number of documents enriched: %s", enriched);
    guiLogger.info("Number of dummy documents created: %s", dummies);
    guiLogger.info("Import completed successfully");
  }

  private void enrichOnly(List<String[]> rows) {
    Map<String, StoredDocument> selectedDocuments = createLookupTable();
    List<AnnotatedPluginDocument> updates = new ArrayList<>(selectedDocuments.size());
    int good = 0, bad = 0, enriched = 0;
    SampleSheetRow row;
    for (int i = 1; i < rows.size(); ++i) {
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
      String extractId = note.getExtractId();
      StoredDocument document = selectedDocuments.get(extractId);
      if (document != null) {
        guiLogger.debugf(() -> format("Enriching document with extract ID %s", extractId));
        note.saveTo(document);
        updates.add(document.getGeneiousDocument());
        ++enriched;
      }
    }
    DocumentUtilities.addGeneratedDocuments(updates, true);
    guiLogger.info("Number of valid records in sample sheet: %s", good);
    guiLogger.info("Number of empty/bad records in sample sheet: %s", bad);
    guiLogger.info("Number of documents selected: %s", input.getSelectedDocuments().length);
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
  private static Set<String> getNewExtractIds(List<String[]> rows, Set<String> selectedIds) throws DatabaseServiceException {
    guiLogger.debug(() -> "Marking rows with new extract IDs (will become dummy documents)");
    Set<String> allIdsInSheet = new HashSet<>(rows.size(), 1F);
    Set<String> nonSelectedIds = new HashSet<>(rows.size(), 1F);
    int colno = SampleSheetRow.COLNO_EXTRACT_ID;
    for (String[] row : rows) {
      if (colno < row.length && StringUtils.isNotBlank(row[colno])) {
        String id = "e" + row[colno];
        allIdsInSheet.add(id);
        if (!selectedIds.contains(id)) {
          nonSelectedIds.add(id);
        }
      }
    }
    guiLogger.debug(() -> "Searching database ...");
    List<AnnotatedPluginDocument> documents = QueryUtils.findByExtractID(nonSelectedIds);
    Set<String> exists = new HashSet<>(documents.size(), 1F);
    documents.forEach(document -> exists.add(SMPL_EXTRACT_ID.readFrom(document)));
    allIdsInSheet.removeAll(exists);
    allIdsInSheet.removeAll(selectedIds);
    guiLogger.debugf(() -> format("Sample sheet contains %s new extract ID(s)", allIdsInSheet.size()));
    return allIdsInSheet;
  }

  private List<String[]> loadSampleSheet(File sampleSheet) {
    guiLogger.info("Loading sample sheet: %s", sampleSheet.getAbsolutePath());
    List<String[]> rows;
    try {
      if (sampleSheet.getName().endsWith(".xls") || sampleSheet.getName().endsWith(".xlsx")) {
        SpreadSheetReader ssr = new SpreadSheetReader(sampleSheet);
        ssr.setSheetNumber(input.getSheetNumber() - 1);
        ssr.setSkipRows(input.getSkipLines());
        rows = ssr.readAllRows();
      } else {
        TsvParserSettings settings = new TsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        settings.setNumberOfRowsToSkip(input.getSkipLines());
        TsvParser parser = new TsvParser(settings);
        rows = parser.parseAll(sampleSheet);
      }
      guiLogger.debugf(() -> format("Number of rows in sample sheet: %s", rows.size()));
      return rows;
    } catch (Throwable t) {
      throw new WrappedException("Error loading sample sheet", t);
    }
  }

  /*
   * Create a lookup table that maps the extract IDs of the selected documents to the selected documents themselves.
   */
  private Map<String, StoredDocument> createLookupTable() {
    int numSelected = input.getSelectedDocuments().length;
    Map<String, StoredDocument> map = new HashMap<>(numSelected, 1F);
    for (AnnotatedPluginDocument doc : input.getSelectedDocuments()) {
      NaturalisNote note = new NaturalisNote(doc);
      StoredDocument sd = new StoredDocument(doc, note);
      String extractId = note.getExtractId();
      if (extractId == null) {
        guiLogger.debugf(() -> format("Ignoring selected document without extract ID (urn=\"%s\")", doc.getURN()));
      } else {
        map.put(extractId, sd);
      }
    }
    return map;
  }

}
