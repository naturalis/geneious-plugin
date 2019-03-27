package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * An extension of {@code HashMap} mainly intended to map extract IDs to stored documents containing them, although the
 * keys could of course by any string property of those documents.
 *
 * @author Ayco Holleman
 */
public class StoredDocumentTable extends HashMap<String, StoredDocumentList> {

  public StoredDocumentTable(Collection<AnnotatedPluginDocument> docs) {
    super(docs.size(), 1F);
    docs.stream().map(StoredDocument::new).forEach(sd -> {
      String id = sd.getNaturalisNote().getExtractId();
      computeIfAbsent(id, (k) -> new StoredDocumentList()).add(sd);
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
