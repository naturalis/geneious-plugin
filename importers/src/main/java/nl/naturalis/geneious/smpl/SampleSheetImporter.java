package nl.naturalis.geneious.smpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.APDList;
import nl.naturalis.geneious.util.DummySequenceDocument;
import nl.naturalis.geneious.util.QueryUtils;
import nl.naturalis.geneious.util.RowProvider;
import nl.naturalis.geneious.util.StoredDocument;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

import static java.util.function.Predicate.not;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class SampleSheetImporter extends SwingWorker<APDList, Void> {

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
  protected APDList doInBackground() throws DatabaseServiceException {
    return importSampleSheet();
  }

  private APDList importSampleSheet() throws DatabaseServiceException {
    if (cfg.isCreateDummies()) {
      return enrichOrCreateDummies();
    }
    return enrichOnly();
  }

  private APDList enrichOrCreateDummies() throws DatabaseServiceException {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowProvider(cfg).getAllRows();
    StoredDocumentTable selected = new StoredDocumentTable(cfg.getSelectedDocuments());
    guiLogger.info("Marking rows with new extract IDs");
    StoredDocumentTable unselected = findUnselectedDocuments(rows, selected.keySet());
    APDList updates = new APDList();
    APDList dummies = new APDList();
    int good = 0, bad = 0, updatedDummies = 0, unused = 0;
    for (int i = 0; i < rows.size(); ++i) {
      SampleSheetRow row = new SampleSheetRow(i, rows.get(i));
      final int rowNum = i + cfg.getSkipLines();
      if (row.isEmpty()) {
        guiLogger.debugf(() -> format("Ignoring empty row at line %s", rowNum));
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
      StoredDocumentList docs0 = selected.get(note.getExtractId());
      if (docs0 == null) {
        StoredDocumentList docs1 = unselected.get(note.getExtractId());
        if (docs1 == null) {
          dummies.add(createDummy(note));
        } else {
          handleUnselectedDocument(docs1, rowNum);
          ++unused;
        }
      } else {
        for (StoredDocument doc : docs0) {
          if (note.saveTo(doc)) {
            if (doc.isDummy()) {
              ++updatedDummies;
              guiLogger.debugf(() -> format("Updating dummy with extract ID %s", note.getExtractId()));
            } else {
              guiLogger.debugf(() -> format("Updating document with extract ID %s", note.getExtractId()));
            }
          }
        }
      }
    }
    int numSelected = cfg.getSelectedDocuments().size();
    int numUpdates = updates.size();
    int newDummies = dummies.size();
    int numUnchanged = numSelected - numUpdates - updatedDummies;
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", numSelected);
    guiLogger.info("Number of unchanged documents ..............: %3d", numUnchanged);
    guiLogger.info("Number of updated documents ................: %3d", numUpdates);
    guiLogger.info("Number of updated dummies ..................: %3d", updatedDummies);
    guiLogger.info("Number of dummy documents created ..........: %3d", newDummies);
    guiLogger.info("UNUSED ROW: The row's extract ID was found in an existing");
    guiLogger.info("            document, but the  document was not selected");
    guiLogger.info("            and therefore not updated.");
    guiLogger.info("Import completed successfully");
    return updates.and(dummies);
  }

  private APDList enrichOnly() {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowProvider(cfg).getAllRows();
    StoredDocumentTable selectedDocuments = new StoredDocumentTable(cfg.getSelectedDocuments());
    int numSelected = cfg.getSelectedDocuments().size();
    APDList updates = new APDList(numSelected);
    int good = 0, bad = 0, unused = 0;
    for (int i = 1; i < rows.size(); ++i) {
      SampleSheetRow row = new SampleSheetRow(i, rows.get(i));
      final int rowNum = i + cfg.getSkipLines();
      if (row.isEmpty()) {
        guiLogger.debugf(() -> format("Ignoring empty row at line %s", rowNum));
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
      StoredDocumentList docs = selectedDocuments.get(extractId);
      if (docs == null) {
        ++unused;
      } else {
        for (StoredDocument doc : docs) {
          if (note.saveTo(doc)) {
            updates.add(doc.getGeneiousDocument());
            guiLogger.debugf(() -> format("Updating document with extract ID %s", note.getExtractId()));
          }
        }
      }
    }
    int numUpdates = updates.size();
    int numUnchanged = numSelected - numUpdates;
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", numSelected);
    guiLogger.info("Number of updated documents ................: %3d", numUpdates);
    guiLogger.info("Number of unchanged documents ..............: %3d", numUnchanged);
    guiLogger.info("UNUSED ROW: The row's extract ID did not correspond to any");
    guiLogger.info("            of the selected documents.");
    guiLogger.info("Import completed successfully");
    return updates;
  }

  private static StoredDocumentTable findUnselectedDocuments(List<String[]> rows, Set<String> selectedIds) throws DatabaseServiceException {
    int colno = SampleSheetRow.COLNO_EXTRACT_ID;
    Set<String> nonSelectedIds = rows.stream()
        .filter(row -> colno < row.length)
        .filter(row -> StringUtils.isNotBlank(row[colno]))
        .map(row -> "e" + row[colno])
        .filter(not(selectedIds::contains))
        .collect(Collectors.toSet());
    guiLogger.debug(() -> "Searching database ...");
    return new StoredDocumentTable(QueryUtils.findByExtractID(nonSelectedIds));
  }

  private static AnnotatedPluginDocument createDummy(NaturalisNote note) {
    guiLogger.debugf(() -> format("Creating dummy document for extract ID %s", note.getExtractId()));
    AnnotatedPluginDocument apd = new DummySequenceDocument(note).wrap();
    apd.save();
    return apd;
  }

  private static void handleUnselectedDocument(StoredDocumentList docs, int rowNum) {
    String extractId = docs.get(0).getNaturalisNote().getExtractId();
    if (docs.size() == 1) {
      String fmt = "Row %s (%s) corresponds to an existing document, but the document was not selected";
      guiLogger.info(fmt, rowNum, extractId);
    } else {
      String fmt = "Row %s (%s) corresponds to %s existing documents, but the they were not selected";
      guiLogger.info(fmt, rowNum, extractId, docs.size());
    }
  }

}
