package nl.naturalis.geneious.trace;

import java.io.File;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;

import static nl.naturalis.common.io.NFiles.basename;
import static nl.naturalis.geneious.util.SequenceNameParser.parseAb1;

/**
 * Provides information about an AB1-encoded sequence.
 */
final class Ab1SequenceInfo extends SequenceInfo {

  private final String name;

  private NaturalisNote note;

  Ab1SequenceInfo(File sourceFile) {
    super(sourceFile);
    name = basename(getSourceFile());
  }

  @Override
  DocumentType getDocumentType() {
    return DocumentType.AB1;
  }

  @Override
  String getName() {
    return name;
  }

  @Override
  void createNote() throws NotParsableException {
    note = parseAb1(name);
  }

  @Override
  NaturalisNote getNote() {
    return note;
  }

}
