package nl.naturalis.geneious.bold;

import java.util.Collection;
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
class BoldImporter extends SwingWorker<APDList, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldImporter.class);

  private final BoldImportConfig cfg;

  BoldImporter(BoldImportConfig cfg) {
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
    StoredDocumentTable selectedDocuments = new StoredDocumentTable(cfg.getSelectedDocuments(), BoldImporter::getRegno);
    int good = 0, bad = 0, updated = 0, unused = 0;
    NaturalisNote note;
    for (int i = 0; i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String regno = note.get(SMPL_REGISTRATION_NUMBER);
      guiLogger.debugf(() -> format("Scanning selected documents for reg.no. %s", regno));
      StoredDocumentList docs = selectedDocuments.get(regno);
      if (docs == null) {
        int line = line(i);
        guiLogger.debugf(() -> format("Not found. Row at line %s remains unused", line));
        ++unused;
      } else {
        guiLogger.debugf(() -> format("Found %1$s document%2$s. Updating document%2$s", docs.size(), plural(docs)));
        for (StoredDocument doc : docs) {
          if (note.saveTo(doc)) {
            ++updated;
          } else {
            String fmt = "Document with reg.no. %s not updated (no new values in CRS file)";
            guiLogger.debugf(() -> format(fmt, regno));
          }
        }
      }
    }
    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated;
    guiLogger.info("Number of valid rows in CRS file .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in CRS file ...: %3d", bad);
    guiLogger.info("Number of unused rows in CRS file ......: %3d", unused);
    guiLogger.info("Number of selected documents ...........: %3d", selected);
    guiLogger.info("Number of updated documents ............: %3d", updated);
    guiLogger.info("Number of unchanged documents ..........: %3d", unchanged);
    guiLogger.info("UNUSED ROW (explanation): The row's registration number did not");
    guiLogger.info("          correspond to any of the selected documents, but may or");
    guiLogger.info("          may not correspond to other, unselected documents.");
    return null; // Tells Geneious that we didn't create any new documents.
  }

  private NaturalisNote createNote(List<String[]> rows, int rownum) {
    BoldRow row = new BoldRow(cfg.getColumnNumbers(), rows.get(rownum));
    if (row.isEmptyRow()) {
      guiLogger.debugf(() -> format("Ignoring empty row at line %s", line(rownum)));
      return null;
    }
    guiLogger.debugf(() -> format("Line %s: %s", line(rownum), toJson(rows.get(rownum))));
    BoldNoteFactory factory = new BoldNoteFactory(line(rownum), row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private static String getRegno(StoredDocument sd) {
    return sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
  }

  private int line(int zeroBased) {
    return zeroBased + cfg.getSkipLines() + 1;
  }

  private static String plural(Collection<?> c) {
    return c.size() == 1 ? "" : "s";
  }

}
