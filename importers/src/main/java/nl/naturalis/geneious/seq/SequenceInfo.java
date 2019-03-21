package nl.naturalis.geneious.seq;

import java.io.File;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.util.SequenceNameParser;

/**
 * Abstract base class for classes providing information about a single nucleotide sequence. {@code SequenceInfo} objects are responsible
 * for producing a {@link NaturalisNote} (to be attached to a Geneious document} by parsing the name of the sequence. The actual parsing,
 * however, is delegated to a {@link SequenceNameParser}.
 */
abstract class SequenceInfo {

  private final File sourceFile;

  SequenceInfo(File sourceFile) {
    this.sourceFile = sourceFile;
  }

  /**
   * Returns the file that contained the nucleotide sequence(s). For fasta sequences this is the original file, selected by the user, which
   * may or may not contain multiple nucleotide sequences.
   * 
   * @return
   */
  File getSourceFile() {
    return sourceFile;
  }

  /**
   * Returns the type of trace file provding the nucleotide sequence (AB1 or fasta).
   * 
   * @return
   */
  abstract DocumentType getDocumentType();

  /**
   * Returns the name associated with the nucleotide sequence. For AB1 files it is the file name minus the file extension. For Fasta files
   * it is the header preceding the nucleotide sequence minus the '>' character.
   * 
   * @return
   */
  abstract String getName();

  /**
   * Creates a {@link NaturalisNote} instance containing the annotations that were parsed out of the sequence name. This method must be
   * called before {@link #getNaturalisNote() getNote}.
   */
  abstract void createNote() throws NotParsableException;

  /**
   * Returns the Naturalis annotation, obtained by parsing the name.
   * 
   * @return
   * @throws NotParsableException
   */
  abstract NaturalisNote getNaturalisNote();

}
