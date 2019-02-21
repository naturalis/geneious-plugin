package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;

abstract class TraceFileInfo {

  final File sourceFile;

  TraceFileInfo(File sourceFile) {
    this.sourceFile = sourceFile;
  }

  File getSourceFile() {
    return sourceFile;
  }

  abstract NaturalisNote getNote() throws SequenceNameNotParsableException, IOException;

}
