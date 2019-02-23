package nl.naturalis.geneious.tracefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

/**
 * Provides information about a fasta-encoded sequence.
 */
class FastaSequenceInfo extends SequenceIno {

  private final File motherFile;
  private String name;
  private NaturalisNote note;
  private String sequence;

  FastaSequenceInfo(File sourceFile, File motherFile) {
    super(sourceFile);
    this.motherFile = motherFile;
  }

  /**
   * Returns the fasta file from which the single-sequence file wrapped by this instance was split off.
   * 
   * @return
   */
  public File getMotherFile() {
    return motherFile;
  }

  @Override
  public String getName() throws IOException {
    if (name == null) {
      readSourceFile();
    }
    return name;
  }

  @Override
  NaturalisNote getNote() throws SequenceNameNotParsableException, IOException {
    if (note == null) {
      return (note = SequenceNameParser.parseFasta(getName()));
    }
    return note;
  }

  public String getSequence() throws IOException {
    if (sequence == null) {
      readSourceFile();
    }
    return sequence;
  }

  private void readSourceFile() throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getSourceFile())))) {
      name = br.readLine().substring(1);
      StringBuilder sb = new StringBuilder(1024);
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        sb.append(line);
      }
      sequence = sb.toString();
    }
  }

}
