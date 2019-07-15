package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StoredDocument;

/**
 * Extension of {@code ArrayList} with {@link StoredDocument} as concrete type argument.
 * 
 * @author Ayco Holleman
 *
 */
public class StoredDocumentList extends ArrayList<StoredDocument> {

  public StoredDocumentList() {
    super(8);
  }

  public StoredDocumentList(int capacity) {
    super(capacity);
  }

  /**
   * Returns the {@code AnnotatedPluginDocument} instances wrapped into this list's {@code StoredDocument} instances.
   * 
   * @return
   */
  public List<AnnotatedPluginDocument> unwrap() {
    return stream().map(StoredDocument::getGeneiousDocument).collect(Collectors.toList());
  }

}
