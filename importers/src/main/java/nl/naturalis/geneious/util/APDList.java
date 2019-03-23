package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * Extesion of oft-used @code ArrayList} just to make code somewhat less verbose.
 *
 * @author Ayco Holleman
 */
public class APDList extends ArrayList<AnnotatedPluginDocument> {

  public static APDList emptyList() {
    return new APDList(0);
  }

  public APDList() {
    super();
  }

  public APDList(int initialCapacity) {
    super(initialCapacity);
  }

  public APDList(Collection<? extends AnnotatedPluginDocument> c) {
    super(c);
  }

  public APDList and(APDList other) {
    addAll(other);
    return this;
  }

}
