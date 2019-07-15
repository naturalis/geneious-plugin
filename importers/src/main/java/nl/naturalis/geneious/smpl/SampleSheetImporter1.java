package nl.naturalis.geneious.smpl;

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

public class SampleSheetImporter1 {

  private static final GuiLogger logger = GuiLogManager.getLogger(SampleSheetImporter1.class);
  private static final String KEY_NAME = "extract ID";

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
  void importRows(List<String[]> rows, StoredDocumentTable<String> lookups) {
    for(int i = config.getSkipLines(); i < rows.size(); ++i) {
      int line = i + 1;
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
        Debug.noDocumentsMatchingKey(logger);
        continue;
      }
      NaturalisNote note = createNote(row, line);
      if(note == null) {
        runtime.markBad(i);
        continue;
      }
      runtime.markUsed(i);
      Debug.showNote(logger, note);
      for(StoredDocument doc : docs) {
        if(doc.attach(note)) {
          runtime.updated(doc);
        } else {
          Debug.noNewValues(logger, "sample sheet", KEY_NAME, key);
        }
      }
      lookups.remove(key);
    }
  }

  private static NaturalisNote createNote(SampleSheetRow row, int line) {
    SmplNoteFactory factory = new SmplNoteFactory(line, row);
    try {
      return factory.createNote();
    } catch(InvalidRowException e) {
      logger.error(e.getMessage());
      return null;
    }
  }

}
