package nl.naturalis.geneious.util;

import java.util.Date;

import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.note.NaturalisNote;

/**
 * A factory for dummy documents, created when importing samplesheets that referto non-existent extract IDs.
 */
public class DummyDocumentFactory {

  /**
   * The nucleotide sequence for a dummy document: "NNNNNNNNNN"
   */
  public static final String DUMMY_NUCLEOTIDE_SEQUENCE = "NNNNNNNNNN";
  /**
   * The extract plate ID for a dummy document: "AA000"
   */
  public static final String DUMMY_PLATE_ID = "AA000";
  /**
   * The marker for a dummy document: "Dum"
   */
  public static final String DUMMY_MARKER = "Dum";

  /**
   * Creates a dummy document for the specified extract ID.
   * 
   * @param extractID
   * @return
   */
  public static DefaultNucleotideSequence createDummyDocument(String extractID) {
    String seqName = extractID + ".dum";
    String descr = "Dummy sequence";
    String sequence = DUMMY_NUCLEOTIDE_SEQUENCE;
    Date timestamp = new Date();
    URN urn = URN.generateUniqueLocalURN("Dummy");
    return new DefaultNucleotideSequence(seqName, descr, sequence, timestamp, urn);
  }

  /**
   * Creates a dummy document for the specified Note.
   * 
   * @param extractID
   * @return
   */
  public static DefaultNucleotideSequence createDummyDocument(NaturalisNote note) {
    return createDummyDocument(note.getExtractId());
  }

}
