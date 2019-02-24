package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;

/**
 * Abstract base class for classes providing information about a single nucleotide sequence.
 */
abstract class SequenceInfo {

  private final File sourceFile;

  SequenceInfo(File sourceFile) {
    this.sourceFile = sourceFile;
  }

  /**
   * Returns the file that contained the nucleotide sequence.
   * 
   * @return
   */
  File getSourceFile() {
    return sourceFile;
  }

  /**
   * Returns the name associated with the nucleotide sequence. For AB1 files it is the file name minus the file extension. For Fasta files
   * it is the header preceding the nucleotide sequence minus the '>' character.
   * 
   * @return
   */
  abstract String getName() throws IOException;

  /**
   * Returns the Naturalis annotation, obtained by parsing the name.
   * 
   * @return
   * @throws SequenceNameNotParsableException
   * @throws IOException
   */
  abstract NaturalisNote getNote() throws SequenceNameNotParsableException, IOException;

}
