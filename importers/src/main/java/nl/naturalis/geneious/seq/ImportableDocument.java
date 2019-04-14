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
   * Attaches the provided annotation to the Geneious document, but does not save the annotation to the database.
   */
  void attach(Note note) {
    note.copyTo(notes);
  }

  /**
   * Attaches the internal {@link NaturalisNote} to the Geneious document, but does not save the annotations to the
   * database.
   */
  void attachNaturalisNote() {
    sequenceInfo.getNaturalisNote().copyTo(notes);
  }

  /**
   * Saves all annotations added via {@code attach} and {@code attachNaturalisNote} to the database. Note that, although
   * the {@link NaturalisNote} that is already present within this {@code ImportableDocument} is the greatest contributor
   * of document notes, it is not the only one. For example for fasta document we also create the native-Geneious
   * "Imported from" note.
   */
  void saveAnnotations() {
    notes.saveNotes(false);
  }

}
