package nl.naturalis.geneious.crs;

import static nl.naturalis.geneious.crs.CrsSwingWorker.FILE_DESCRIPTION;
import static nl.naturalis.geneious.crs.CrsSwingWorker.KEY_NAME;
import static nl.naturalis.geneious.util.JsonUtil.toJson;

import java.util.List;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Warn;
import nl.naturalis.geneious.util.DocumentLookupTable;

/**
 * Responsible for the actual processing of the row in a CRS file.
 * 
 * @author Ayco Holleman
 */

class CrsImporter {

  private static final GuiLogger logger = GuiLogManager.getLogger(CrsImporter.class);
  private final CrsImportConfig config;
  private final RuntimeInfo runtime;

  /**
   * Creates a new {@code CrsImporter} instance configured using the provided configuration object and updating the
   * provided runtime object as it proceeds.
   * 
   * @param config
   * @param runtime
   */
  CrsImporter(CrsImportConfig config, RuntimeInfo runtime) {
    this.config = config;
    this.runtime = runtime;
  }

  /**
   * Processes the provided rows, using them to enrich the provided documents, which are cached as a fast lookup table.
   * 
   * @param rows
   * @param lookups
   */
  void importRows(List<String[]> rows, DocumentLookupTable<String> lookups) {
    for(int i = 0; i < rows.size(); ++i) {
      int line = i + +config.getSkipLines() + 1;
      Debug.showRow(logger, line, rows.get(i));
      CrsRow row = new CrsRow(config.getColumnNumbers(), rows.get(i));
      String key = row.get(CrsColumn.REGISTRATION_NUMBER);
      if(key == null) {
        Warn.missingKey(logger, KEY_NAME, line);
        runtime.markBad(i);
        continue;
      }
      Integer prevLine = runtime.checkAndAddKey(key, line);
      if(prevLine != null) {
        Warn.duplicateKey(logger, key, line, prevLine);
        continue;
      }
      Debug.scanningSelectedDocuments(logger, KEY_NAME, toJson(key));
      List<StoredDocument> docs = lookups.get(key);
      if(docs == null) {
        continue;
      }
      Debug.foundDocumensMatchingKey(logger, docs, KEY_NAME, key);
      NaturalisNote note = createNote(row, line);
      if(note == null) {
        runtime.markBad(i);
        continue;
      }
      runtime.markUsed(i);
      annotateDocuments(docs, note);
      lookups.remove(key);
    }
  }

  private void annotateDocuments(List<StoredDocument> docs, NaturalisNote note) {
    int updated = 0;
    for(StoredDocument doc : docs) {
      if(doc.attach(note)) {
        runtime.updated(doc);
        ++updated;
      } else {
        Debug.noNewValues(logger, doc.getName(), FILE_DESCRIPTION);
      }
    }
    Debug.updatedDocuments(logger, docs, updated, KEY_NAME, note.get(NaturalisField.SMPL_REGISTRATION_NUMBER));
  }

  private static NaturalisNote createNote(CrsRow row, int line) {
    CrsNoteFactory factory = new CrsNoteFactory(row, line);
    try {
      NaturalisNote note = factory.createNote();
      Debug.showNote(logger, note);
      return note;
    } catch(InvalidRowException e) {
      logger.error(e.getMessage());
      return null;
    }
  }

}
