package nl.naturalis.geneious.seq;

import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.seq.QueryCache.Key;

/**
 * Keeps track of, increments, and hands out version numbers for documents based on their {@link DocumentType} and
 * extract ID.
 *
 * @author Ayco Holleman
 */
class VersionTracker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(VersionTracker.class);

  private final Map<Key, MutableInt> cache;

  VersionTracker(Map<Key, MutableInt> initialVersions) {
    cache = initialVersions;
  }

  /**
   * Sets the document version on the provided document and then increments the document version for the combination of
   * {@link DocumentType} and extract ID found within the document.
   * 
   * @param doc
   */
  void setDocumentVersion(ImportableDocument doc) {
    Key key = new Key(doc.getSequenceInfo().getDocumentType(), doc.getSequenceInfo().getName());
    MutableInt version = cache.get(key);
    if (version == null) {
      version = new MutableInt(1);
      cache.put(key, version);
      if (guiLogger.isDebugEnabled()) {
        guiLogger.debug("No other %s document with name \"%s\" exists. Document version set to 1", key.docType, key.field);
      }
    } else {
      version.increment();
      if (guiLogger.isDebugEnabled()) {
        guiLogger.debug("Another %s document with name \"%s\" already exists. Document version set to %s", key.docType, key.field, version);
      }
    }
    doc.getSequenceInfo().getNaturalisNote().setDocumentVersion(version.intValue());
  }

}
