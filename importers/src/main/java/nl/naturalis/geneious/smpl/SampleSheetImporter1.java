package nl.naturalis.geneious.smpl;

import static nl.naturalis.geneious.smpl.SampleSheetSwingWorker.FILE_DESCRIPTION;
import static nl.naturalis.geneious.smpl.SampleSheetSwingWorker.KEY_NAME;

import java.util.List;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Log.Debug;
import nl.naturalis.geneious.util.Log.Warn;
import nl.naturalis.geneious.util.DocumentLookupTable;

/**
 * The sample sheet importer that runs when the user has opted <i>not</i> to create place-holder documents
 * (a#&46;k&#46;a#&46; dummies) for rows whose extract ID does not correspond to any document, selected or not.
 * 
 * @author Ayco Holleman
 *
 */
public class SampleSheetImporter1 {

  private static final GuiLogger logger = GuiLogManager.getLogger(SampleSheetImporter1.class);

  private final SampleSheetImportConfig config;
  private final RuntimeInfo runtime;

  /**
   * Creates a sample sheet importer configured using the provided configuration object and updating the provided runtime
   * object as it proceeds.
   * 
   * @param config
   * @param runtime
   */
  SampleSheetImporter1(SampleSheetImportConfig config, RuntimeInfo runtime) {
    this.config = config;
    this.runtime = runtime;
  }

  /**
   * Processes the provided rows, using them to enrich the provided documents. The documents come in the form of a fast
   * lookup table so they can be quickly scanned for each and every row. The lookup table is keyed on the document's
   * extract ID.
   * 
   * @param rows
   * @param lookups
   */
  void importRows(List<String[]> rows, DocumentLookupTable<String> lookups) {
    for(int i = 0; i < rows.size(); ++i) {
      int line = i + config.getSkipLines() + 1;
      Debug.showRow(logger, line, rows.get(i));
      SampleSheetRow row = new SampleSheetRow(config.getColumnNumbers(), rows.get(i));
      String key = row.get(SampleSheetColumn.EXTRACT_ID);
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
      Debug.scanningSelectedDocuments(logger, KEY_NAME, key);
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
    Debug.updatedDocuments(logger, docs, updated, KEY_NAME, note.getExtractId());
  }

  private static NaturalisNote createNote(SampleSheetRow row, int line) {
    SmplNoteFactory factory = new SmplNoteFactory(row, line);
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
