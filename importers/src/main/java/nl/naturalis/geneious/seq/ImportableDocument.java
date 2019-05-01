package nl.naturalis.geneious.seq;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.Note;

/**
 * A wrapper around the Geneious-native {@code AnnotatedPluginDocument} class along with a {@code SequenceInfo} object
 * that will provide the annotations for the document. The document itself has already been saved to the database, but
 * the annotations are yet to be created and saved.
 * 
 * @see StoredDocument
 */
class ImportableDocument {

  private final SequenceInfo sequenceInfo;
  private final AnnotatedPluginDocument document;
  private final DocumentNotes notes;

  ImportableDocument(AnnotatedPluginDocument doc, SequenceInfo info) {
    this.sequenceInfo = info;
    this.document = doc;
    this.notes = document.getDocumentNotes(true);
  }

  /**
   * Returns the {@code SequenceInfo} object containing the annotations for the Geneious document.
   * 
   * @return
   */
  SequenceInfo getSequenceInfo() {
    return sequenceInfo;
  }

  /**
   * Returns the Geneious document.
   * 
   * @return
   */
  AnnotatedPluginDocument getGeneiousDocument() {
    return document;
  }

  /**
   * Adds the annotations present in the provided note to this document, but does not save the document to the database.
   */
  void attach(Note note) {
    note.copyTo(notes);
  }

  /**
   * Attaches the {@link NaturalisNote} to the Geneious document, but does not save the document to the database.
   */
  void attachNaturalisNote() {
    sequenceInfo.getNaturalisNote().copyTo(notes);
  }

  /**
   * Saves the annotations to the database. Note that, although the {@link NaturalisNote} that is already present within
   * this {@code ImportableDocument} is the big Gorilla here, it is not the only source of annotations. For fasta
   * documents, for example, we also create the native-Geneious "Imported from" note.
   */
  void saveAnnotations() {
    notes.saveNotes(false);
  }

}
