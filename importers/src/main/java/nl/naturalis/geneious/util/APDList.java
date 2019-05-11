package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * A subclass of {@code ArrayList} with {@code AnnotatedPluginDocument} as concrete type argument. The subclass adds or overrides no
 * functionality, but it makes the code look less bloated.
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
