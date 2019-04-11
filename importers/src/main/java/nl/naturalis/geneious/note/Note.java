package nl.naturalis.geneious.note;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

public interface Note {
  
  void attachTo(DocumentNotes notes);

}
