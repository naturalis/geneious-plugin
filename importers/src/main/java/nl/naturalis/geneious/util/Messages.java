package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.List;

import nl.naturalis.common.ArrayMethods;
import nl.naturalis.common.ExceptionMethods;
import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.CsvImportConfig;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.smpl.SampleSheetImporter1;

import static java.util.stream.Collectors.joining;

import static nl.naturalis.geneious.Settings.settings;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.JsonUtil.toJson;

/**
 * Common messages or messages so verbose that they would clutter the code that emits them too much.
 * 
 * @author Ayco Holleman
 *
 */
public class Messages {

  private static final String LIST_ITEM = "\n**** ";

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
     * <i>Splitting "x"</i>
     * 
     * @param logger
     * @param name
     */
    public static void splittingName(GuiLogger logger, String name) {
      logger.debugf(() -> format("Splitting \"%s\"", name));
    }

    /**
     * <i>Collecting extract IDs</i>
     * 
     * @param logger
     */
    public static void collectingExtractIds(GuiLogger logger, String source) {
      logger.debugf(() -> format("Collecting extract IDs from %s", source));
    }

    /**
     * <i>Collected x extract IDs</i>
     * 
     * @param logger
     */
    public static void collectedExtractIds(GuiLogger logger, Collection<String> ids) {
      logger.debugf(() -> format("Collected %d extract ID%s", ids.size(), plural(ids)));
    }

    /**
     * <i>Searching database x for matching documents</i>
     * 
     * @param logger
     */
    public static void searchingForDocuments(GuiLogger logger, String dbName) {
      logger.debugf(() -> format("Searching database %s for matching documents", dbName));
    }

    /**
     * <i>Found x for matching documents</i>
     * 
     * @param logger
     */
    public static void foundDocuments(GuiLogger logger, Collection<?> documents) {
      logger.debugf(() -> format("Found %d matching document%s", documents.size(), plural(documents)));
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
     * Message informing the user a document was not updated because the values in the document did not differ from the values in the data
     * source.
     * 
     * @param logger
     * @param name
     * @param dataSource
     */
    public static void noNewValues(GuiLogger logger, String name, String dataSource) {
      logger.debugf(() -> format("Document %s not updated. No new values in %s", name, dataSource));
    }

    /**
     * Message informing the user how many documents with a particular were updated.
     * 
     * @param logger
     * @param updated
     * @param keyName
     * @param keyValue
     */
    public static void updatedDocuments(GuiLogger logger, Collection<StoredDocument> docs, int updated, String keyName, Object keyValue) {
      logger.debugf(() -> format("%d out of %d documents with %s %s updated", updated, docs.size(), keyName, keyValue));
    }

    /**
     * Message informing the user that one of the selected document could not be processed for some reason.
     * 
     * @param logger
     * @param sd
     * @param reason
     * @param msgArgs
     */
    public static void ignoringSelectedDocument(GuiLogger logger, StoredDocument sd, String reason, Object... msgArgs) {
      logger.debugf(() -> format("Ignoring selected document %s. %s", ArrayMethods.prefix(msgArgs, sd.getName(), reason)));
    }

    /**
     * Message informing the user that a dummy has been foud for the extract ID currently being processed.
     * 
     * @param logger
     * @param extractId
     * @param copyToType
     */
    public static void foundDummyForExtractId(GuiLogger logger, String extractId, DocumentType copyToType) {
      logger.debugf(() -> format("Found dummy for extract ID %s. Copying annotations to %s document", extractId, copyToType));
    }

    /**
     * Message informing the user that a dummy document will be deleted.
     * 
     * @param logger
     * @param extractId
     * @param dummies
     */
    public static void dummyQueuedForDeletion(GuiLogger logger, String extractId) {
      logger.debugf(() -> format("Dummy with extract ID %s queued for deletion", extractId));
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
    public static void loadingFile(GuiLogger logger, CsvImportConfig<?> config) {
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
     * <i>Annotating documents</i>
     * 
     * @param logger
     */
    public static void annotatingDocuments(GuiLogger logger) {
      logger.info("Annotating documents");
    }

    /**
     * <i>Versioning document(s)</i>
     * 
     * @param logger
     */
    public static void versioningDocuments(GuiLogger logger, Collection<?> documents) {
      logger.info("Versioning document%s", plural(documents));
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
     * Message informing the user how many documents were updated with annotations present in the povided dummy documents.
     * 
     * @param logger
     * @param docs
     * @param dummies
     */
    public static void documentsUpdatedFromDummies(GuiLogger logger, Collection<?> docs, Collection<?> dummies) {
      String fmt = "%d document%s updated with annotations from %d dummy document%s";
      logger.info(fmt, docs.size(), plural(docs), dummies.size(), plural(dummies));
    }

    /**
     * Message informing the user that the provided dummies will be sent to their graves.
     * 
     * @param logger
     * @param dummies
     */
    public static void deletingObsoleteDummies(GuiLogger logger, Collection<?> dummies) {
      logger.info("Deleting %s obsolete dummy document%s", dummies.size(), plural(dummies));
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
      logger.info("           updated");
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

    /**
     * Warns the user that a document is missing the required document version annotation.
     * 
     * @param logger
     * @param doc
     * @param field
     */
    public static void missingDocumentVersion(GuiLogger logger, StoredDocument doc, NaturalisField field) {
      corruptDocument(logger, doc, "Document has value for %d but no document version", field);
    }

    /**
     * Warns the user that a duplicate document version has been encountered.
     * 
     * @param logger
     * @param docName
     * @param docVersion
     */
    public static void duplicateDocumentVersion(GuiLogger logger, String docName, String docVersion) {
      logger.warn("Data corruption: multiple documents with identical name (%s) and document version (%s)", docName, docVersion);
    }

    /**
     * Informs the user about data corruption.
     * 
     * @param logger
     * @param doc
     * @param reason
     * @param msgArgs
     */
    public static void corruptDocument(GuiLogger logger, StoredDocument doc, String reason, Object... msgArgs) {
      logger.warn("Corrupt document: %s. %s", ArrayMethods.prefix(msgArgs, doc.getName(), reason));
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
     * <i>Error while parsing &lt;name&gt;</i>
     * 
     * @param logger
     * @param name
     * @param exception
     */
    public static void nameParsingFailed(GuiLogger logger, String name, NotParsableException exception) {
      logger.error("Error while parsing \"%s\": %s", name, exception.getMessage());
    }

    /**
     * <i>Found multiple dummy documents with extract ID [etc.]</i>
     * 
     * @param logger
     * @param doc
     * @param dummies
     */
    public static void duplicateDummies(GuiLogger logger, String docName, String extractId, List<StoredDocument> dummies) {
      String msg = new StringBuilder(255)
          .append("Error while annotating document ")
          .append(docName)
          .append(". Found multiple dummy documents with extract ID ")
          .append(extractId)
          .append(LIST_ITEM)
          .append(dummies.stream()
              .map(StoredDocument::getLocation)
              .collect(joining(LIST_ITEM)))
          .toString();
      logger.error(msg);
    }

    /**
     * Message informing the use that an exception was thrown while executing a database query.
     * 
     * @param logger
     * @param e
     */
    public static void queryError(GuiLogger logger, Exception e) {
      logger.debug(() -> ExceptionMethods.getRootStackTraceAsString(e));
      logger.error("Error while querying database: %s", e.toString());
      logger.error("This problem could possibly be solved by going to Tools -> Preferences");
      logger.error("and lowering the value of Max. query size (currently %d). If the problem", settings().getQuerySize());
      logger.error("persists, enable DEBUG mode in Tools -> Preferences, try again, and send");
      logger.error("a copy of the log to support");
    }

  }

  private Messages() {}

}
