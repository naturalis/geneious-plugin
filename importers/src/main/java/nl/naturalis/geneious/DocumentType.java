package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.implementations.TextDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.smpl.DummySequence;

/**
 * Symbolic constants for document types that the Naturalis plugin deals with.
 */
public enum DocumentType {

  /**
   * Used for Geneious document types that the plugin doesn't know how to handle. The Naturalis plugin will emit
   * a warning when it unexpectedly encounters documents of an unknown type.
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
   * The type of document created by the sample sheet importer when confronted with extract IDs for which no
   * Geneious documents exist yet. Note that the corresponding Geneious type is the same as it is for FASTA,
   * because V1 of the Naturalis plugin <b>did</b> create a DefaultNucleotideSequence document with some dummy
   * default values (e.g. "NNNNNNNNNN" for the nucleotide sequence). It might be conceptually cleaner and
   * possibly also slightly more performant to simply create a {@link TextDocument}, which is another type of
   * document that Geneious allows you to create.
   */
  DUMMY(DefaultNucleotideSequence.class);

  private final Class<? extends PluginDocument> geneiousType;

  private DocumentType(Class<? extends PluginDocument> geneiousType) {
    this.geneiousType = geneiousType;
  }

  /**
   * Returns the Geneious-specific class to which this constant maps. Note that in this version of the plugin
   * dummy documents actually map to a subclass of Geneious's own {@code DefaultNucleotideSequence} class,
   * namely the {@link DummySequence} class. However documents created using version 1 of the plugin are
   * {@code DefaultNucleotideSequence} documents with special values that mark them as dummies. Therefore
   * {@code getGeneiousType} returns {@code DefaultNucleotideSequence} for the {@link #DUMMY} document type
   * (which is correct both for versions of the plugin).
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
