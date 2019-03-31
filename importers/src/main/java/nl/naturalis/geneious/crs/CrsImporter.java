package nl.naturalis.geneious.crs;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.APDList;
import nl.naturalis.geneious.util.DebugUtil;
import nl.naturalis.geneious.util.StoredDocument;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;

import static nl.naturalis.geneious.util.DebugUtil.*;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class CrsImporter extends SwingWorker<APDList, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(CrsImporter.class);

  private final CrsImportConfig cfg;

  CrsImporter(CrsImportConfig cfg) {
    this.cfg = cfg;
  }

  /**
   * Enriches the documents selected within the GUI with data from the sample sheet. Documents and sample sheet records
   * are linked using their extract ID. In addition, if requested, this routine will create dummy documents from sample
   * sheet records if their extract ID does not exist yet.
   */
  @Override
  protected APDList doInBackground() throws DatabaseServiceException {
    return importSampleSheet();
  }

  private APDList importSampleSheet() throws DatabaseServiceException {
    return updateOnly();
  }

  private APDList updateOnly() {
    guiLogger.info("Loading CRS file " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    StoredDocumentTable selected = new StoredDocumentTable(cfg.getSelectedDocuments(), CrsImporter::getRegno);
    int numSelected = cfg.getSelectedDocuments().size();
    APDList updates = new APDList(numSelected);
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
      StoredDocumentList docs = selected.get(id);
      if (docs == null) {
        int rownum = userfriendly(i);
        guiLogger.debugf(() -> format("Not found. Row %s remains unused", rownum));
        ++unused;
      } else {
        for (StoredDocument doc : docs) {
          if (note.saveTo(doc)) {
            guiLogger.debugf(() -> format("Updating document with extract ID %s", id));
            updates.add(doc.getGeneiousDocument());
          } else {
            String fmt = "Document with extract ID %s not updated (no new values in sample sheet)";
            guiLogger.debugf(() -> format(fmt, id));
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
    guiLogger.info("Import type: update existing documents; do not create dummies");
    guiLogger.info("Import completed successfully");
    return updates;
  }

  private NaturalisNote createNote(List<String[]> rows, int rownum) {
    String[] values = rows.get(rownum);
    int y = userfriendly(rownum);
    guiLogger.debugf(() -> format("Processing row %s: %s", y, toJson(values)));
    CrsRow row = new CrsRow(cfg.getColumnNumbers(), values);
    CrsNoteFactory factory = new CrsNoteFactory(userfriendly(rownum), row);
    if (factory.isEmpty()) {
      guiLogger.debugf(() -> format("Ignoring empty or useless row at line %s", y));
      return null;
    }
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", DebugUtil.toJson(note)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private static Set<String> collectRegnos(List<String[]> rows) {
    int colno = CrsColumn.REGISTRATION_NUMBER.ordinal();
    return rows.stream()
        .filter(row -> colno < row.length)
        .filter(row -> StringUtils.isNotBlank(row[colno]))
        .map(row -> "e" + row[colno])
        .collect(Collectors.toSet());
  }

  private void handleUnselectedDocument(StoredDocumentList docs, int row) {
    String extractId = docs.get(0).getNaturalisNote().getExtractId();
    if (docs.size() == 1) {
      String fmt = "Row %s (%s) corresponds to an existing document, but the document was not selected and therefore not updated";
      guiLogger.info(fmt, userfriendly(row), extractId);
    } else {
      String fmt = "Row %s (%s) corresponds to %s existing documents, but they were not selected and therefore not updated";
      guiLogger.info(fmt, userfriendly(row), extractId, docs.size());
    }
  }

  private static String getRegno(StoredDocument sd) {
    return sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
  }

  private int userfriendly(int zeroBased) {
    return zeroBased + cfg.getSkipLines() + 1;
  }

}
