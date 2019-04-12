package nl.naturalis.geneious.seq;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.Note;
import nl.naturalis.geneious.util.StoredDocument;

/**
 * A simple combination of an {@link AnnotatedPluginDocument} and a {@code SequenceInfo} object that will be used to
 * supply the annotations for it. The Geneious document has not yet been saved to the database, but is about to be.
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

  void attach(Note note) {
    note.attachTo(notes);
  }

  /**
   * Attaches the {@link NaturalisNote} to the Geneious document.
   */
  void attachNaturalisNote() {
    sequenceInfo.getNaturalisNote().attachTo(notes);
  }

  /**
   * Saves all annotations accrued during a AB1/Fasta import session to the database. Note that, although the
   * {@link NaturalisNote} that is already present within this {@code ImportableDocument} is the greatest contributor of
   * document notes, it is not the only one. For example for fasta document we also create the native-Geneious "Imported
   * from" note.
   */
  void saveAnnotations() {
    notes.saveNotes(false);
  }

}
