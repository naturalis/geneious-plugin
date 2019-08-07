package nl.naturalis.geneious.smpl;

import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PCR_PLATE_ID;

import java.util.Date;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * An extension of Geneious's {@code DefaultNucleotideSequence} class solely meant for the creation of dummy documents. The dummy
 * documents will be removed as soon as the real fasta and AB1 sequences are imported into Geneious, providing them with the
 * annotations saved to the dummy document.
 *
 * @author Ayco Holleman
 */
public class DummySequence extends DefaultNucleotideSequence {

  /**
   * The nucleotide sequence used for all dummy documents: "NNNNNNNNNN"
   */
  public static final String DUMMY_SEQUENCE = "NNNNNNNNNN";
  /**
   * The plate ID used for all dummy documents: "AA000"
   */
  public static final String DUMMY_PCR_PLATE_ID = "AA000";
  /**
   * The marker used for all dummy documents: "Dum"
   */
  public static final String DUMMY_MARKER = "Dum";

  private final NaturalisNote note;

  /**
   * No-arg constructor, required by Geneious framework, but it seems we can rely on the other constructor being called
   * when it matters.
   */
  public DummySequence() {
    super();
    this.note = null;
  }

  /**
   * Creates a dummy sequence with the specified annotations.
   * 
   * @param note
   */
  public DummySequence(NaturalisNote note) {
    super(name(note), "", DUMMY_SEQUENCE, new Date());
    this.note = note;
  }

  /**
   * Wraps the sequence into a {@code StoredDoucment}.
   * 
   * @return
   */
  public StoredDocument wrap() {
    AnnotatedPluginDocument apd = DocumentUtilities.createAnnotatedPluginDocument(this);
    note.setDocumentVersion(0);
    if (note.get(SEQ_PCR_PLATE_ID) == null) {
      note.castAndSet(SEQ_PCR_PLATE_ID, DUMMY_PCR_PLATE_ID);
    }
    if (note.get(SEQ_MARKER) == null) {
      note.castAndSet(SEQ_MARKER, DUMMY_MARKER);
    }
    return new StoredDocument(apd, note);
  }

  private static String name(NaturalisNote note) {
    return note.getExtractId() + " (dummy)";
  }

}
