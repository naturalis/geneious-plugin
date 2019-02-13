package nl.naturalis.geneious.tracefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import nl.naturalis.common.io.NFileUtils;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.RuntimeSettings;

/**
 * The TraceFilePreprocessor divides the files selected by the user in the file chooser dialog into AB1 files and fasta files, and for each
 * of the selected fasta files it creates one or more temporary fasta files which are guaranteed to contain just a single nucleotide
 * sequence. The actual splitting of a single fasta file into multiple single-nucleotide fasta files is delegated to the
 * {@link FastaFileSplitter}.
 */
class TraceFilePreprocessor {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(TraceFileDocumentOperation.class);

  private final File[] files;
  private final FastaFileSplitter splitter;

  TraceFilePreprocessor(File[] files) {
    this.files = files;
    splitter = new FastaFileSplitter();
  }

  List<List<File>> split() {
    List<File> ab1Files = new ArrayList<>(), fastaFiles = new ArrayList<>();
    for (File file : files) {
      try {
        if (isAb1File(file)) {
          ab1Files.add(file);
        } else if (isFastaFile(file)) {
          fastaFiles.addAll(splitter.split(file));
        }
      } catch (IOException e) {
        guiLogger.error("Error processing file %s", e, file.getName());
      }
    }
    return Arrays.asList(ab1Files, fastaFiles);
  }

  private static boolean isAb1File(File f) throws IOException {
    Set<String> exts = Ab1FastaFileFilter.getAb1Extensions();
    if (exts.isEmpty()) {
      return firstChar(f) != '>';
    }
    for (String ext : exts) {
      if (f.getName().endsWith(ext)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isFastaFile(File f) throws IOException {
    Set<String> exts = Ab1FastaFileFilter.getFastaExtensions();
    if (exts.isEmpty()) {
      return firstChar(f) == '>';
    }
    for (String ext : exts) {
      if (f.getName().endsWith(ext)) {
        if (firstChar(f) == '>') {
          return true;
        }
        guiLogger.warn("Invalid fasta file: %s. It has valid file extension (%s), but does not start with '>'", f.getName(), ext);
        return false;
      }
    }
    return false;
  }

  private static char firstChar(File f) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(new FileInputStream(f))) {
      return (char) isr.read();
    }
  }

}
