package nl.naturalis.geneious.seq;

import java.io.File;

import com.google.common.base.Preconditions;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.SequenceInfo;
import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.name.SequenceNameParser;
import nl.naturalis.geneious.note.NaturalisNote;

import static org.apache.commons.io.FilenameUtils.getBaseName;

/**
 * Provides information about an AB1-encoded sequence.
 */
final class AB1Info extends SequenceInfo {

  private final String name;

  private NaturalisNote note;

  AB1Info(File sourceFile) {
    super(sourceFile);
    name = getBaseName(getImportedFrom().getName());
  }

  @Override
  public DocumentType getDocumentType() {
    return DocumentType.AB1;
  }

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
