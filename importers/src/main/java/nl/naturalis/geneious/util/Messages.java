package nl.naturalis.geneious.util;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.JsonUtil.toJson;

import java.util.Collection;
import java.util.List;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.CsvImportConfig;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.StorableDocument;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.smpl.SampleSheetImporter1;

import static java.util.stream.Collectors.*;

/**
 * Common messages or messages so verbose that they would clutter the code that emits them too much.
 * 
 * @author Ayco Holleman
 *
 */
public class Messages {

  public static final String LIST_ITEM = "\n**** ";

  /**
   * Common DEBUG messages.
   * 
   * @author Ayco Holleman
   *
   */
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
     * Displays the annotations created from a row.
     * 
     * @param logger
     * @param note
     */
    public static void showNote(GuiLogger logger, NaturalisNote note) {
      logger.debugf(() -> format("Generated annotations: %s", toJson(note)));
    }

    /**
     * <i>Scanning selected documents for [&#46;&#46;&#46;]</i>
     * 
     * @param logger
     * @param keyName
     * @param keyValue
     */
    public static void scanningSelectedDocuments(GuiLogger logger, String keyName, Object keyValue) {
      logger.debugf(() -> format("Scanning selected documents for %s %s", keyName, keyValue));
    }

    /**
     * Message informing the user how many documents matched the key of the currently processed row.
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
     * Message informing the user that, although a row could be matched against several documents, the documents were not updated because
     * the row contained no new values.
     * 
     * @param logger
     * @param name
     * @param file
     */
    public static void noNewValues(GuiLogger logger, String name, String file) {
      logger.debugf(() -> format("Document %s not updated. No new values in %s", name, file));
    }

    /**
     * <i>Updated [&#46;&#46;&#46;] documents [&#46;&#46;&#46;]</i>
     * 
     * @param logger
     * @param updated
     * @param keyName
     * @param keyValue
     */
    public static void updatedDocuments(GuiLogger logger, Collection<StoredDocument> docs, int updated, String keyName, Object keyValue) {
      logger.debugf(() -> format("%d out of %d documents with %s %s updated", updated, docs.size(), keyName, keyValue));
    }
    
    
  }

  /**
   * Common INFO messages.
   * 
   * @author Ayco Holleman
   *
   */
  public static class Info {
    private Info() {}

    /**
     * Informational message indicating that a source file is being loaded into memory.
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
     * Message informing the user that there were multiple documents matching an extract ID, but that they were not selected and therefore
     * not updated.
     * 
     * @param logger
     * @param docs
     */
    public static void foundUnselectedDocuments(GuiLogger logger, List<StoredDocument> docs) {
      String msg;
      if (docs.size() == 1) {
        msg = new StringBuilder(255)
            .append("Found 1 ")
            .append(docs.get(0).getType())
            .append(" document matching extract ID ")
            .append(docs.get(0).getNaturalisNote().getExtractId())
            .append(", but the document was not selected and therefore not updated:")
            .append(LIST_ITEM)
            .append(docs.get(0).getLocation())
            .toString();

      } else {
        msg = new StringBuilder(255)
            .append("Found ")
            .append(docs.size())
            .append(" documents matching extract ID ")
            .append(docs.get(0).getNaturalisNote().getExtractId())
            .append(", but the documents were not selected and therefore not updated:")
            .append(LIST_ITEM)
            .append(docs.stream()
                .map(StoredDocument::getLocation)
                .collect(joining(LIST_ITEM)))
            .toString();
      }
      logger.info(msg);
    }

    /**
     * Explains the concept of an unused row for sample sheet imports using {@link SampleSheetImporter1}.
     * 
     * @param logger
     */
    public static void explainUnusedRowForSampleSheets1(GuiLogger logger) {
      logger.info("UNUSED ROW (explanation): The row's extract ID was not found in any of");
      logger.info("           the selected documents, but may or may not be found in other,");
      logger.info("           unselected documents elsewhere in the database");
    }

    /**
     * Explains the concept of an unused row for sample sheet imports using {@link SampleSheetImporter2}.
     * 
     * @param logger
     */
    public static void explainUnusedRowForSampleSheets2(GuiLogger logger) {
      logger.info("UNUSED ROW (explanation): The row's extract ID was found in an existing");
      logger.info("           document, but the document was not selected and therefore not");
      logger.info("           updated.");
    }

    /**
     * Explains the concept of an unused row for CRS and BOLD.
     * 
     * @param logger
     */
    public static void explainUnusedRowForCrsAndBold(GuiLogger logger) {
      logger.info("UNUSED ROW (explanation): The row's registration number was not found in any");
      logger.info("           of the selected documents, but may or may not be found in other,");
      logger.info("           unselected documents elsewhere in the database");
    }

    /**
     * <i>Operation completed successfully</i>
     * 
     * @param logger
     * @param operation
     */
    public static void operationCompletedSuccessfully(GuiLogger logger, String operation) {
      logger.info(operation + " completed successfully");
    }

  }

  /**
   * Common WARN messages.
   * 
   * @author Ayco Holleman
   *
   */
  public static class Warn {
    private Warn() {}

    /**
     * Message indicating that a row could not be processed because it didn't have value(s) for its key column(s).
     * 
     * @param logger
     * @param keyName
     * @param line
     */
    public static void missingKey(GuiLogger logger, String keyName, int line) {
      logger.warn("Ignoring row at line %d. Missing %s", line, keyName);
    }

    /**
     * Message indicating that a row will not be processed because its key was the same as a previous row's key.
     * 
     * @param logger
     * @param key
     * @param line
     * @param prevLine
     */
    public static void duplicateKey(GuiLogger logger, Object key, int line, int prevLine) {
      logger.warn("Ignoring row at line %d. Duplicate key: %s. Duplicate of row at line %d", line, key, prevLine);
    }
  }

  /**
   * Common ERROR messages.
   * 
   * @author Ayco Holleman
   *
   */
  public static class Error {
    private Error() {}

    /**
     * <i>Found multiple dummy documents with extract ID [&#46;&#46;&#46;]</i>
     * 
     * @param logger
     * @param doc
     * @param dummies
     */
    public static void duplicateDummies(GuiLogger logger, StorableDocument doc, List<StoredDocument> dummies) {
      String msg = new StringBuilder(255)
          .append("Error while annotating document ")
          .append(doc.getSequenceInfo().getName())
          .append(". Found multiple dummy documents with extract ID ")
          .append(doc.getSequenceInfo().getNaturalisNote().getExtractId())
          .append(LIST_ITEM)
          .append(dummies.stream()
              .map(StoredDocument::getLocation)
              .collect(joining(LIST_ITEM)))
          .toString();
      logger.error(msg);
    }
  }

  private Messages() {}

}
