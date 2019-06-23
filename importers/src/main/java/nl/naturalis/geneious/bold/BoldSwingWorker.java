package nl.naturalis.geneious.bold;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static nl.naturalis.geneious.bold.BoldColumn.MARKER;
import static nl.naturalis.geneious.bold.BoldColumn.SAMPLE_ID;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.util.JsonUtil.toJson;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.CommonStatistics;
import nl.naturalis.geneious.util.Messages;
import nl.naturalis.geneious.util.PreconditionValidator;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

/**
 * Manages and coordinates the import of BOLD files into Geneious.
 * 
 * @author Ayco Holleman
 */
class BoldSwingWorker extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldSwingWorker.class);

  private final BoldImportConfig cfg;

  BoldSwingWorker(BoldImportConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws NonFatalException {
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
    validator.validate();
    guiLogger.info("Loading BOLD file " + cfg.getFile().getPath());
    BoldNormalizer normalizer = new BoldNormalizer(cfg);
    Map<String, List<String[]>> allRows = normalizer.normalizeRows();
    MarkerMap markerMap = new MarkerMap(normalizer.getMarkers());
    guiLogger.debugf(() -> format("Will use these BOLD-to-Naturalis marker mappings: %s", toJson(markerMap)));
    StoredDocumentTable<BoldKey> selectedDocuments = createLookupTableForSelectedDocuments();
    StoredDocumentList updated = new StoredDocumentList(selectedDocuments.size());
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
        String boldMarker = row.get(MARKER);
        String[] mapsTo = markerMap.get(boldMarker);
        /*
         * BOLD marker may map to multiple Naturalis markers. Only if none of the Naturalis markers is found within
         * the selected documents will the row in the BOLD file remain unused.
         */
        boolean used = false;
        for (String naturalisMarker : mapsTo) {
          BoldKey key = new BoldKey(row.get(SAMPLE_ID), naturalisMarker);
          Messages.scanningSelectedDocuments(guiLogger, "key", key);
          StoredDocumentList docs = selectedDocuments.get(key);
          if (docs != null) {
            Messages.foundDocumensMatchingKey(guiLogger, "BOLD file", docs);
            used = true;
            for (StoredDocument doc : docs) {
              if (doc.attach(note)) {
                updated.add(doc);
              } else {
                Messages.noNewValues(guiLogger, "BOLD file", "key", key);
              }
            }
          }
        }
        if (!used) {
          Messages.noDocumentsMatchingKey(guiLogger, line);
          ++unused;
        }
      }
    }

    updated.forEach(StoredDocument::saveAnnotations);
    List<AnnotatedPluginDocument> all = updated.unwrap();
    all.addAll(updated.unwrap());
    if (!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }

    new CommonStatistics()
        .rowStats(good, bad, unused)
        .documentStats(cfg.getSelectedDocuments().size(), updated.size())
        .write(guiLogger);

    guiLogger.info("UNUSED ROW (explanation): The row's registration number and marker");
    guiLogger.info("          did not correspond to any of the selected documents, but");
    guiLogger.info("          they may or may not correspond to other, unselected documents.");
    Messages.operationCompletedSuccessfully(guiLogger, "BOLD Import");
    return all;
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
    return new StoredDocumentTable<>(cfg.getSelectedDocuments(), this::getBoldKey);
  }

  private BoldKey getBoldKey(StoredDocument sd) {
    String marker = sd.getNaturalisNote().get(SEQ_MARKER);
    if (marker != null) {
      String regno = sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
      if (regno != null) {
        return new BoldKey(regno, marker);
      }
    }
    return null; // ignore document; do not add to lookup table
  }

  @Override
  protected String getLogTitle() {
    return "BOLD Import";
  }

}
