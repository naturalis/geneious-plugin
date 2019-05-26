package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StoredDocument;

public class StoredDocumentList extends ArrayList<StoredDocument> {

  public StoredDocumentList() {
    super(4);
  }

  public StoredDocumentList(int capacity) {
    super(capacity);
  }

  public List<AnnotatedPluginDocument> unwrap() {
    return stream().map(StoredDocument::getGeneiousDocument).collect(Collectors.toList());
  }

}
