package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

public class APDList extends ArrayList<AnnotatedPluginDocument> {

  public APDList() {}

  public APDList(int initialCapacity) {
    super(initialCapacity);
  }

  public APDList(Collection<? extends AnnotatedPluginDocument> c) {
    super(c);
  }

}
