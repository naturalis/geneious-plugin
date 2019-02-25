package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import static com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

public class DocumentResultSetInspector {

  private final Collection<AnnotatedPluginDocument> documents;

  private HashMap<String, ArrayList<AnnotatedPluginDocument>> docsByExtractId;

  public DocumentResultSetInspector(Collection<AnnotatedPluginDocument> documents) {
    this.documents = documents;
  }

  public List<AnnotatedPluginDocument> getDummies() {
    List<AnnotatedPluginDocument> dummies = new ArrayList<>(documents.size());
    for (AnnotatedPluginDocument doc : documents) {
    }
    return dummies;
  }

}
