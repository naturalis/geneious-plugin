package nl.naturalis.geneious.bold;

import static nl.naturalis.geneious.bold.BoldColumn.MARKER;
import static nl.naturalis.geneious.bold.BoldColumn.SAMPLE_ID;
import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.gui.log.GuiLogger.plural;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.util.DebugUtil.toJson;

import java.util.List;
import java.util.Map;

import nl.naturalis.geneious.ErrorCode;
import nl.naturalis.geneious.MessageProvider;
import nl.naturalis.geneious.NaturalisPluginWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

/**
 * Does the actual work of importing a BOLD file into Geneious.
 */
class BoldImporter extends NaturalisPluginWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldImporter.class);

  private final BoldImportConfig cfg;

  BoldImporter(BoldImportConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected boolean performOperation() throws BoldNormalizationException {
    guiLogger.info("Loading BOLD file " + cfg.getFile().getPath());
    Map<String, List<String[]>> allRows = new BoldNormalizer(cfg).normalizeRows();
    StoredDocumentTable<BoldKey> selectedDocuments = createLookupTableForSelectedDocuments();
    StoredDocumentList updates = new StoredDocumentList(selectedDocuments.size());
    int good = 0, bad = 0, unused = 0;
    NaturalisNote note;
    for (String marker : allRows.keySet()) {
      guiLogger.info("Processing marker \"%s\"", marker);
      List<String[]> rows = allRows.get(marker);
      for (int i = 0; i < rows.size(); ++i) {
        // Convert i to user-friendly (one-based) line number
        int line = cfg.getSkipLines() + i + 1;
        BoldRow row = new BoldRow(cfg.getColumnNumbers(), rows.get(i));
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
            if (doc.attach(note)) {
              updates.add(doc);
            } else {
              String fmt = "Document with %s not updated (no new values in BOLD file)";
              guiLogger.debugf(() -> format(fmt, key));
            }
          }
        }
      }
    }
    updates.forEach(StoredDocument::saveAnnotations);
    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updates.size();
    guiLogger.info("Number of valid rows in BOLD file .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in BOLD file ...: %3d", bad);
    guiLogger.info("Number of unused rows in BOLD file ......: %3d", unused);
    guiLogger.info("Number of selected documents ............: %3d", selected);
    guiLogger.info("Number of updated documents .............: %3d", updates.size());
    guiLogger.info("Number of unchanged documents ...........: %3d", unchanged);
    guiLogger.info("UNUSED ROW (explanation): The row's registration number and marker");
    guiLogger.info("          did not correspond to any of the selected documents, but");
    guiLogger.info("          they may or may not correspond to other, unselected documents.");
    guiLogger.info(MessageProvider.get(ErrorCode.OPERATION_SUCCESS));
    return updates.size() != 0;
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
      String regno = sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
      if (regno != null) {
        return new BoldKey(regno, marker);
      }
    }
    return null; // do not add to lookup table
  }

}
