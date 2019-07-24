package nl.naturalis.geneious.name;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.QueryCache.Key;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Keeps track of, and hands out version numbers for documents.
 *
 * @author Ayco Holleman
 */
class VersionTracker {

  private static final GuiLogger logger = GuiLogManager.getLogger(VersionTracker.class);

  private final Map<Key, MutableInt> versions;

  /**
   * Creates a new {@code VersionTracker} using the provided map of initial document versions. The map key is the
   * combination of a document's type and name while the map value is the latest document version for this combination.
   * 
   * @param initialVersions
   */
  VersionTracker(Map<Key, MutableInt> initialVersions) {
    versions = initialVersions;
  }

  /**
   * Sets the document version on the provided document and then updates the intern cache of document versions.
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
    MutableInt version = versions.computeIfAbsent(key, k -> new MutableInt());
    version.increment();
    note.setDocumentVersion(version.intValue());
  }

}
