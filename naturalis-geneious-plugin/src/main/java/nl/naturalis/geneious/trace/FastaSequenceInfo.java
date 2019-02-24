package nl.naturalis.geneious.trace;

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
final class FastaSequenceInfo extends SequenceInfo {

  private final File motherFile;

  private String name;
  private NaturalisNote note;
  private String sequence;

  FastaSequenceInfo(File sourceFile, File motherFile) {
    super(sourceFile);
    this.motherFile = motherFile;
  }

  FastaSequenceInfo(String name, String sequence, File motherFile) {
    super(null);
    this.name = name;
    this.sequence = sequence;
    this.motherFile = motherFile;
  }

  @Override
  public String getName() throws IOException {
    if (name == null) {
      readSourceFile();
    }
    return name;
  }

  /**
   * Returns the fasta-encoded nucleotide sequence.
   * 
   * @return
   * @throws IOException
   */
  public String getSequence() throws IOException {
    if (sequence == null) {
      readSourceFile();
    }
    return sequence;
  }

  @Override
  NaturalisNote getNote() throws SequenceNameNotParsableException, IOException {
    if (note == null) {
      return (note = SequenceNameParser.parseFasta(getName()));
    }
    return note;
  }

  /**
   * Returns the file from which the nucleotide sequence was extracted.
   * 
   * @return
   */
  public File getMotherFile() {
    return motherFile;
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
