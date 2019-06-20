package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultAlignmentDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.smpl.DummySequence;

/**
 * Symbolic constants for all document types that the Naturalis plugin deals with. Note that the Geneious API also has a
 * class called {@code DocumentType}, but the Geneious API provides a far richer palette of document types than listed
 * here while, on the other hand, Geneious doesn't have something like a {@link #DUMMY} document type. In other words
 * you cannot really compare the plugin's notion of a document type with the Geneious notion of it. Nevertheless, we do
 * try to map each of <i>our</i> document types to a class in the Geneious API (see {@link #getGeneiousType()}).
 */
public enum DocumentType {

  /**
   * Used for unknown (unexpected) types of Geneious documents
   */
  UNKNOWN(null),
  /**
   * The type of document created by Geneious when importing an AB1 file.
   */
  AB1(DefaultNucleotideGraphSequence.class),
  /**
   * The type of document created by Geneious when importing a fasta file.
   */
  FASTA(DefaultNucleotideSequence.class),
  /**
   * The type of document created by Geneious when merging the two strands of a nucleotide sequence.
   */
  CONTIG(DefaultAlignmentDocument.class),
  /**
   * The type of document created by the sample sheet importer when confronted with extract IDs for which no Geneious
   * documents exist yet. Note that this type maps to the {@link DummySequence} class, which is a home-grown subclass of
   * the Geneious's own {@code DefaultNucleotideSequence} class. However, the {@link DummySequence} class was only
   * introduced with V2 of the plugin. In Version 1 of the plugin dummy documents were fasta documents (so
   * {@code DefaultNucleotideSequence} documents) with special values that marked them as dummies. We still need to be
   * able to handle documents created with that version 1!
   */
  DUMMY(DummySequence.class);

  private final Class<? extends PluginDocument> geneiousType;

  private DocumentType(Class<? extends PluginDocument> geneiousType) {
    this.geneiousType = geneiousType;
  }

  /**
   * Returns the Geneious-specific class to which this constant maps. Note that in this version of the plugin dummy
   * documents actually map to a subclass of Geneious's own {@code DefaultNucleotideSequence} class, namely the
   * {@link DummySequence} class. However documents created using version 1 of the plugin are
   * {@code DefaultNucleotideSequence} documents with special values that mark them as dummies. Therefore
   * {@code getGeneiousType} returns {@code DefaultNucleotideSequence} for the {@link #DUMMY} document type (which is
   * correct both for versions of the plugin).
   * 
   * @return The corresponding Geneious document type, or null in case of {@link #UNKNOWN}.
   */
  public Class<? extends PluginDocument> getGeneiousType() {
    return geneiousType;
  }

  public String toString() {
    return this == AB1 ? name() : name().toLowerCase();
  }

}
