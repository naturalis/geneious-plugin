package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;

import nl.naturalis.common.base.NStrings;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

/**
 * Provides information about an AB1-encoded sequence.
 */
final class Ab1SequenceInfo extends SequenceInfo {

  private String name;
  private NaturalisNote note;

  Ab1SequenceInfo(File sourceFile) {
    super(sourceFile);
  }

  @Override
  String getName() {
    if (name == null) {
      name = NStrings.substr(getSourceFile().getName(), '.');
    }
    return name;
  }

  @Override
  NaturalisNote getNote() throws NotParsableException, IOException {
    if (note == null) {
      return (note = SequenceNameParser.parseAb1(getName()));
    }
    return note;
  }

}
