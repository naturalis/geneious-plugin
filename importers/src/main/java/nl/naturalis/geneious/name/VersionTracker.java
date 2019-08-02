package nl.naturalis.geneious.name;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.QueryCache.Key;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Keeps track of, and hands out version numbers for documents.
 *
 * @author Ayco Holleman
 */
public class VersionTracker {

  private static final GuiLogger logger = GuiLogManager.getLogger(VersionTracker.class);

  private final Map<Key, MutableInt> versions;

  /**
   * Creates a new {@code VersionTracker} using the provided map of initial document versions. The map key is the combination of a
   * document's type and name while the map value is the latest document version for this combination.
   * 
   * @param initialVersions
   */
  public VersionTracker(Map<Key, MutableInt> initialVersions) {
    versions = initialVersions;
  }

  /**
   * Sets the document version on the provided document.
   * 
   * @param doc
   */
  public void setDocumentVersion(StorableDocument doc) {
    String documentName = doc.getSequenceInfo().getName();
    DocumentType documentType = doc.getSequenceInfo().getDocumentType();
    NaturalisNote note = doc.getSequenceInfo().getNaturalisNote();
    setDocumentVersion(note, documentType, documentName);
  }

  /**
   * Sets the document version on the provided document.
   * 
   * @param doc
   */
  public void setDocumentVersion(StoredDocument doc) {
    String documentName = doc.getName();
    DocumentType documentType = doc.getType();
    NaturalisNote note = doc.getNaturalisNote();
    setDocumentVersion(note, documentType, documentName);
  }

  private void setDocumentVersion(NaturalisNote note, DocumentType docType, String docName) {
    if (StringUtils.isNotBlank(note.getDocumentVersion())) {
      logger.error("Document version already set for document %s. Overwriting forbidden", docName);
      return;
    }
    Key key = new Key(docType, docName);
    MutableInt version = versions.computeIfAbsent(key, k -> new MutableInt());
    version.increment();
    note.setDocumentVersion(version.intValue());
  }

}
