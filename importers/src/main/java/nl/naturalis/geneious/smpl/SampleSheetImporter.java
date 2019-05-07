package nl.naturalis.geneious.smpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.APDList;
import nl.naturalis.geneious.util.Ping;
import nl.naturalis.geneious.util.QueryUtils;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

import static java.util.function.Predicate.not;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addGeneratedDocuments;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.gui.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.DebugUtil.toJson;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class SampleSheetImporter extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetImporter.class);

  private final SampleSheetImportConfig cfg;

  SampleSheetImporter(SampleSheetImportConfig cfg) {
    this.cfg = cfg;
  }

  /**
   * Enriches the documents selected within the GUI with data from the sample sheet. Documents and sample sheet records are linked using
   * their extract ID. In addition, if requested, this routine will create dummy documents from sample sheet records if their extract ID
   * does not exist yet.
   */
  @Override
  protected Void doInBackground() {
    try {
      importSampleSheet();
    } catch (Throwable t) {
      guiLogger.fatal(t);
    }
    return null;
  }

  private void importSampleSheet() throws DatabaseServiceException {
    if (Ping.resume()) {
      if (cfg.isCreateDummies()) {
        updateOrCreateDummies();
      } else {
        updateSelectedDocuments();
      }
      Ping.start();
    }
  }

  private void updateOrCreateDummies() throws DatabaseServiceException {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    guiLogger.info("Collecting extract IDs");
    Set<String> extractIds = collectExtractIds(rows);
    StoredDocumentTable<String> selectedDocuments = createLookupTableForSelectedDocuments();
    guiLogger.info("Searching database %s for matching extract IDs", getTargetDatabaseName());
    Set<String> unselectedExtractIds = extractIds.stream()
        .filter(not(selectedDocuments::containsKey))
        .collect(Collectors.toSet());
    List<AnnotatedPluginDocument> searchResult = QueryUtils.findByExtractID(unselectedExtractIds);
    StoredDocumentTable<String> unselected = createLookupTableForUnselectedDocuments(searchResult);
    int overlap = (int) extractIds.stream().filter(selectedDocuments::containsKey).count();
    guiLogger.info("Sample sheet contains %s row%s matching selected documents", rows.size(), plural(rows));
    guiLogger.info("Sample sheet contains %s extract ID%s matching selected documents", overlap, plural(overlap));
    guiLogger.info("Sample sheet contains %s extract ID%s matching unselected documents", searchResult.size(), plural(searchResult));
    // Note that this is only an estimate of the amount of dummies to be created. The involved rows in the sample sheet
    // may not pass validation.
    int dummyCount = extractIds.size() - overlap - unselected.keySet().size();
    guiLogger.info("Sample sheet contains %s brand new extract ID%s", dummyCount, plural(dummyCount));
    StoredDocumentList updatesOrDummies = new StoredDocumentList(overlap + dummyCount);
    int good = 0, bad = 0, updated = 0, updatedDummies = 0, unused = 0;
    NaturalisNote note;
    for (int i = 0; i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      guiLogger.debugf(() -> format("Searching selected documents for extract ID %s", id));
      StoredDocumentList docs0 = selectedDocuments.get(id);
      if (docs0 == null) {
        guiLogger.debugf(() -> format("Not found. Searching query cache for unselected documents with extract ID %s", id));
        StoredDocumentList docs1 = unselected.get(id);
        if (docs1 == null) {
          guiLogger.debugf(() -> format("Not found. Creating dummy document for extract ID %s", id));
          updatesOrDummies.add(new DummySequence(note).wrap());
        } else {
          ++unused;
          if (guiLogger.isDebugEnabled()) {
            logUnusedRow(docs1, i);
          }
        }
      } else {
        String fmt = "Found %s document%s. Scanning documents for obsolete values";
        guiLogger.debugf(() -> format(fmt, docs0.size(), plural(docs0)));
        for (StoredDocument doc : docs0) {
          if (doc.attach(note)) {
            updatesOrDummies.add(doc);
            if (doc.isDummy()) {
              ++updatedDummies;
            } else {
              ++updated;
            }
          } else {
            String fmt1 = "Document with extract ID %s not updated (no new values in sample sheet)";
            guiLogger.debugf(() -> format(fmt1, id));
          }
        }
      }
    }
    APDList newDummies = new APDList(dummyCount);
    updatesOrDummies.forEach(doc -> {
      doc.saveAnnotations();
      if (doc.isDummy()) {
        newDummies.add(doc.getGeneiousDocument());
      }
    });
    if (!newDummies.isEmpty()) {
      addGeneratedDocuments(newDummies, true, Collections.emptyList());
    }
    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated - updatedDummies;
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", selected);
    guiLogger.info("Number of unchanged documents ..............: %3d", unchanged);
    guiLogger.info("Number of updated documents ................: %3d", updated);
    guiLogger.info("Number of updated dummies ..................: %3d", updatedDummies);
    guiLogger.info("Number of dummy documents created ..........: %3d", newDummies.size());
    guiLogger.info("UNUSED ROW (explanation): The row's extract ID was found in an");
    guiLogger.info("          existing document, but the  document was not selected");
    guiLogger.info("          and therefore not updated.");
    guiLogger.info("Import type: update existing documents or create dummies");
    guiLogger.info("Operation completed successfully");
  }

  private void updateSelectedDocuments() {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    StoredDocumentTable<String> selectedDocuments = createLookupTableForSelectedDocuments();
    StoredDocumentList updates = new StoredDocumentList(selectedDocuments.size());
    int good = 0, bad = 0, unused = 0;
    NaturalisNote note;
    for (int i = 1; i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      guiLogger.debugf(() -> format("Scanning selected documents for extract ID %s", id));
      StoredDocumentList docs = selectedDocuments.get(id);
      if (docs == null) {
        int line = line(i);
        guiLogger.debugf(() -> format("Not found. Row at line %s remains unused", line));
        ++unused;
      } else {
        guiLogger.debugf(() -> format("Found %1$s document%2$s. Updating document%2$s", docs.size(), plural(docs)));
        for (StoredDocument doc : docs) {
          if (doc.attach(note)) {
            updates.add(doc);
          } else {
            String fmt = "Document with extract ID %s not updated (no new values in sample sheet)";
            guiLogger.debugf(() -> format(fmt, id));
          }
        }
      }
    }
    updates.forEach(StoredDocument::saveAnnotations);
    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updates.size();
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", selected);
    guiLogger.info("Number of updated documents ................: %3d", updates.size());
    guiLogger.info("Number of unchanged documents ..............: %3d", unchanged);
    guiLogger.info("UNUSED ROW (explanation): The row's extract ID did not correspond");
    guiLogger.info("          to any of the selected documents, but may or may not");
    guiLogger.info("          correspond to other, unselected documents.");
    guiLogger.info("Import type: update existing documents; do not create dummies");
    guiLogger.info("Operation completed successfully");
  }

  private StoredDocumentTable<String> createLookupTableForSelectedDocuments() {
    return new StoredDocumentTable<>(cfg.getSelectedDocuments(), sd -> sd.getNaturalisNote().getExtractId());
  }

  private static StoredDocumentTable<String> createLookupTableForUnselectedDocuments(List<AnnotatedPluginDocument> searchResult) {
    return new StoredDocumentTable<>(searchResult, sd -> sd.getNaturalisNote().getExtractId());
  }

  private NaturalisNote createNote(List<String[]> rows, int rownum) {
    String[] values = rows.get(rownum);
    int x = line(rownum);
    SampleSheetRow row = new SampleSheetRow(cfg.getColumnNumbers(), values);
    if (row.isEmpty()) {
      guiLogger.debugf(() -> format("Ignoring empty row at line %s", x));
      return null;
    }
    guiLogger.debugf(() -> format("Line %s: %s", x, toJson(values)));
    SmplNoteFactory factory = new SmplNoteFactory(x, row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private Set<String> collectExtractIds(List<String[]> rows) {
    int colno = cfg.getColumnNumbers().get(SampleSheetColumn.EXTRACT_ID);
    return rows.stream()
        .filter(row -> colno < row.length)
        .filter(row -> StringUtils.isNotBlank(row[colno]))
        .map(row -> "e" + row[colno])
        .collect(Collectors.toSet());
  }

  private static void logUnusedRow(StoredDocumentList docs, int row) {
    String extractId = docs.get(0).getNaturalisNote().getExtractId();
    if (docs.size() == 1) {
      String fmt = "Row at line %s (%s) corresponds to an existing document, but the document was not selected and therefore not updated";
      guiLogger.debug(fmt, line(row), extractId);
    } else {
      String fmt = "Row at line %s (%s) corresponds to %s existing documents, but they were not selected and therefore not updated";
      guiLogger.debug(fmt, line(row), extractId, docs.size());
    }
  }

  private static int line(int zeroBased) {
    return zeroBased + 1;
  }

}
