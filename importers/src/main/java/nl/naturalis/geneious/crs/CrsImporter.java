package nl.naturalis.geneious.crs;

import java.util.List;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.APDList;
import nl.naturalis.geneious.util.StoredDocument;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.util.DebugUtil.toJson;

/**
 * Does the actual work of importing a CRS file into Geneious.
 */
class CrsImporter extends SwingWorker<APDList, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(CrsImporter.class);

  private final CrsImportConfig cfg;

  CrsImporter(CrsImportConfig cfg) {
    this.cfg = cfg;
  }

  /**
   * Enriches the documents selected within the GUI with data from a CRS file. The rows within the CRS files are matched
   * to the selected documents using the registration number annotation (set during sample sheet import).
   */
  @Override
  protected APDList doInBackground() throws DatabaseServiceException {
    return importCrsFile();
  }

  @SuppressWarnings("unused")
  private APDList importCrsFile() throws DatabaseServiceException {
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
    for (int i = 0; i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String regno = note.get(SMPL_REGISTRATION_NUMBER);
      guiLogger.debugf(() -> format("Scanning selected documents for reg.no. %s", regno));
      StoredDocumentList docs = selected.get(regno);
      if (docs == null) {
        int rownum = userfriendly(i);
        guiLogger.debugf(() -> format("Not found. Row %s remains unused", rownum));
        ++unused;
      } else {
        for (StoredDocument doc : docs) {
          if (note.saveTo(doc)) {
            guiLogger.debugf(() -> format("Updating document with reg.no. %s", regno));
            updates.add(doc.getGeneiousDocument());
          } else {
            String fmt = "Document with reg.no. %s not updated (no new values in CRS file)";
            guiLogger.debugf(() -> format(fmt, regno));
          }
        }
      }
    }
    int numUpdates = updates.size();
    int numUnchanged = numSelected - numUpdates;
    guiLogger.info("Number of valid rows in CRS file .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in CRS file ...: %3d", bad);
    guiLogger.info("Number of unused rows in CRS file ......: %3d", unused);
    guiLogger.info("Number of selected documents ...........: %3d", numSelected);
    guiLogger.info("Number of updated documents ............: %3d", numUpdates);
    guiLogger.info("Number of unchanged documents ..........: %3d", numUnchanged);
    guiLogger.info("UNUSED ROW: The row's registration number did not correspond");
    guiLogger.info("            to any of the selected documents.");
    guiLogger.info("Import completed successfully");
    return null;
  }

  private NaturalisNote createNote(List<String[]> rows, int rownum) {
    String[] values = rows.get(rownum);
    int x = userfriendly(rownum);
    CrsRow row = new CrsRow(cfg.getColumnNumbers(), values);
    if (row.isEmptyRow()) {
      guiLogger.debugf(() -> format("Ignoring empty or useless row at line %s", x));
      return null;
    }
    guiLogger.debugf(() -> format("Processing row %s: %s", x, toJson(values, false)));
    CrsNoteFactory factory = new CrsNoteFactory(x, row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note, false)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private static String getRegno(StoredDocument sd) {
    return sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
  }

  private int userfriendly(int zeroBased) {
    return zeroBased + cfg.getSkipLines() + 1;
  }

}
