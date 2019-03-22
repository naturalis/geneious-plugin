package nl.naturalis.geneious.util;

import java.util.Collections;
import java.util.HashMap;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

public class StoredDocumentLookupTable extends HashMap<String, StoredDocumentList> {

  public StoredDocumentLookupTable(AnnotatedPluginDocument[] docs) {
    super(docs.length, 1F);
    for (AnnotatedPluginDocument doc : docs) {
      StoredDocument sd = new StoredDocument(doc);
      String id = sd.getNaturalisNote().getExtractId();
      computeIfAbsent(id, (k) -> new StoredDocumentList()).add(sd);
    }
    values().forEach(list -> Collections.sort(list, StoredDocumentComparator.INSTANCE));
  }

}
