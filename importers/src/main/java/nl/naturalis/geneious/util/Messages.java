package nl.naturalis.geneious.util;

import java.util.Collection;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogger;

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
     * <i>Scanning selected documents for &#46;&#46;&#46;</i>
     * 
     * @param logger
     * @param keyName
     * @param keyValue
     */
    public static void scanningSelectedDocuments(GuiLogger logger, String keyName, Object keyValue) {
      if(logger.isDebugEnabled()) {
        StringBuilder sb = new StringBuilder(48)
            .append("Scanning selected documents for ")
            .append(keyName)
            .append(' ')
            .append(keyValue);
        logger.debug(sb.toString());
      }
    }

    /**
     * <i>Found &#46;&#46;&#46; matching document(s). Comparing values &#46;&#46;&#46;</i>
     * 
     * @param logger
     * @param file
     * @param matchingDocuments
     */
    public static void foundDocumensMatchingKey(GuiLogger logger, String file, Collection<StoredDocument> matchingDocuments) {
      if(logger.isDebugEnabled()) {
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
     * <i>None found. Row at line &#46;&#46;&#46; with key &#46;&#46;&#46; remains unused</i>
     * 
     * @param logger
     * @param line
     */
    public static void noDocumentsMatchingKey(GuiLogger logger) {
      logger.debug(() -> "None found");
    }

    /**
     * <i>Document &#46;&#46;&#46; not updated (no new values in &#46;&#46;&#46;)</i>
     * 
     * @param logger
     * @param file
     * @param keyName
     * @param keyValue
     */
    public static void noNewValues(GuiLogger logger, String file, String keyName, Object keyValue) {
      if(logger.isDebugEnabled()) {
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
  }

  public static class Info {
    private Info() {}

    /**
     * <i>&#46;&#46;&#46; completed successfully</i>
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
