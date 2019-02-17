package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

/**
 * The TraceFilePreprocessor divides the files selected by the user in the file chooser dialog into AB1 files and fasta files.
 * 
 * {@link FastaFileSplitter}.
 */
class TraceFilePreprocessor {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(TraceFileDocumentOperation.class);

  private final File[] files;

  TraceFilePreprocessor(File[] files) {
    this.files = files;
  }

  /**
   * Returns two lists; the first one contains only AB1 files, the second one only fasta files.
   * 
   * @return
   */
  List<List<File>> divideByFileType() {
    List<File> ab1Files = new ArrayList<>(), fastaFiles = new ArrayList<>();
    for (File file : files) {
      try {
        if (isAb1File(file)) {
          ab1Files.add(file);
        } else if (isFastaFile(file)) {
          fastaFiles.add(file);
        }
      } catch (IOException e) {
        guiLogger.error("Error processing file %s", e, file.getName());
      }
    }
    return Arrays.asList(ab1Files, fastaFiles);
  }

  static boolean isAb1File(File f) throws IOException {
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

  static boolean isFastaFile(File f) throws IOException {
    Set<String> exts = Ab1FastaFileFilter.getFastaExtensions();
    if (exts.isEmpty()) {
      return firstChar(f) == '>';
    }
    for (String ext : exts) {
      if (f.getName().endsWith(ext)) {
        if (firstChar(f) == '>') {
          return true;
        }
        guiLogger.warn("Invalid fasta file: %s. First character in file must be '>'", f.getName());
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
