package nl.naturalis.geneious.seq;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import org.apache.commons.lang3.mutable.MutableInt;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.StoredDocumentComparator;

/**
 * Provides various types of lookups on a collection of Geneious documents, presumably fetched-and-cached using a
 * database query.
 */
class QueryCache {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(QueryCache.class);

  /**
   * A compound key consisting that can be used as a key for the query cache. The key consists of at least the document
   * type (dummy/fasta/ab1), along which you want to differentiate documents most of the time, plus an arbitrary other
   * property of a document.
   *
   * @author Ayco Holleman
   */
  static class Key {
    final DocumentType docType;
    final String field;
    final int hash;

    Key(DocumentType docType, String field) {
      Objects.requireNonNull(this.docType = docType, "Document type must not be null");
      Objects.requireNonNull(this.field = field, "ID must not be null");
      hash = (docType.ordinal() * 31) + field.hashCode();
    }

    /**
     * Creates a cache key using the extract ID of the document.
     * 
     * @param doc
     */
    Key(StoredDocument doc) {
      this(doc.getType(), doc.getNaturalisNote().getExtractId());
    }

    /**
     * Creates a cache key using the extract ID of the document.
     * 
     * @param doc
     */
    Key(ImportableDocument doc) {
      this(doc.getSequenceInfo().getDocumentType(), doc.getSequenceInfo().getNaturalisNote().getExtractId());
    }

    @Override
    public boolean equals(Object obj) {
      Key other = (Key) obj;
      return docType == other.docType && field.equals(other.field);
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }

  private final HashMap<Key, StoredDocument> cache;

  /**
   * Creates and populates a {@code QueryCache} for the specified documents using the extract ID as the main component the
   * cache key.
   * 
   * @param documents
   */
  QueryCache(Collection<AnnotatedPluginDocument> documents) {
    cache = new HashMap<>(documents.size(), 1F);
    documents.stream()
        .map(StoredDocument::new)
        .forEach(sd -> cache.merge(new Key(sd), sd, StoredDocumentComparator::chooseLatest));
  }

  /**
   * Return an {@code Optional} containing a dummy document with the specified extract ID or an empty {@code Optional} if
   * there is no such dummy document.
   * 
   * @param extractID
   * @return
   */
  Optional<StoredDocument> findDummy(String extractId) {
    return Optional.ofNullable(cache.get(new Key(DocumentType.DUMMY, extractId)));
  }

  /**
   * Transforms this cache into another cache that maps document names to (latest) version numbers.
   * 
   * @return
   */
  Map<Key, MutableInt> getLatestDocumentVersions() {
    HashMap<Key, MutableInt> versions = new HashMap<>();
    cache.entrySet().stream().filter(entry -> !entry.getValue().isDummy()).forEach(e -> {
      String version = e.getValue().getNaturalisNote().getDocumentVersion();
      if (version == null) {
        guiLogger.warn("Bad %s document. Extract ID is set (%s) but document version is not", e.getKey().docType, e.getKey().field);
      } else {
        String name = e.getValue().getGeneiousDocument().getName();
        versions.put(new Key(e.getKey().docType, name), new MutableInt(version));
      }
    });
    return versions;
  }

}
