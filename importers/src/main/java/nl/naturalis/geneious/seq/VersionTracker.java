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
    Key key = new Key(doc);
    MutableInt version = cache.get(key);
    if (version == null) {
      version = new MutableInt(1);
      cache.put(key, version);
    } else {
      version.increment();
      if (guiLogger.isDebugEnabled()) {
        guiLogger.debug("Document version of %s document with extract ID %s set to %s", key.dt, key.id, version);
      }
    }
    doc.getSequenceInfo().getNaturalisNote().setDocumentVersion(version.intValue());
  }

}
