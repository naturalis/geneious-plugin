package nl.naturalis.geneious;

import java.io.File;

import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.name.SequenceNameParser;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Abstract base class for classes providing information about a single nucleotide sequence. {@code SequenceInfo} objects are responsible
 * for producing a {@link NaturalisNote} (to be attached to a Geneious document} by parsing the name of the sequence. The actual parsing,
 * however, is delegated to a {@link SequenceNameParser}.
 */
public abstract class SequenceInfo {

  private final File importedFrom;

  public SequenceInfo(File sourceFile) {
    this.importedFrom = sourceFile;
  }

  /**
   * Returns the file that contained the nucleotide sequence(s). For fasta sequences this is the original file, selected by the user, which
   * may or may not contain multiple nucleotide sequences (saved to temporary files).
   * 
   * @return
   */
  public File getImportedFrom() {
    return importedFrom;
  }

  /**
   * Returns the type of trace file provding the nucleotide sequence (AB1 or fasta).
   * 
   * @return
   */
  public abstract DocumentType getDocumentType();

  /**
   * Returns the name associated with the nucleotide sequence. For AB1 files it is the file name minus the file extension. For Fasta files
   * it is the header preceding the nucleotide sequence minus the '>' character.
   * 
   * @return
   */
  public abstract String getName();

  /**
   * Creates a {@link NaturalisNote} instance containing the annotations that were parsed out of the sequence name. This method must be
   * called before {@link #getNaturalisNote() getNote}. Although there is currently no difference between parsing AB1 file names and fasta
   * headers, we don't rely on it, so this method must be implemented by subclasses.
   */
  public abstract void createNote() throws NotParsableException;

  /**
   * Returns the Naturalis annotation, obtained by parsing the name.
   * 
   * @return
   * @throws NotParsableException
   */
  public abstract NaturalisNote getNaturalisNote();

}
