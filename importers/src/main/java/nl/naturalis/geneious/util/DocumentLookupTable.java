package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StoredDocument;

/**
 * A lookup table for documents selected by the user or documents retrieved through a database query.
 *
 * @author Ayco Holleman
 */
public class DocumentLookupTable<K> extends HashMap<K, ArrayList<StoredDocument>> {

  /**
   * Creates a new lookup using the provided {@code keyExtractor} function to extract the key from the document to be inserted into the
   * lookup table. If the {@code keyExtractor} function returns null, the document will not be inserted into the lookup table.
   * 
   * @param docs
   * @param keyExtractor
   */
  public DocumentLookupTable(Collection<AnnotatedPluginDocument> docs, Function<StoredDocument, K> keyExtractor) {
    super(docs.size(), 1F);
    docs.stream().map(StoredDocument::new).forEach(sd -> {
      K key = keyExtractor.apply(sd);
      if (key != null) {
        computeIfAbsent(key, (k) -> new ArrayList<>()).add(sd);
      }
    });
  }

}
