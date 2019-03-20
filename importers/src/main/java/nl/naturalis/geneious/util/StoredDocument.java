package nl.naturalis.geneious.util;

import java.util.Comparator;
import java.util.Objects;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;
import static nl.naturalis.geneious.DocumentType.UNKNOWN;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;

/**
 * A simple combination of an instance of Geneious's own {@code AnnotatedPluginDocument} class and an instance of a {@link NaturalisNote}
 * extracted from the Geneious document. The document has been retrieved from the database; in other words it's not an about-to-be-imported
 * document. Since it is not clear whether extracting the note implicitly involves a database query everytime you do it, we err on the
 * cautious side and chain the two together (immediately and just once) within this class.
 */
public class StoredDocument {

  /**
   * Compares 2 Geneious documents using their URN.
   */
  public static Comparator<StoredDocument> URN_COMPARATOR = (o1, o2) -> {
    return o1.doc.getURN().toString().compareTo(o2.doc.getURN().toString());
  };

  private final AnnotatedPluginDocument doc;
  private final NaturalisNote note;
  private final DocumentType type;

  public StoredDocument(AnnotatedPluginDocument document) {
    this(document, new NaturalisNote(document));
  }

  public StoredDocument(AnnotatedPluginDocument doc, NaturalisNote note) {
    this.doc = doc;
    this.note = note;
    this.type = type();
  }

  public AnnotatedPluginDocument getGeneiousDocument() {
    return doc;
  }

  public NaturalisNote getNaturalisNote() {
    return note;
  }

  public void save() {
    doc.save();
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

}
