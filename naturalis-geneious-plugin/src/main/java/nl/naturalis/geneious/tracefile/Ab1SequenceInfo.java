package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;

import nl.naturalis.common.base.NStrings;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

/**
 * Provides information about an AB1-encoded sequence.
 */
class Ab1SequenceInfo extends SequenceIno {

  private String name;
  private NaturalisNote note;

  Ab1SequenceInfo(File sourceFile) {
    super(sourceFile);
  }

  @Override
  String getName() {
    if (name == null) {
      name = NStrings.deleteFrom(getSourceFile().getName(), '.');
    }
    return name;
  }

  @Override
  NaturalisNote getNote() throws SequenceNameNotParsableException, IOException {
    if (note == null) {
      return (note = SequenceNameParser.parseAb1(getName()));
    }
    return note;
  }

}
