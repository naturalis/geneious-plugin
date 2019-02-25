package nl.naturalis.geneious.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.stripEnd;

/**
 * Various methods related to Geneious documents.
 */
public class DocumentUtils {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentUtils.class);

  private DocumentUtils() {}

  public static boolean isDummyDocument(AnnotatedPluginDocument doc) {
    return false;
  }

  /**
   * Whether or not the specified file is an AB1 file as per the user-provided file extensions in the Geneious Preferences panel.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isAb1File(File f) throws IOException {
    Set<String> exts = DocumentUtils.getAb1Extensions();
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
   * Whether or not the specified file is a fasta file as per the user-provided file extensions in the Geneious Preferences panel.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  public static boolean isFastaFile(File f) throws IOException {
    Set<String> exts = DocumentUtils.getFastaExtensions();
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

  /**
   * Returns the AB1 file extensions in the Geneious Preferences panel.
   * 
   * @return
   */
  public static Set<String> getAb1Extensions() {
    Set<String> exts = new HashSet<>();
    String s = NaturalisPreferencesOptions.getAb1Extensions();
    if (s != null && !(s = stripEnd(s, ", ")).equals("*")) {
      Arrays.stream(s.split(",")).forEach(ext -> {
        ext = ext.trim().toLowerCase();
        if (isNotBlank(ext)) {
          if (!ext.startsWith(".")) {
            ext = "." + ext;
          }
          exts.add(ext);
        }
      });
    }
    return exts;
  }

  /**
   * Returns the fasta file extensions in the Geneious Preferences panel.
   * 
   * @return
   */
  public static Set<String> getFastaExtensions() {
    Set<String> exts = new HashSet<>();
    String s = NaturalisPreferencesOptions.getFastaExtensions();
    if (s != null && !(s = stripEnd(s, ", ")).equals("*")) {
      Arrays.stream(s.split(",")).forEach(ext -> {
        ext = ext.trim().toLowerCase();
        if (isNotBlank(ext)) {
          if (!ext.startsWith(".")) {
            ext = "." + ext;
          }
          exts.add(ext);
        }
      });
    }
    return exts;
  }

  private static char firstChar(File f) throws IOException {
    try (InputStreamReader isr = new InputStreamReader(new FileInputStream(f))) {
      return (char) isr.read();
    }
  }

}
