package nl.naturalis.geneious.util;

import java.util.Comparator;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;

import static com.google.common.base.Preconditions.checkArgument;

import static nl.naturalis.geneious.util.DocumentUtils.getDateModifield;

/**
 * Sorts StoredDocument instances according to the document version (descending). If the document versions of two
 * documents are the same, a warning is issued (should never be the case) and they are sorted according to their
 * last-modified date (descending). The {@code compare} method will throw an {@code IllegalArgumentException} if you
 * attempt to compare different types of documents (e.g. AB1 with fasta).
 *
 * @author Ayco Holleman
 */
public class StoredDocumentComparator implements Comparator<StoredDocument> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(StoredDocumentComparator.class);

  public static final StoredDocumentComparator INSTANCE = new StoredDocumentComparator();

  public static StoredDocument chooseLatest(StoredDocument doc1, StoredDocument doc2) {
    return INSTANCE.compare(doc1, doc2) < 0 ? doc1 : doc2;
  }

  private StoredDocumentComparator() {}

  @Override
  public int compare(StoredDocument doc1, StoredDocument doc2) {
    checkArgument(doc1.getType() == doc2.getType(), "Cannot compare %s document with %s document", doc1.getType(), doc2.getType());
    AnnotatedPluginDocument apd1 = doc1.getGeneiousDocument();
    AnnotatedPluginDocument apd2 = doc2.getGeneiousDocument();
    Integer v1 = Integer.valueOf(doc1.getNaturalisNote().getDocumentVersion());
    Integer v2 = Integer.valueOf(doc2.getNaturalisNote().getDocumentVersion());
    v1 = v1 == null ? Integer.MIN_VALUE : v1; // nulls last when in descending order
    v2 = v2 == null ? Integer.MIN_VALUE : v2;
    int i = v2.compareTo(v1);
    if (i == 0) {
      if (v1 != Integer.MIN_VALUE) {
        String fmt = "Encountered two %s documents with same extract ID (%s) and same document version (%s)";
        guiLogger.warn(fmt, doc1.getType(), doc1.getNaturalisNote().getExtractId(), v1);
      }
      i = getDateModifield(apd2).compareTo(getDateModifield(apd1));
      if (i == 0) {
        i = apd2.getRevisionNumber() - apd1.getRevisionNumber();
      }
    }
    return i;
  }

}
