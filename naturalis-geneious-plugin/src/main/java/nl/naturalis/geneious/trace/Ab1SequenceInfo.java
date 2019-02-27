package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

/**
 * Provides information about an AB1-encoded sequence.
 */
final class Ab1SequenceInfo extends SequenceInfo {

  private final String name;

  private NaturalisNote note;

  Ab1SequenceInfo(File sourceFile) {
    super(sourceFile);
    this.name = StringUtils.substringBefore(getSourceFile().getName(), ".");
  }

  @Override
  String getName() {
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
