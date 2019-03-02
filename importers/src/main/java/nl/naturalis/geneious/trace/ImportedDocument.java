package nl.naturalis.geneious.trace;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.DocumentManager;

class ImportedDocument {

  private final SequenceInfo info;
  private final AnnotatedPluginDocument doc;

  ImportedDocument(SequenceInfo info, AnnotatedPluginDocument doc) {
    this.info = info;
    this.doc = doc;
  }

  SequenceInfo getSequenceInfo() {
    return info;
  }

  AnnotatedPluginDocument getDocument() {
    return doc;
  }

  void annotate(DocumentManager dm) {
    NaturalisNote note = info.getNote();
    info.getNote().overwrite(doc);
  }

}
