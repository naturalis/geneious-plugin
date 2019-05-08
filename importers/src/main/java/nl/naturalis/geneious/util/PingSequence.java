package nl.naturalis.geneious.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.note.NaturalisNote;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;

import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabase;

/**
 * An extension of Geneious's {@code DefaultNucleotideSequence} class solely meant to create dummy documents. The dummy documents will be
 * removed as soon as the real fasta and AB1 sequences are imported, providing them with the annotations saved to the dummy document.
 *
 * @author Ayco Holleman
 */
public class PingSequence extends DefaultNucleotideSequence {

  /**
   * The nucleotide sequence used for all ping documents: "AAAAAAAAAA"
   */
  public static final String PING_SEQUENCE = "AAAAAAAAAA";
  /**
   * The marker used for all documents: "Ping"
   */
  public static final String DUMMY_MARKER = "Ping";

  private final String pingValue;

  /**
   * No-arg constructor, required by Geneious framework, but it seems we can rely on the other constructor being called when it matters.
   */
  public PingSequence() {
    this("shouldn't happen");
  }

  /**
   * No-arg constructor, required by Geneious framework, but it seems we can rely on the other constructor being called when it matters.
   */
  public PingSequence(String pingValue) {
    super(pingValue, "", PING_SEQUENCE, new Date());
    this.pingValue = pingValue;
  }

  /**
   * Wraps the sequence into a {@code StoredDoucment}.
   * 
   * @return
   */
  public void save() {
    AnnotatedPluginDocument apd = DocumentUtilities.createAnnotatedPluginDocument(this);
    NaturalisNote note = new NaturalisNote();
    note.setDocumentVersion(0);
    note.castAndSet(SEQ_MARKER, DUMMY_MARKER);
    note.castAndSet(SEQ_EXTRACT_ID, pingValue);
    DocumentNotes notes = apd.getDocumentNotes(true);
    note.copyTo(notes);
    notes.saveNotes(true);
    List<AnnotatedPluginDocument> apds = addAndReturnGeneratedDocuments(
        Arrays.asList(apd),
        false,
        Collections.emptyList(),
        getTargetDatabase());
    if (apds.size() != 1) {
      throw new NaturalisPluginException("Error while saving ping value");
    }
  }

}
