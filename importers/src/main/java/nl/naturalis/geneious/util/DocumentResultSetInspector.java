package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

/**
 * Provides various types of lookups on a collection of Geneious documents (presumably fetched from the database).
 */
public class DocumentResultSetInspector {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentResultSetInspector.class);

  // Sorts descending on document version
  private static Comparator<ImportedDocument> versionComparator = (v1, v2) -> {
    if (v1.getNaturalisNote().getDocumentVersion() == null) {
      if (v2.getNaturalisNote().getDocumentVersion() == null) {
        return 0;
      }
      return -1;
    }
    if (v2.getNaturalisNote().getDocumentVersion() == null) {
      return 1;
    }
    return v2.getNaturalisNote().getDocumentVersion() - v1.getNaturalisNote().getDocumentVersion();
  };

  private final EnumMap<DocumentType, HashMap<String, TreeSet<ImportedDocument>>> byTypeByExtractId;

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
  public Optional<ImportedDocument> getLatestVersion(String extractID, DocumentType type) {
    Map<String, TreeSet<ImportedDocument>> subcache = byTypeByExtractId.get(type);
    if (subcache != null) {
      TreeSet<ImportedDocument> values = subcache.get(extractID);
      if (values != null) {
        return Optional.of(values.first());
      }
    }
    return Optional.empty();
  }

  /**
   * Convenience method, equivalent to calling {@code getDocumentWithHighestVersion(extractID, DocumentType.DUMMY)}.
   * 
   * @param extractID
   * @return
   */
  public Optional<ImportedDocument> getDummy(String extractID) {
    return getLatestVersion(extractID, DocumentType.DUMMY);
  }

  private void cacheDocuments(Collection<AnnotatedPluginDocument> documents) {
    for (AnnotatedPluginDocument document : documents) {
      ImportedDocument doc = new ImportedDocument(document);
      switch (doc.getType()) {
        case AB1:
        case FASTA:
        case DUMMY:
          cacheDocument(doc);
          break;
        case UNKNOWN:
        default:
          guiLogger.warn("Unexpected Geneious document type: %s (document ignored!)", document.getDocumentClass());
      }
    }
  }

  private void cacheDocument(ImportedDocument doc) {
    byTypeByExtractId
        .computeIfAbsent(doc.getType(), (k) -> new HashMap<>())
        .computeIfAbsent(doc.getNaturalisNote().getExtractId(), (k) -> new TreeSet<>(versionComparator))
        .add(doc);
  }

}
