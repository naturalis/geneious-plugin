package nl.naturalis.geneious.seq;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.RuntimeSettings;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.apache.commons.lang3.StringUtils.substring;

import static nl.naturalis.common.io.NFiles.newFile;
import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Splits a fasta file in a set of new fasta files, each containing a single nucleotide sequence, and saves the newly created fasta files to
 * a temporary directory.
 */
class FastaFileSplitter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FastaFileSplitter.class);
  private static final byte[] NEWLINE = IOUtils.LINE_SEPARATOR.getBytes(UTF_8);

  private final boolean inMemory;
  private final File tmpDir;

  private int fileNo = 0;

  /**
   * Creates a new {@code FastaFileSplitter}.
   * 
   * @param inMemory If {@code true}, the splitter will create {@link FastaSequenceInfo} instances with the sequence data already set.
   *        Otherwise it will save the sequence to temporaray, single-sequence fasta files.
   */
  FastaFileSplitter(boolean inMemory) {
    guiLogger.debug(() -> "Initializing fasta file splitter");
    if (this.inMemory = inMemory) {
      this.tmpDir = null;
    } else {
      this.tmpDir = inMemory ? null : newFile(RuntimeSettings.WORK_DIR, "tmp", "fasta", System.currentTimeMillis());
    }
  }

  /**
   * Splits the specified fasta file into one or more nucleotide sequences, wrapping them into {@code FastaSequenceInfo} objects.
   * 
   * @param motherFile
   * @return
   * @throws IOException
   */
  List<FastaSequenceInfo> split(File motherFile) throws IOException {
    List<FastaSequenceInfo> files = new ArrayList<>();
    StringBuilder buf = new StringBuilder(672); // fasta sequences actually contain 659 chars
    try (BufferedReader br = new BufferedReader(new FileReader(motherFile))) {
      String header = br.readLine();
      String line;
      OUTER_LOOP: do {
        line = br.readLine();
        if (!isStartOfSequence(line)) {
          guiLogger.error("Corrupt file: \"%s\". Expected start of nucleotide sequence below \"%s\"", motherFile, header);
          break OUTER_LOOP;
        }
        INNER_LOOP: while (true) {
          buf.append(line);
          line = br.readLine();
          if (line == null) { // end of file
            files.add(newSequenceInfo(motherFile, header, buf.toString()));
            break OUTER_LOOP;
          } else if (line.startsWith(">")) { // start of new sequence
            files.add(newSequenceInfo(motherFile, header, buf.toString()));
            header = line;
            buf.setLength(0);
            break INNER_LOOP;
          }
        }
      } while (true);
    }
    if (files.size() > 1) {
      guiLogger.debugf(() -> format("File \"%s\" was split into %s nucleotide sequences", motherFile.getName(), files.size()));
    }
    return files;
  }

  private FastaSequenceInfo newSequenceInfo(File mother, String header, String sequence) throws IOException {
    if (inMemory) {
      return new FastaSequenceInfo(mother, substring(header, 1), sequence);
    }
    String base = FilenameUtils.getBaseName(mother.getName());
    String ext = FilenameUtils.getExtension(mother.getName());
    String childName = new StringBuilder(base.length() + 10)
        .append(base)
        .append('_')
        .append(++fileNo)
        .append('.')
        .append(ext)
        .toString();
    File child = newFile(tmpDir, childName);
    try (BufferedOutputStream bos = open(child)) {
      bos.write(header.getBytes(UTF_8));
      bos.write(NEWLINE);
      bos.write(sequence.getBytes(UTF_8));
    }
    return new FastaSequenceInfo(mother, substring(header, 1), child);
  }

  /**
   * Returns the directory into which the splitter has written the single-sequence fasta files.
   * 
   * @return
   */
  File getFastaTempDirectory() {
    return tmpDir;
  }

  /**
   * Returns the total number of single-sequence fasta files created by this instance.
   * 
   * @return
   */
  int getSplitCount() {
    return fileNo;
  }

  private static BufferedOutputStream open(File f) throws IOException {
    return new BufferedOutputStream(FileUtils.openOutputStream(f), 4096);
  }

  private static boolean isStartOfSequence(String chunk) {
    return chunk != null && !chunk.startsWith(">") && !StringUtils.isBlank(chunk);
  }

}
