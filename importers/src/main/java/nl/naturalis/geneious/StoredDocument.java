package nl.naturalis.geneious;

import java.util.Comparator;
import java.util.Objects;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.Note;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;
import static nl.naturalis.geneious.DocumentType.UNKNOWN;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;

/**
 * A wrapper around the Geneious-native {@code AnnotatedPluginDocument} class with all Naturalis-specific annotations
 * pre-fetched into a {@link NaturalisNote} extracted from the Geneious document.
 */
public class StoredDocument {

  /**
   * Compares 2 {@code StoredDocument} instances using their URN.
   */
  public static Comparator<StoredDocument> URN_COMPARATOR = (o1, o2) -> {
    return o1.doc.getURN().toString().compareTo(o2.doc.getURN().toString());
  };

  private final AnnotatedPluginDocument doc;
  private final NaturalisNote note;
  private final DocumentType type;

  private DocumentNotes notes;

  public StoredDocument(AnnotatedPluginDocument document) {
    this(document, new NaturalisNote(document));
  }

  public StoredDocument(AnnotatedPluginDocument doc, NaturalisNote note) {
    this.doc = doc;
    this.note = note;
    this.type = type();
  }

  /**
   * Returns the Geneious-native document instance.
   * 
   * @return
   */
  public AnnotatedPluginDocument getGeneiousDocument() {
    return doc;
  }

  /**
   * Returns the annotations associated with the Geneious document.
   * 
   * @return
   */
  public NaturalisNote getNaturalisNote() {
    return note;
  }

  /**
   * Attaches the provided annotation to the Geneious document, but does not save the annotation to the database.
   */
  public void attach(Note note) {
    note.copyTo(getDocumentNotes());
  }

  /**
   * Merges the provided annotations with the annotations already present on the document. Returns {@code true} if the
   * document actually changed as a consequence, {@code false} otherwise.
   */
  public boolean attach(NaturalisNote note) {
    return note.copyTo(this.note);
  }

  /**
   * Saves all annotations added via {@code attach} and {@code attachNaturalisNote} to the database.
   */
  public void saveAnnotations() {
    note.copyTo(getDocumentNotes());
    getDocumentNotes().saveNotes(true);
  }

  public DocumentType getType() {
    return type;
  }

  public boolean isAB1() {
    return type == AB1;
  }

  public boolean isFasta() {
    return type == FASTA;
  }

  public boolean isDummy() {
    return type == DUMMY;
  }

  private DocumentType type() {
    DocumentType t;
    if (Objects.equals(note.get(SEQ_MARKER), "Dum")) {
      t = DUMMY;
    } else if (doc.getDocumentClass() == AB1.getGeneiousType()) {
      t = AB1;
    } else if (doc.getDocumentClass() == FASTA.getGeneiousType()) {
      t = FASTA;
    } else {
      t = UNKNOWN;
    }
    return t;
  }

  private DocumentNotes getDocumentNotes() {
    if (notes == null) {
      notes = doc.getDocumentNotes(true);
    }
    return notes;
  }

}
