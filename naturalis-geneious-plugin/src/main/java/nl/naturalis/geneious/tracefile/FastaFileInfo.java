package nl.naturalis.geneious.tracefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

class FastaFileInfo extends TraceFileInfo {

  private final File motherFile;

  private NaturalisNote note;

  FastaFileInfo(File sourceFile, File motherFile) {
    super(sourceFile);
    this.motherFile = motherFile;
  }

  @Override
  NaturalisNote getNote() throws SequenceNameNotParsableException, IOException {
    if (note == null) {
      return (note = SequenceNameParser.parseFasta(getFastaHeader()));
    }
    return note;
  }

  /**
   * Returns the multi-sequence fasta file from which the single-sequence file wrapped by this instance was split off.
   * 
   * @return
   */
  public File getMotherFile() {
    return motherFile;
  }

  private String getFastaHeader() throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)))) {
      return br.readLine();
    }
  }
}
