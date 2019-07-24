package nl.naturalis.geneious.seq;

import java.io.File;

import com.google.common.base.Preconditions;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.name.SequenceInfo;
import nl.naturalis.geneious.name.SequenceNameParser;
import nl.naturalis.geneious.note.NaturalisNote;

import static org.apache.commons.io.FilenameUtils.getBaseName;

/**
 * A {@code SequenceInfo} object created from an AB1 file.
 */
final class Ab1Info extends SequenceInfo {

  private final String name;

  private NaturalisNote note;

  Ab1Info(File sourceFile) {
    super(sourceFile);
    name = getBaseName(getImportedFrom().getName());
  }

  @Override
  public DocumentType getDocumentType() {
    return DocumentType.AB1;
  }

  /**
   * Returns the base name of the file being imported.
   */
  @Override
  public String getName() {
    return name;
  }

  @Override
  public void createNote() throws NotParsableException {
    note = new SequenceNameParser(name).parseName();
  }

  @Override
  public NaturalisNote getNaturalisNote() {
    Preconditions.checkNotNull(note, "Note not yet created");
    return note;
  }

}
