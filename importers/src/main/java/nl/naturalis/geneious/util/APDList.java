package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * A subclass of {@code ArrayList} with {@code AnnotatedPluginDocument} as type argument. Provides no extra
 * functionality, but it makes the code look less bloated.
 *
 * @author Ayco Holleman
 */
class APDList extends ArrayList<AnnotatedPluginDocument> {

  static APDList emptyList() {
    return new APDList(0);
  }

  APDList() {
    super();
  }

  APDList(int initialCapacity) {
    super(initialCapacity);
  }

  APDList(Collection<? extends AnnotatedPluginDocument> c) {
    super(c);
  }

}
