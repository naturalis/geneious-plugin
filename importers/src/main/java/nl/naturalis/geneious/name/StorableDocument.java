package nl.naturalis.geneious.name;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.Note;

/**
 * A wrapper around the {@code AnnotatedPluginDocument} class along with a {@code SequenceInfo} object that will provide the annotations for
 * the document. The document has typically just been created by the plugin itself (i.e. the AB1/Fasta Import operation) and the annotations
 * for it have yet to be created and attached. Contrast this with the {@link StoredDocument} class, which is also a wrapper around
 * {@code AnnotatedPluginDocument} class, but here the document already resided in the database and was retrieved through some sort of
 * query.
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
