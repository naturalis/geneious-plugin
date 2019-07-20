package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StoredDocument;

/**
 * Caches documents, either selected by the user or retrieved through a database query, in a {@code HashMap} for fast
 * lookups as an importer iterates over the rows in a CSV or file or spreadsheet.
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
