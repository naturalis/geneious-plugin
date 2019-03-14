package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.geneious.util.DocumentUtils.getDateModifield;

/**
 * Provides various types of lookups on a collection of Geneious documents (presumably fetched from the database).
 */
public class DocumentResultSetInspector {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentResultSetInspector.class);

  // Sort descending on document version or creation date
  private static Comparator<ImportedDocument> comparator = (doc1, doc2) -> {
    String v1 = doc1.getNaturalisNote().getDocumentVersion();
    String v2 = doc2.getNaturalisNote().getDocumentVersion();
    if (v1 == null) {
      if (v2 != null) {
        return -1; // Prefer anything over null
      }
    }
    if (v2 == null) {
      return 1;
    }
    int i1 = Integer.parseInt(v1.toString());
    int i2 = Integer.parseInt(v2.toString());
    if (i1 == i2) {
      return getDateModifield(doc2.getGeneiousDocument()).compareTo(getDateModifield(doc1.getGeneiousDocument()));
    }
    return i2 - i1;
  };

  private final EnumMap<DocumentType, HashMap<String, ImportedDocument>> byTypeByExtractId;

  /**
   * Creates a new DocumentResultSetInspector for the specified documents.
   * 
   * @param documents
   */
  public DocumentResultSetInspector(Collection<AnnotatedPluginDocument> documents) {
    this.byTypeByExtractId = new EnumMap<>(DocumentType.class);
    cacheDocuments(documents);
  }

  /**
   * Returns the latest version of the document with the specified extract ID and type, or an empty optional if this combination does not
   * exist yet in the database.
   * 
   * @param extractID
   * @param type
   * @return
   */
  public Optional<ImportedDocument> findLatestVersion(String extractID, DocumentType type) {
    HashMap<String, ImportedDocument> subcache = byTypeByExtractId.get(type);
    if (subcache != null) {
      return Optional.ofNullable(subcache.get(extractID));
    }
    return Optional.empty();
  }

  /**
   * Convenience method, equivalent to calling {@code findLatestVersion(extractID, DocumentType.DUMMY)}.
   * 
   * @param extractID
   * @return
   */
  public Optional<ImportedDocument> findDummy(String extractID) {
    return findLatestVersion(extractID, DocumentType.DUMMY);
  }

  private void cacheDocuments(Collection<AnnotatedPluginDocument> documents) {
    for (AnnotatedPluginDocument document : documents) {
      ImportedDocument doc = new ImportedDocument(document);
      String extractId = doc.getNaturalisNote().getExtractId();
      switch (doc.getType()) {
        case AB1:
        case FASTA:
        case DUMMY:
          byTypeByExtractId
              .computeIfAbsent(doc.getType(), (k) -> new HashMap<>())
              .merge(extractId, doc, (d1, d2) -> comparator.compare(d1, d2) < 0 ? d2 : d1);
          break;
        case UNKNOWN:
        default:
          guiLogger.warn("Unexpected Geneious document type: %s (document ignored)", document.getDocumentClass());
      }
    }
  }

}
