package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * A simple extension of {@code ArrayList} that does not add any new functionality, but has a concrete type argument
 * {@code AnnotatedPluginDocument}. Lists if this type of objects are used so often throughout the code, that having
 * this subclass makes the code quite a bit less verbose.
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


}
