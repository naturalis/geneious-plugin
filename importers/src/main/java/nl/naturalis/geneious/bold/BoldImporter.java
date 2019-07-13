package nl.naturalis.geneious.bold;

import static nl.naturalis.geneious.bold.BoldColumn.SAMPLE_ID;
import static nl.naturalis.geneious.bold.BoldColumn.SEQ_LENGTH;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.JsonUtil.toJson;

import java.util.List;
import java.util.Set;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Messages.*;

/**
 * Imports rows for one specific marker. This class can also be used to extract only specimen-related information from
 * the rows in the BOLD spreadsheet.
 * 
 * @author Ayco Holleman
 */
class BoldImporter {

  private static final GuiLogger logger = GuiLogManager.getLogger(BoldImporter.class);

  private final BoldImportConfig cfg;
  private final RuntimeInfo runtime;

  /**
   * Creates a new {@code BoldImporter} instance configured using the provided configuration object and updating the
   * provided runtime object as it proceeds.
   * 
   * @param cfg
   * @param runtime
   */
  BoldImporter(BoldImportConfig cfg, RuntimeInfo runtime) {
    this.cfg = cfg;
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
      logger.info(">>> Processing remainder (matching on registration number only)");
    } else {
      logger.info(">>> Processing marker %s", marker);
    }
    int updated = 0; // The number of updates for this particular marker
    for(int i = 0; i < rows.size(); ++i) {
      if(runtime.isBadRow(i)) {
        continue;
      }
      int line = i + cfg.getSkipLines() + 1;
      BoldRow row = new BoldRow(cfg.getColumnNumbers(), rows.get(i));
      String regno = row.get(SAMPLE_ID);
      if(regno == null) {
        Warn.missingKey(logger, "CRS registration number", line);
        runtime.markBad(i);
        continue;
      }
      if(marker != null & row.get(SEQ_LENGTH) == null) {
        logger.info("Ignoring row at line %d: no value for marker %s", line, marker);
        continue;
      }
      BoldKey key = new BoldKey(regno, marker);
      Integer prevLine = runtime.checkKey(key, line);
      if(prevLine != null) {
        Warn.duplicateKey(logger, key, line, prevLine);
        continue;
      }
      Debug.scanningSelectedDocuments(logger, "key", toJson(key));
      Set<StoredDocument> docs = lookups.get(key);
      if(docs == null) {
        continue;
      }
      Debug.foundDocumensMatchingKey(logger, "BOLD file", docs);
      logger.debug("Line %d: %s", line, toJson(rows.get(i)));
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
          Debug.noNewValues(logger, "BOLD file", "key", toJson(key));
        }
      }
      lookups.remove(key);
    }
    if(marker == null) {
      logger.info("%d document%s updated while matching on registration number only", updated, plural(updated));
    } else {
      logger.info("%d document%s updated for marker %s", updated, plural(updated), marker);
    }
  }

  private static NaturalisNote createNote(BoldRow row, int line, boolean ignoreMarkerColumns) {
    BoldNoteFactory factory = new BoldNoteFactory(line, row, ignoreMarkerColumns);
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
