package nl.naturalis.geneious.util;

import java.util.Comparator;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import static nl.naturalis.geneious.util.DocumentUtils.getDateModifield;

/**
 * Sorts AnnotatedPluginDocument instances by document version descending.
 *
 * @author Ayco Holleman
 */
public class StoredDocumentComparator implements Comparator<StoredDocument> {

  public static final StoredDocumentComparator INSTANCE = new StoredDocumentComparator();

  public static StoredDocument chooseLatest(StoredDocument doc1, StoredDocument doc2) {
    return INSTANCE.compare(doc1, doc2) < 0 ? doc2 : doc1;
  }

  private StoredDocumentComparator() {}

  @Override
  public int compare(StoredDocument doc1, StoredDocument doc2) {
    AnnotatedPluginDocument apd1 = doc1.getGeneiousDocument();
    AnnotatedPluginDocument apd2 = doc2.getGeneiousDocument();
    Integer v1 = Integer.valueOf(doc1.getNaturalisNote().getDocumentVersion());
    Integer v2 = Integer.valueOf(doc2.getNaturalisNote().getDocumentVersion());
    if (v1 == null) {
      if (v2 != null) {
        return -1; // Prefer anything over null
      }
    }
    if (v2 == null) {
      return 1;
    }
    int i = v2.compareTo(v1);
    if (i == 0) {
      return getDateModifield(apd1).compareTo(getDateModifield(apd2));
    }
    return i;
  }

}
