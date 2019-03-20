package nl.naturalis.geneious.util;

import java.util.Date;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

public class DummySequenceDocument extends DefaultNucleotideSequence {

  public static final String DUMMY_SEQUENCE = "NNNNNNNNNN";
  public static final String DUMMY_PCR_PLATE_ID = "AA000";
  public static final String DUMMY_MARKER = "Dum";

  private final NaturalisNote note;

  // No-arg constructor required by Geneious framework.
  public DummySequenceDocument() {
    super();
    this.note = null;
  }

  public DummySequenceDocument(NaturalisNote note) {
    super(name(note), descr(note), DUMMY_SEQUENCE, new Date());
    this.note = note;
  }

  public AnnotatedPluginDocument wrap() {
    AnnotatedPluginDocument document = DocumentUtilities.createAnnotatedPluginDocument(this);
    note.castAndSet(NaturalisField.SEQ_PCR_PLATE_ID, DUMMY_PCR_PLATE_ID);
    note.castAndSet(NaturalisField.SEQ_MARKER, DUMMY_MARKER);
    note.saveTo(document);
    return document;
  }

  private static String name(NaturalisNote note) {
    return note.getExtractId() + " (dummy)";
  }

  private static String descr(NaturalisNote note) {
    return "Dummy sequence for extract ID " + note.getExtractId();
  }
}
