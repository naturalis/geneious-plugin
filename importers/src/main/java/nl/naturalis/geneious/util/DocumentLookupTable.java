package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StoredDocument;

/**
 * An extension of {@code HashMap} that maps some property of a {@link StoredDocument} to a list of
 * {@code StoredDocument} instances sharing the same value for that property. For sample sheet imports the property will
 * the extract ID, for CRS import the CRS registration number, and for BOLD imports the combination of extract ID and
 * marker.
 *
 * @author Ayco Holleman
 */
public class DocumentLookupTable<K> extends HashMap<K, ArrayList<StoredDocument>> {

  /**
   * Creates a {@code StoredDocumentTable} using the provided {@code keyExtractor} function to extract the key under which
   * to store an {@code AnnotatedPluginDocument}. If the {@code keyExtractor} function returns null, the document from
   * which it was extracted will be ignored (i.e. not added to the map).
   * 
   * @param docs
   * @param keyExtractor
   */
  public DocumentLookupTable(Collection<AnnotatedPluginDocument> docs, Function<StoredDocument, K> keyExtractor) {
    super(docs.size(), 1F);
    docs.stream().map(StoredDocument::new).forEach(sd -> {
      K key = keyExtractor.apply(sd);
      if(key != null) {
        computeIfAbsent(key, (k) -> new ArrayList<>()).add(sd);
      }
    });
  }

}
