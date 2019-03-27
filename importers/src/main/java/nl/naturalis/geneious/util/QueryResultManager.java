package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

/**
 * Provides various types of lookups on a collection of Geneious documents, presumably fetched-and-cached using a
 * database query.
 */
public class QueryResultManager {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(QueryResultManager.class);

  private final EnumMap<DocumentType, HashMap<String, StoredDocument>> byTypeByExtractId;

  /**
   * Creates a new DocumentResultSetInspector for the specified documents.
   * 
   * @param documents
   */
  public QueryResultManager(Collection<AnnotatedPluginDocument> documents) {
    this.byTypeByExtractId = new EnumMap<>(DocumentType.class);
    cacheDocuments(documents);
  }

  /**
   * Returns the latest version of the document with the specified extract ID and type, or an empty optional if this
   * combination does not exist yet in the database.
   * 
   * @param extractID
   * @param type
   * @return
   */
  public Optional<StoredDocument> find(String extractID, DocumentType type) {
    HashMap<String, StoredDocument> subcache = byTypeByExtractId.get(type);
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
  public Optional<StoredDocument> findDummy(String extractID) {
    return find(extractID, DocumentType.DUMMY);
  }

  private void cacheDocuments(Collection<AnnotatedPluginDocument> documents) {
    for (AnnotatedPluginDocument document : documents) {
      StoredDocument doc = new StoredDocument(document);
      String extractId = doc.getNaturalisNote().getExtractId();
      switch (doc.getType()) {
        case AB1:
        case FASTA:
        case DUMMY:
          byTypeByExtractId
              .computeIfAbsent(doc.getType(), (k) -> new HashMap<>())
              .merge(extractId, doc, StoredDocumentComparator::chooseLatest);
          break;
        case UNKNOWN:
        default:
          guiLogger.warn("Unexpected Geneious document type: %s (document ignored)", document.getDocumentClass());
      }
    }
  }

}
