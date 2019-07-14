package nl.naturalis.geneious.crs;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.util.JsonUtil.toJson;

import java.util.List;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Warn;
import nl.naturalis.geneious.util.StoredDocumentTable;

/**
 * Responsible for the actual processing of the row in a CRS file.
 * 
 * @author Ayco Holleman
 */

class CrsImporter {

  private static final GuiLogger logger = GuiLogManager.getLogger(CrsImporter.class);
  private static final String KEY_NAME = "registration number";

  private final CrsImportConfig cfg;
  private final RuntimeInfo runtime;

  /**
   * Creates a new {@code CrsImporter} instance configured using the provided configuration object and updating the
   * provided runtime object as it proceeds.
   * 
   * @param cfg
   * @param runtime
   */
  CrsImporter(CrsImportConfig cfg, RuntimeInfo runtime) {
    this.cfg = cfg;
    this.runtime = runtime;
  }

  /**
   * Processes the provided rows, using them to enrich the provided documents, which are cached as a fast lookup table.
   * 
   * @param rows
   * @param lookups
   */
  void importRows(List<String[]> rows, StoredDocumentTable<String> lookups) {
    for(int i = cfg.getSkipLines(); i < rows.size(); ++i) {
      int line = i + 1;
      logger.debug("Line %d: %s", line, toJson(rows.get(i)));
      CrsRow row = new CrsRow(cfg.getColumnNumbers(), rows.get(i));
      String key = row.get(CrsColumn.REGISTRATION_NUMBER);
      if(key == null) {
        Warn.missingKey(logger, KEY_NAME, line);
        runtime.markBad(i);
        continue;
      }
      Integer prevLine = runtime.checkKey(key, line);
      if(prevLine != null) {
        Warn.duplicateKey(logger, key, line, prevLine);
        continue;
      }
      Debug.scanningSelectedDocuments(logger, KEY_NAME, toJson(key));
      List<StoredDocument> docs = lookups.get(key);
      if(docs == null) {
        Debug.noDocumentsMatchingKey(logger);
        continue;
      }
      NaturalisNote note = createNote(row, line);
      if(note == null) {
        runtime.markBad(i);
        continue;
      }
      runtime.markUsed(i);
      for(StoredDocument doc : docs) {
        if(doc.attach(note)) {
          runtime.updated(doc);
        } else {
          Debug.noNewValues(logger, "CRS file", KEY_NAME, toJson(key));
        }
      }
      lookups.remove(key);
    }
  }

  private static NaturalisNote createNote(CrsRow row, int line) {
    CrsNoteFactory factory = new CrsNoteFactory(line, row);
    try {
      NaturalisNote note = factory.createNote();
      logger.debugf(() -> format("Note created: %s", toJson(note)));
      return note;
    } catch(InvalidRowException e) {
      logger.error(e.getMessage());
      return null;
    }
  }

}
