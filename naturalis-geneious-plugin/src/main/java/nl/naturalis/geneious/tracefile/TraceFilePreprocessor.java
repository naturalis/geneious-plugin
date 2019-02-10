package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

public class TraceFilePreprocessor {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(TraceFileDocumentOperation.class);

  private final List<File> fastaFiles = new ArrayList<>();
  private final List<File> ab1Files = new ArrayList<>();;

  TraceFilePreprocessor(File[] files) {
    split(files, fastaFiles, ab1Files);
  }

  private static void split(File[] files, List<File> fastaFiles, List<File> ab1Files) {

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
        guiLogger.warn("File %s will not be processed! It has valid file extension (%s), but does not start with '>'", f.getName(), ext);
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
