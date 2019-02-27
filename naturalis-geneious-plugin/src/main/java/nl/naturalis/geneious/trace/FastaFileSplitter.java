package nl.naturalis.geneious.trace;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.base.NStrings;
import nl.naturalis.common.io.NFileUtils;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.RuntimeSettings;

import static java.nio.charset.StandardCharsets.UTF_8;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static org.apache.commons.lang3.StringUtils.*;

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

  FastaFileSplitter(boolean inMemory) {
    guiLogger.debug(() -> "Initializing fasta file splitter");
    if (this.inMemory = inMemory) {
      this.tmpDir = null;
    } else {
      this.tmpDir = inMemory ? null : NFileUtils.newFile(RuntimeSettings.WORK_DIR, "tmp", "fasta", System.currentTimeMillis());
      guiLogger.debugf(() -> format("Temporary fasta files will be saved to %s", tmpDir.getPath()));
    }
  }

  /**
   * Splits the specified fasta file into one or more nucleotide sequences and, if required, saves them to temporary, single-sequence fasta
   * files.
   * 
   * @param motherFile
   * @return
   * @throws IOException
   */
  List<FastaSequenceInfo> split(File motherFile) throws IOException {
    List<FastaSequenceInfo> files = new ArrayList<>();
    StringBuilder sequence = new StringBuilder(672); // they actually contain 659 chars
    try (BufferedReader br = new BufferedReader(new FileReader(motherFile))) {
      String header = br.readLine();
      String chunk;
      OUTER_LOOP: do {
        chunk = br.readLine();
        if (!isStartOfSequence(chunk)) {
          guiLogger.error("Corrupt file: \"%s\". Expected start of nucleotide sequence below \"%s\"", motherFile, header);
          break OUTER_LOOP;
        }
        INNER_LOOP: while (true) {
          sequence.append(chunk);
          chunk = br.readLine();
          if (chunk == null) {
            if (inMemory) {
              files.add(new FastaSequenceInfo(motherFile, substring(header, 1), sequence.toString()));
            } else {
              files.add(new FastaSequenceInfo(motherFile, saveSequence(chunk, sequence.toString())));
            }
            break OUTER_LOOP;
          }
          if (chunk.startsWith(">")) {
            if (inMemory) {
              files.add(new FastaSequenceInfo(motherFile, substring(chunk, 1), sequence.toString()));
            } else {
              files.add(new FastaSequenceInfo(motherFile, saveSequence(chunk, sequence.toString())));
            }
            header = chunk;
            sequence.setLength(0);
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

  private File saveSequence(String header, String sequence) throws IOException {
    File f = getTempFile();
    try (BufferedOutputStream bos = open(f)) {
      bos.write(header.getBytes(UTF_8));
      bos.write(NEWLINE);
      bos.write(sequence.getBytes(UTF_8));
    }
    return f;
  }

  private File getTempFile() {
    return NFileUtils.newFile(tmpDir, NStrings.zpad(++fileNo, 4, ".fasta"));
  }

  private static boolean isStartOfSequence(String chunk) {
    return chunk != null && !chunk.startsWith(">") && !StringUtils.isBlank(chunk);
  }

  private static BufferedOutputStream open(File f) throws IOException {
    return new BufferedOutputStream(FileUtils.openOutputStream(f), 4096);
  }

}
