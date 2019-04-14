package nl.naturalis.geneious.util;

import java.util.ArrayList;

import nl.naturalis.geneious.StoredDocument;

public class StoredDocumentList extends ArrayList<StoredDocument> {

  public StoredDocumentList() {
    super(4);
  }

  public StoredDocumentList(int capacity) {
    super(capacity);
  }

}
