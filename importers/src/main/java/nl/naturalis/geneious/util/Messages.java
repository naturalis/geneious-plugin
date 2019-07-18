package nl.naturalis.geneious.util;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.JsonUtil.toJson;

import java.util.Collection;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.CsvImportConfig;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Common messages emitted by all operations.
 * 
 * @author Ayco Holleman
 *
 */
public class Messages {

  public static class Debug {
    private Debug() {}

    /**
     * Displays the row currently being processed.
     * 
     * @param logger
     * @param line
     * @param row
     */
    public static void showRow(GuiLogger logger, int line, String[] row) {
      logger.debugf(() -> format("Line %d: %s", line, toJson(row)));
    }

    /**
     * Displayes the annotations (in the form of a {@code NaturalisNote}) created from a row.
     * 
     * @param logger
     * @param note
     */
    public static void showNote(GuiLogger logger, NaturalisNote note) {
      logger.debugf(() -> format("Generated annotations: %s", toJson(note)));
    }

    /**
     * <i>Scanning selected documents for &#46;&#46;&#46;</i>
     * 
     * @param logger
     * @param keyName
     * @param keyValue
     */
    public static void scanningSelectedDocuments(GuiLogger logger, String keyName, Object keyValue) {
      logger.debugf(() -> format("Scanning selected documents for %s %s", keyName, keyValue));
    }

    /**
     * <i>Found X matching document(s). Comparing values &#46;&#46;&#46;</i>
     * 
     * @param logger
     * @param file
     * @param matchingDocuments
     */
    public static void foundDocumensMatchingKey(GuiLogger logger, Collection<StoredDocument> documents, String keyName, Object keyValue) {
      int x = documents.size();
      logger.debugf(() -> format("Found %d document%s with %s %s", x, plural(x), keyName, keyValue));
    }

    /**
     * <i>Document X not updated (no new values in Y)</i>
     * 
     * @param logger
     * @param file
     * @param keyName
     * @param keyValue
     */
    public static void noNewValues(GuiLogger logger, String name, String file) {
      logger.debugf(() -> format("Document %s not updated. No new values in %s", name, file));
    }

    /**
     * <i>Updated X documents &#46;&#46;&#46;</i>
     * 
     * @param logger
     * @param updated
     * @param keyName
     * @param keyValue
     */
    public static void updatedDocuments(GuiLogger logger, Collection<StoredDocument> docs, int updated, String keyName, Object keyValue) {
      logger.debugf(() -> format("%d out of %d documents with %s %s were updated", updated, docs.size(), keyName, keyValue));
    }
  }

  public static class Info {
    private Info() {}

    /**
     * Informational message indicating that the source file (a CSV-like file in this case) is being loaded into memory.
     * 
     * @param logger
     * @param file
     * @param config
     */
    public static void loadingFile(GuiLogger logger, String file, CsvImportConfig<?> config) {
      logger.info("Loading " + config.getFile().getPath());
    }

    /**
     * Displays the number of rows in the source file.
     * 
     * @param logger
     * @param file
     * @param rowCount
     */
    public static void displayRowCount(GuiLogger logger, String file, int rowCount) {
      logger.info("%s contains %s row%s (excluding header rows)", file, rowCount, plural(rowCount));
    }

    /**
     * <i>X completed successfully</i>
     * 
     * @param logger
     * @param operation
     */
    public static void operationCompletedSuccessfully(GuiLogger logger, String operation) {
      logger.info(operation + " completed successfully");
    }

  }

  public static class Warn {
    private Warn() {}

    public static void missingKey(GuiLogger logger, String keyName, int line) {
      logger.warn("Ignoring row at line %d. Missing %s", line, keyName);
    }

    public static void duplicateKey(GuiLogger logger, Object key, int line, int prevLine) {
      logger.warn("Ignoring row at line %d. Duplicate key: %s. Duplicate of row at line %d", line, key, prevLine);
    }
  }

  private Messages() {}

}
