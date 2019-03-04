package nl.naturalis.geneious.util;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.DocumentType.AB1;
import static nl.naturalis.geneious.DocumentType.DUMMY;
import static nl.naturalis.geneious.DocumentType.FASTA;
import static nl.naturalis.geneious.DocumentType.UNKNOWN;

/**
 * A simple combination of an instance of Geneious's own {@code AnnotatedPluginDocument} class and an instance of a {@code NaturalisNote}
 * extracted from the Geneious document. The Geneious document is generally obtained through some sort of database query; in other words it
 * has already been imported in some previous run of the plugin (or Geneious's own import functionality). Since it is not clear whether
 * extracting the note implicitly involves a database query everytime you do it, we err on the cautious side and chain the two together
 * (immediately and just once) within this class.
 */
public class ImportedDocument {

  private final AnnotatedPluginDocument doc;
  private final NaturalisNote note;

  ImportedDocument(AnnotatedPluginDocument doc, NaturalisNote note) {
    this.doc = doc;
    this.note = note;
  }

  ImportedDocument(AnnotatedPluginDocument doc) {
    this.doc = doc;
    this.note = new NaturalisNote(doc);
  }

  public AnnotatedPluginDocument getGeneiousDocument() {
    return doc;
  }

  public NaturalisNote getNaturalisNote() {
    return note;
  }

  public DocumentType getType() {
    if (note.getMarker() != null && note.getMarker().equals("Dum")) {
      return DUMMY;
    }
    if (doc.getDocumentClass() == AB1.getGeneiousType()) {
      return AB1;
    }
    if (doc.getDocumentClass() == FASTA.getGeneiousType()) {
      return FASTA;
    }
    return UNKNOWN;
  }

}
