package nl.naturalis.geneious.trace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

/**
 * Provides information about a fasta-encoded sequence.
 */
final class FastaSequenceInfo extends SequenceInfo {

  private final File child;

  private String name;
  private String sequence;

  private NaturalisNote note;

  FastaSequenceInfo(File mother, File child) {
    super(mother);
    this.child = child;
  }

  FastaSequenceInfo(File mother, String name, String sequence) {
    super(mother);
    this.child = null;
    this.name = name;
    this.sequence = sequence;
  }

  @Override
  public String getName() throws IOException {
    if (name == null) {
      readChildFile();
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
      readChildFile();
    }
    return sequence;
  }

  @Override
  NaturalisNote getNote() throws NotParsableException, IOException {
    if (note == null) {
      note = SequenceNameParser.parseFasta(getName());
    }
    return note;
  }

  /**
   * Returns the file from which the nucleotide sequence was extracted.
   * 
   * @return
   */
  public File getChildFile() {
    return child;
  }

  private void readChildFile() throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(child)))) {
      name = br.readLine().substring(1);
      StringBuilder sb = new StringBuilder(768); // sequences seem to be always 659 chars long
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        sb.append(line);
      }
      sequence = sb.toString();
    }
  }

}
