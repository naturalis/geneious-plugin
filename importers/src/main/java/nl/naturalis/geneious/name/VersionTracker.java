package nl.naturalis.geneious.name;

import static nl.naturalis.geneious.log.GuiLogger.format;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.QueryCache.Key;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Keeps track of, and hands out version numbers for documents based on their {@link DocumentType} and name (not
 * including name suffixes like ".ab1", " (ab1)" and " (fasta)"). A {@code VersionTracker} starts out with a set of
 * initial document versions, which are the document versions of the most recent historical documents.
 *
 * @author Ayco Holleman
 */
class VersionTracker {

  private static final GuiLogger logger = GuiLogManager.getLogger(VersionTracker.class);

  private final Map<Key, MutableInt> cache;

  VersionTracker(Map<Key, MutableInt> initialVersions) {
    cache = initialVersions;
  }

  /**
   * Sets the document version on the provided document and then increments the document version for the combination of
   * {@link DocumentType} and name found within the document.
   * 
   * @param doc
   */
  void setDocumentVersion(StorableDocument doc) {
    String documentName = doc.getSequenceInfo().getName();
    DocumentType documentType = doc.getSequenceInfo().getDocumentType();
    NaturalisNote note = doc.getSequenceInfo().getNaturalisNote();
    if(StringUtils.isNotBlank(note.getDocumentVersion())) {
      logger.error("Document version already set for document %s. Overwriting forbidden", documentName);
      return;
    }
    Key key = new Key(documentType, documentName);
    MutableInt version = cache.computeIfAbsent(key, k -> new MutableInt());
    version.increment();
    logger.debugf(() -> format("Document %s (%s): document version set to %s", documentName, documentType, version));
    note.setDocumentVersion(version.intValue());
  }

}
