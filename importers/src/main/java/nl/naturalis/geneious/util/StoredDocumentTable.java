package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * An extension of {@code HashMap} that maps some property of a {@code StoredDocument} to a list of
 * {@code StoredDocument} instances sharing the same value for that property.
 *
 * @author Ayco Holleman
 */
public class StoredDocumentTable extends HashMap<String, StoredDocumentList> {

  /**
   * Creates a {@code StoredDocumentTable} using extract IDs as keys.
   * 
   * @param docs
   */
  public StoredDocumentTable(Collection<AnnotatedPluginDocument> docs) {
    this(docs, sd -> sd.getNaturalisNote().getExtractId());
  }

  public StoredDocumentTable(Collection<AnnotatedPluginDocument> docs, Function<StoredDocument, String> keyExtractor) {
    super(docs.size(), 1F);
    docs.stream().map(StoredDocument::new).forEach(sd -> {
      String key = keyExtractor.apply(sd);
      if (key != null) {
        computeIfAbsent(key, (k) -> new StoredDocumentList()).add(sd);
      }
    });
    values().forEach(list -> Collections.sort(list, StoredDocumentComparator.INSTANCE));
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
