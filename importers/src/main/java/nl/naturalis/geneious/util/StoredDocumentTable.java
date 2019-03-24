package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

public class StoredDocumentTable extends HashMap<String, StoredDocumentList> {

//  public StoredDocumentTable(AnnotatedPluginDocument[] docs) {
//    super(docs.length, 1F);
//    for (AnnotatedPluginDocument doc : docs) {
//      StoredDocument sd = new StoredDocument(doc);
//      String id = sd.getNaturalisNote().getExtractId();
//      computeIfAbsent(id, (k) -> new StoredDocumentList()).add(sd);
//    }
//    values().forEach(list -> Collections.sort(list, StoredDocumentComparator.INSTANCE));
//  }

  public StoredDocumentTable(Collection<AnnotatedPluginDocument> docs) {
    super(docs.size(), 1F);
    docs.stream().map(StoredDocument::new).forEach(sd -> {
      String id = sd.getNaturalisNote().getExtractId();
      computeIfAbsent(id, (k) -> new StoredDocumentList()).add(sd);
    });
    // for (AnnotatedPluginDocument doc : docs) {
    // StoredDocument sd = new StoredDocument(doc);
    // String id = sd.getNaturalisNote().getExtractId();
    // StoredDocumentList list = get(id);
    // if (list == null) {
    // list = new StoredDocumentList();
    // put(id, list);
    // }
    // list.add(sd);
    // }
    values().forEach(list -> Collections.sort(list, StoredDocumentComparator.INSTANCE));
  }

}
