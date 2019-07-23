package nl.naturalis.geneious.name;

import java.io.File;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * {@code SequenceInfo} objects are responsible for producing the annotations for a document. The annotations are
 * extracted from the sequence name. In case of AB1 files the sequence name is the same as the file name minus the file
 * extension. In case of fasta files the sequence name is the fasta header line minus the opening '&gt;' character.
 * {@code SequenceInfo} objects delegate the actual name parsing to a {@link SequenceNameParser}.
 */
public abstract class SequenceInfo {

  private final File importedFrom;

  /**
   * Creates a {@code SequenceInfo} object for the provided source file (either an AB1 file or a fasta file).
   * 
   * @param sourceFile
   */
  public SequenceInfo(File sourceFile) {
    this.importedFrom = sourceFile;
  }

  /**
   * Returns the source file for which this {@code SequenceInfo} object was created. For fasta sequences this is the
   * <i>original</i> file, selected by the user, which may or may not contain multiple nucleotide sequences.
   * 
   * @return
   */
  public File getImportedFrom() {
    return importedFrom;
  }

  /**
   * Returns either {@link DocumentType#AB1 AB1} or {@link DocumentType#FASTA FASTA}, depending on the source file passed
   * in through the {@link #SequenceInfo(File) constructor}.
   * 
   * @return
   */
  public abstract DocumentType getDocumentType();

  /**
   * Returns the name associated with the nucleotide sequence. For AB1 files this is the file name minus the file
   * extension. For Fasta files it is the header preceding the nucleotide sequence minus the '&gt;' character.
   * 
   * @return
   */
  public abstract String getName();

  /**
   * Creates a {@link NaturalisNote} containing the annotations that were parsed out of the sequence name. This method
   * <b>must</i> be called before {@link #getNaturalisNote() getNote}. During creation of the annotations a
   * {@link NotParsableException} may be thrown, but not when retrieving them via {@code getNote}.Although there is
   * currently no difference between parsing AB1 file names and fasta headers, we don't rely on it, so this method must be
   * implemented by subclasses.
   * 
   * @throws NotParsableException If the sequence name did not comply with the rules for naming AB1 or fasta files.
   */
  public abstract void createNote() throws NotParsableException;

  /**
   * Returns the Naturalis annotation, obtained through parsing the name.
   * 
   * @return
   */
  public abstract NaturalisNote getNaturalisNote();

}
