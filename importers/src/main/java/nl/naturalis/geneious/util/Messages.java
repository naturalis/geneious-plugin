package nl.naturalis.geneious.util;

import nl.naturalis.geneious.log.GuiLogger;

/**
 * Common messages emitted by all operations.
 * 
 * @author Ayco Holleman
 *
 */
public class Messages {

  /**
   * <i>Scanning selected documents for &#34;&#34;&#34;</i>
   * 
   * @param logger
   * @param keyName
   * @param keyValue
   */
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

  /**
   * <i>Document &#34;&#34;&#34; not updated (no new values in &#34;&#34;&#34;)</i>
   * 
   * @param logger
   * @param file
   * @param keyName
   * @param keyValue
   */
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

  /**
   * <i>Found &#34;&#34;&#34; matching document(s). Comparing values &#34;&#34;&#34;</i>
   * 
   * @param logger
   * @param file
   * @param matchingDocuments
   */
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

  /**
   * <i>Not found. Row at line &#34;&#34;&#34; remains unused</i>
   * 
   * @param logger
   * @param line
   */
  public static void noDocumentsMatchingKey(GuiLogger logger, int line) {
    if (logger.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder(48)
          .append("Not found. Row at line ")
          .append(line)
          .append(" remains unused");
      logger.debug(sb.toString());
    }
  }

  /**
   * <i>&#34;&#34;&#34; completed successfully</i> 
   * @param logger
   * @param operation
   */
  public static void operationCompletedSuccessfully(GuiLogger logger, String operation) {
    logger.info(operation + " completed successfully");
  }

  private Messages() {}

}
