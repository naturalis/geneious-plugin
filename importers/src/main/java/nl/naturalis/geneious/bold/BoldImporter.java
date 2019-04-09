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
import static nl.naturalis.geneious.note.NaturalisField.*;
import static nl.naturalis.geneious.util.DebugUtil.toJson;
import static nl.naturalis.geneious.bold.BoldColumn.*;

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
    guiLogger.info("Loading BOLD file " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    StoredDocumentTable<BoldKey> selectedDocuments = createLookupTableForSelectedDocuments();
    int good = 0, bad = 0, updated = 0, unused = 0;
    NaturalisNote note;
    for (int i = 0; i < rows.size(); ++i) {
      int line = line(i);
      BoldRow row = new BoldRow(cfg.getColumnNumbers(), rows.get(i));
      if (row.isEmpty()) {
        guiLogger.debugf(() -> format("Ignoring empty row at line %s", line));
        ++bad;
        continue;
      }
      if (!row.hasValueFor(SAMPLE_ID, MARKER)) {
        guiLogger.debugf(() -> format("Missing registration number and/or marker in line %s", line));
        ++bad;
        continue;
      }
      if ((note = createNote(line, row)) == null) {
        ++bad;
        continue;
      }
      ++good;
      BoldKey key = new BoldKey(row.get(SAMPLE_ID), row.get(MARKER));
      guiLogger.debugf(() -> format("Searching for selected documents with %s", key));
      StoredDocumentList docs = selectedDocuments.get(key);
      if (docs == null) {
        guiLogger.debugf(() -> format("Not found. Row at line %s remains unused", line));
        ++unused;
      } else {
        guiLogger.debugf(() -> format("Found %1$s document%2$s. Updating document%2$s", docs.size(), plural(docs)));
        for (StoredDocument doc : docs) {
          if (note.saveTo(doc)) {
            ++updated;
          } else {
            String fmt = "Document with %s not updated (no new values in BOLD file)";
            guiLogger.debugf(() -> format(fmt, key));
          }
        }
      }
    }
    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated;
    guiLogger.info("Number of valid rows in BOLD file .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in BOLD file ...: %3d", bad);
    guiLogger.info("Number of unused rows in BOLD file ......: %3d", unused);
    guiLogger.info("Number of selected documents ............: %3d", selected);
    guiLogger.info("Number of updated documents .............: %3d", updated);
    guiLogger.info("Number of unchanged documents ...........: %3d", unchanged);
    guiLogger.info("UNUSED ROW (explanation): The row's registration number and marker");
    guiLogger.info("          did not correspond to any of the selected documents, but");
    guiLogger.info("          they may or may not correspond to other, unselected documents.");
    return null; // Tells Geneious that we didn't create any new documents.
  }

  private static NaturalisNote createNote(int line, BoldRow row) {
    guiLogger.debugf(() -> format("Line %s: %s", line, toJson(row)));
    BoldNoteFactory factory = new BoldNoteFactory(line, row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private StoredDocumentTable<BoldKey> createLookupTableForSelectedDocuments() {
    StoredDocumentTable<BoldKey> sdt = new StoredDocumentTable<>(cfg.getSelectedDocuments(), this::getBoldKey);
    return sdt;
  }

  private BoldKey getBoldKey(StoredDocument sd) {
    String marker = sd.getNaturalisNote().get(SEQ_MARKER);
    if (marker != null) {
      String extractId = sd.getNaturalisNote().getExtractId();
      if (extractId != null) {
        return new BoldKey(extractId, marker);
      }
    }
    return null; // do not add to StoredDocumentTable
  }

  private int line(int zeroBased) {
    return zeroBased + cfg.getSkipLines() + 1;
  }

  private static String plural(Collection<?> c) {
    return c.size() == 1 ? "" : "s";
  }

}
