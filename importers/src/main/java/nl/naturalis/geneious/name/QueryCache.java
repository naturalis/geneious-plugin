package nl.naturalis.geneious.name;

import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.name.NameUtil.removeKnownSuffixes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.mutable.MutableInt;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.fasterxml.jackson.annotation.JsonValue;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.JsonUtil;

/**
 * Provides various types of lookups on a collection of Geneious documents, presumably fetched-and-cached using a
 * database query.
 */
class QueryCache {

  private static final GuiLogger logger = GuiLogManager.getLogger(QueryCache.class);

  /**
   * A compound key that is likely to be useful as a key for the query cache. The key consists of at least the document
   * type (dummy/fasta/ab1) plus an arbitrary other property of the document.
   *
   * @author Ayco Holleman
   */
  static class Key {
    final DocumentType docType;
    final Object value;
    final int hash;

    Key(DocumentType docType, Object value) {
      Objects.requireNonNull(this.docType = docType, "Document type must not be null");
      Objects.requireNonNull(this.value = value, "Value must not be null");
      hash = (docType.ordinal() * 31) + value.hashCode();
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
    Key(StorableDocument doc) {
      this(doc, doc.getSequenceInfo().getNaturalisNote().getExtractId());
    }

    /**
     * Creates a cache key using the provided document's type and the provided value (presumably retrieved from the same
     * document).
     * 
     * @param doc
     * @param val
     */
    Key(StorableDocument doc, Object val) {
      this(doc.getSequenceInfo().getDocumentType(), val);
    }

    @Override
    public boolean equals(Object obj) {
      Key other = (Key) obj;
      return docType == other.docType && value.equals(other.value);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @JsonValue
    @Override
    public String toString() {
      return value + " (" + docType + ")";
    }
  }

  private final HashMap<Key, ArrayList<StoredDocument>> cache;

  /**
   * Creates and populates a {@code QueryCache} for the specified documents using the extract ID as the main component the
   * cache key.
   * 
   * @param documents
   */
  QueryCache(Collection<AnnotatedPluginDocument> documents) {
    cache = new HashMap<>(documents.size(), 1F);
    for(AnnotatedPluginDocument doc : documents) {
      StoredDocument sd = new StoredDocument(doc);
      Key key = new Key(sd);
      ArrayList<StoredDocument> sds = cache.get(key);
      if(sds == null) {
        sds = new ArrayList<StoredDocument>(8);
        cache.put(key, sds);
      }
      sds.add(sd);
    }
  }

  /**
   * Returns a list of dummy documents with the provided extract ID. Note that there SHOULD never be more than one dummy
   * document for any given extract ID. However, to make the application more robust in the face of data corruption, we
   * allow for that to be not the case. By returning a list of dummy documents rather than a single one, we allow the
   * application to recover from data corruption (e.g. by deleting all but one of them).
   * 
   * @param extractID
   * @return
   */
  List<StoredDocument> findDummy(String extractId) {
    return cache.get(new Key(DUMMY, extractId));
  }

  /**
   * Transforms this cache into another cache that maps document names to (latest) version numbers.
   * 
   * @return
   */
  Map<Key, MutableInt> getLatestDocumentVersions() {
    HashMap<Key, MutableInt> versions = new HashMap<>();
    for(Key key : cache.keySet()) {
      List<StoredDocument> sds = cache.get(key);
      for(StoredDocument sd : sds) {
        if(sd.isDummy()) {
          continue;
        }
        String name = removeKnownSuffixes(sd.getGeneiousDocument().getName());
        String version = sd.getNaturalisNote().getDocumentVersion();
        if(version == null) {
          /*
           * If the document has any Naturalis annotation (and we know it has one b/c we queried on extract ID), then it must also
           * have the document version annotation.
           */
          logger.error("Corrupt %s document: %s. Missing document version", key.docType, name);
          continue;
        }
        Key newKey = new Key(sd.getType(), name);
        MutableInt mi1 = new MutableInt(version);
        MutableInt mi2 = versions.get(newKey);
        if(mi2 == null) {
          versions.put(newKey, mi1);
        } else if(mi1.intValue() > mi2.intValue()) {
          mi2.setValue(mi1.intValue());
        } else if(mi1.intValue() == mi2.intValue()) {
          String fmt = "Corrupt %s documents: same name (%s) and same document version (%s)";
          logger.error(fmt, key.docType, name, version);
        }
      }
    }
    return versions;
  }

  @Override
  public String toString() {
    return JsonUtil.toPrettyJson(cache);
  }

}
