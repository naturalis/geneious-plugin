package nl.naturalis.geneious.util;

import nl.naturalis.geneious.log.GuiLogger;

/**
 * Common messages emitted by all operations.
 * 
 * @author Ayco Holleman
 *
 */
public class Messages {

  public static void scanningSelectedDocuments(GuiLogger logger, String keyName, Object keyValue) {
    if (logger.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder(48)
          .append("Scanning selected documents for ")
          .append(keyName)
          .append(' ')
          .append(keyValue);
      logger.debug(sb.toString());
    }
  }

  public static void noNewValues(GuiLogger logger, String file, String keyName, Object keyValue) {
    if (logger.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder(96)
          .append("Document with ")
          .append(keyName)
          .append(' ')
          .append(keyValue)
          .append(" not updated (no new values in ")
          .append(file)
          .append(')');
      logger.debug(sb.toString());
    }
  }

  public static void foundDocumensMatchingKey(GuiLogger logger, String file, StoredDocumentList matchingDocuments) {
    if (logger.isDebugEnabled()) {
      String s = matchingDocuments.size() == 1 ? "" : "s";
      StringBuilder sb = new StringBuilder(96)
          .append("Found ")
          .append(matchingDocuments.size())
          .append(" matching document")
          .append(s)
          .append(". Comparing values in ")
          .append(file)
          .append(" with values in document")
          .append(s)
          .append(" (updating document")
          .append(s)
          .append(" if necessary)");
      logger.debug(sb.toString());
    }
  }

  public static void noDocumentsMatchingKey(GuiLogger logger, int line) {
    if (logger.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder(48)
          .append("Not found. Row at line ")
          .append(line)
          .append(" remains unused");
      logger.debug(sb.toString());
    }
  }

  public static void operationCompletedSuccessfully(GuiLogger logger, String operation) {
    logger.info(operation + " completed successfully");
  }

  private Messages() {}

}
