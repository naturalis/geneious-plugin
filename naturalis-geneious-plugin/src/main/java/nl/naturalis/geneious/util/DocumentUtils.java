package nl.naturalis.geneious.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import nl.naturalis.geneious.gui.Ab1FastaFileFilter;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

public class DocumentUtils {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentUtils.class);

  /**
   * Whether or not the specified file is an AB1 file given the user-provided file extensions in the Geneious Preferences panel.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isAb1File(File f) throws IOException {
    Set<String> exts = Ab1FastaFileFilter.getAb1Extensions();
    if (exts.isEmpty()) { // then this is the best we can do:
      return firstChar(f) != '>';
    }
    for (String ext : exts) {
      if (f.getName().endsWith(ext)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether or not the specified file is a Fasta file given the user-provided file extensions in the Geneious Preferences panel.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isFastaFile(File f) throws IOException {
    Set<String> exts = Ab1FastaFileFilter.getFastaExtensions();
    if (exts.isEmpty()) { // then this is the best we can do:
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
