package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

public class StoredDocumentTable extends HashMap<String, StoredDocumentList> {

  public StoredDocumentTable(Collection<AnnotatedPluginDocument> docs) {
    super(docs.size(), 1F);
    docs.stream().map(StoredDocument::new).forEach(sd -> {
      String id = sd.getNaturalisNote().getExtractId();
      computeIfAbsent(id, (k) -> new StoredDocumentList()).add(sd);
    });
    values().forEach(list -> Collections.sort(list, StoredDocumentComparator.INSTANCE));
  }

  public int documentCount() {
    return values().stream().mapToInt(StoredDocumentList::size).sum();
  }

}
