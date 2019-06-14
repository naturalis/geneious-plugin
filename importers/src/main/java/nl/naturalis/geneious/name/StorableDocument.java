package nl.naturalis.geneious.name;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.Note;

/**
 * A wrapper around the Geneious-native {@code AnnotatedPluginDocument} class along with a {@code SequenceInfo} object that will provide the
 * annotations for the document. The document itself has already been saved to the database, but the annotations are yet to be created and
 * saved.
 * 
 * @see StoredDocument
 */
public class StorableDocument {

  private final AnnotatedPluginDocument document;
  private final DocumentNotes notes;
  private final SequenceInfo sequenceInfo;

  public StorableDocument(AnnotatedPluginDocument doc, SequenceInfo info) {
    this.document = doc;
    this.notes = document.getDocumentNotes(true);
    this.sequenceInfo = info;
  }

  public StorableDocument(AnnotatedPluginDocument doc) {
    this.document = doc;
    this.notes = document.getDocumentNotes(true);
    this.sequenceInfo = new DefaultSequenceInfo(doc);
  }

  /**
   * Returns the {@code SequenceInfo} object containing the annotations for the Geneious document.
   * 
   * @return
   */
  public SequenceInfo getSequenceInfo() {
    return sequenceInfo;
  }

  /**
   * Returns the Geneious document.
   * 
   * @return
   */
  public AnnotatedPluginDocument getGeneiousDocument() {
    return document;
  }

  /**
   * Adds the annotations present in the provided note to this document, but does not save the document to the database.
   */
  public void attach(Note note) {
    note.copyTo(notes);
  }

  /**
   * Attaches the {@link NaturalisNote} to the Geneious document, but does not save the document to the database.
   */
  public void attachNaturalisNote() {
    sequenceInfo.getNaturalisNote().copyTo(notes);
  }

  /**
   * Saves the annotations to the database. Note that, although the {@link NaturalisNote} that is already present within this
   * {@code ImportableDocument} is the big Gorilla here, it is not the only source of annotations. For fasta documents, for example, we also
   * create the native-Geneious "Imported from" note.
   */
  public void saveAnnotations() {
    notes.saveNotes(true);
  }

  /**
   * Saves the entire document to the database. Required if setting attributes that are not part of the {@code DocumentNotes} (e.g. the
   * document's name).
   */
  public void save() {
    document.save(true);
  }

}
