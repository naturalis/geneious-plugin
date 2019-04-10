package nl.naturalis.geneious.seq;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import org.apache.commons.lang3.mutable.MutableInt;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.StoredDocument;
import nl.naturalis.geneious.util.StoredDocumentComparator;

/**
 * Provides various types of lookups on a collection of Geneious documents, presumably fetched-and-cached using a
 * database query.
 */
class QueryCache {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(QueryCache.class);

  static class Key {
    final DocumentType dt;
    final String id;
    final int hash;

    Key(DocumentType dt, String id) {
      Objects.requireNonNull(this.dt = dt, "Document type must not be null");
      Objects.requireNonNull(this.id = id, "ID must not be null");
      hash = (dt.ordinal() * 31) + id.hashCode();
    }

    Key(StoredDocument doc) {
      this(doc.getType(), doc.getNaturalisNote().getExtractId());
    }

    Key(ImportableDocument doc) {
      this(doc.getSequenceInfo().getDocumentType(), doc.getSequenceInfo().getNaturalisNote().getExtractId());
    }

    @Override
    public boolean equals(Object obj) {
      Key other = (Key) obj;
      return dt == other.dt && id.equals(other.id);
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }

  private final HashMap<Key, StoredDocument> cache;

  /**
   * Creates a new DocumentResultSetInspector for the specified documents.
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

  Map<Key, MutableInt> getLatestDocumentVersions() {
    HashMap<Key, MutableInt> versions = new HashMap<>();
    cache.forEach((key, sd) -> {
      String version = sd.getNaturalisNote().getDocumentVersion();
      if (version == null) {
        guiLogger.warn("Encountered corrupt %s document. Extract ID is set (%s) but document version is not", key.dt, key.id);
      } else {
        versions.put(key, new MutableInt(version));
      }
    });
    return versions;
  }

}
