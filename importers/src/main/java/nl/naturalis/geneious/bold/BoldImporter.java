package nl.naturalis.geneious.bold;

import static nl.naturalis.geneious.bold.BoldColumn.SAMPLE_ID;
import static nl.naturalis.geneious.bold.BoldColumn.SEQ_LENGTH;
import static nl.naturalis.geneious.bold.BoldSwingWorker.FILE_DESCRIPTION;
import static nl.naturalis.geneious.bold.BoldSwingWorker.KEY_NAME;
import static nl.naturalis.geneious.log.GuiLogger.plural;
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

/**
 * Imports rows for one specific marker. This class can also be used to extract only specimen-related information from
 * the rows in the BOLD spreadsheet.
 * 
 * @author Ayco Holleman
 */
class BoldImporter {

  private static final GuiLogger logger = GuiLogManager.getLogger(BoldImporter.class);

  private final BoldImportConfig config;
  private final RuntimeInfo runtime;

  /**
   * Creates a new {@code BoldImporter} instance configured using the provided configuration object and updating the
   * provided runtime object as it proceeds.
   * 
   * @param config
   * @param runtime
   */
  BoldImporter(BoldImportConfig config, RuntimeInfo runtime) {
    this.config = config;
    this.runtime = runtime;
  }

  /**
   * Equivalent to calling {@code importRows(rows, null, lookups}.
   * 
   * @param rows
   * @param lookups
   */
  void importRows(List<String[]> rows, DocumentLookupTable lookups) {
    importRows(rows, null, lookups);
  }

  /**
   * Imports the provided rows using the provided marker to look up all selected documents with that marker. The marker is
   * explicitly allowed to be null, in which case the marker-related columns in the rows will be ignored. In other words,
   * only the specimen-related information will be used to annotate the documents.
   * 
   * @param rows
   * @param marker
   * @param lookups
   */
  void importRows(List<String[]> rows, String marker, DocumentLookupTable lookups) {
    if(marker == null) {
      logger.info("Processing remaining documents (matching on registration number only)");
    } else {
      logger.info("Processing marker %s", marker);
    }
    int updated = 0; // The number of updates for this particular marker
    for(int i = 0; i < rows.size(); ++i) {
      if(runtime.isBadRow(i)) {
        continue;
      }
      int line = i + config.getSkipLines() + 1;
      Debug.showRow(logger, line, rows.get(i));
      BoldRow row = new BoldRow(config.getColumnNumbers(), rows.get(i));
      String regno = row.get(SAMPLE_ID);
      if(regno == null) {
        Warn.missingKey(logger, KEY_NAME, line);
        runtime.markBad(i);
        continue;
      }
      if(marker != null & row.get(SEQ_LENGTH) == null) {
        logger.info("Ignoring row at line %d: no value for marker %s", line, marker);
        continue;
      }
      BoldKey key = new BoldKey(regno, marker);
      Integer prevLine = runtime.checkAndAddKey(key, line);
      if(prevLine != null) {
        Warn.duplicateKey(logger, key, line, prevLine);
        continue;
      }
      Debug.scanningSelectedDocuments(logger, "key", toJson(key));
      List<StoredDocument> docs = lookups.get(key);
      if(docs == null) {
        Debug.noDocumentsMatchingKey(logger);
        continue;
      }
      Debug.foundDocumensMatchingKey(logger, FILE_DESCRIPTION, docs);
      NaturalisNote note = createNote(row, line, marker == null);
      if(note == null) {
        runtime.markBad(i);
        continue;
      }
      runtime.markUsed(i);
      for(StoredDocument doc : docs) {
        if(doc.attach(note)) {
          runtime.updated(doc);
          ++updated;
        } else {
          Debug.noNewValues(logger, FILE_DESCRIPTION, "key", toJson(key));
        }
      }
      lookups.remove(key);
    }
    if(marker == null) {
      logger.info("%d document%s updated while matching on registration number only", updated, plural(updated));
    } else {
      logger.info("%d document%s updated while matching on marker %s", updated, plural(updated), marker);
    }
  }

  private static NaturalisNote createNote(BoldRow row, int line, boolean ignoreMarkerColumns) {
    BoldNoteFactory factory = new BoldNoteFactory(line, row, ignoreMarkerColumns);
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
