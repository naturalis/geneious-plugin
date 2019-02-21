package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

class Ab1FileInfo extends TraceFileInfo {

  private NaturalisNote note;

  Ab1FileInfo(File sourceFile) {
    super(sourceFile);
  }

  @Override
  NaturalisNote getNote() throws SequenceNameNotParsableException, IOException {
    if (note == null) {
      return (note = SequenceNameParser.parseAb1(sourceFile.getName()));
    }
    return note;
  }

}
