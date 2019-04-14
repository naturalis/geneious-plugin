package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StoredDocument;

/**
 * An extension of {@code HashMap} that maps some property of a {@code StoredDocument} to a list of
 * {@code StoredDocument} instances sharing the same value for that property. For sample sheet imports the property will
 * typically be the extract ID, for CRS import the CRS registration number, and for BOLD imports the combination of
 * extract ID and marker.
 *
 * @author Ayco Holleman
 */
public class StoredDocumentTable<K> extends HashMap<K, StoredDocumentList> {

  public StoredDocumentTable(Collection<AnnotatedPluginDocument> docs, Function<StoredDocument, K> keyExtractor) {
    super(docs.size(), 1F);
    docs.stream().map(StoredDocument::new).forEach(sd -> {
      K key = keyExtractor.apply(sd);
      if (key != null) {
        computeIfAbsent(key, (k) -> new StoredDocumentList()).add(sd);
      }
    });
  }

  /**
   * Returns the total number of documents in this table.
   * 
   * @return
   */
  public int documentCount() {
    return values().stream().mapToInt(StoredDocumentList::size).sum();
  }


}
