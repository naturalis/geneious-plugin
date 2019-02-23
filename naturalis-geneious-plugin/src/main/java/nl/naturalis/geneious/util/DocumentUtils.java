package nl.naturalis.geneious.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.common.base.NStrings.rtrim;

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
   * Whether or not the specified file is a Fasta file given the user-provided file extensions in the Geneious Preferences panel.
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

  public static Set<String> getAb1Extensions() {
    Set<String> exts = new HashSet<>();
    String s = NaturalisPreferencesOptions.getAb1Extensions();
    if (s != null && !(s = rtrim(s.trim(), ',')).equals("*")) {
      Arrays.stream(s.split(",")).forEach(x -> {
        x = x.trim().toLowerCase();
        if (StringUtils.isNotBlank(x)) {
          if (!x.startsWith(".")) {
            x = "." + x;
          }
          exts.add(x);
        }
      });
    }
    return exts;
  }

  public static Set<String> getFastaExtensions() {
    Set<String> exts = new HashSet<>();
    String s = NaturalisPreferencesOptions.getFastaExtensions();
    if (s != null && !(s = rtrim(s.trim(), ',')).equals("*")) {
      Arrays.stream(s.split(",")).forEach(x -> {
        x = x.trim().toLowerCase();
        if (StringUtils.isNotBlank(x)) {
          if (!x.startsWith(".")) {
            x = "." + x;
          }
          exts.add(x);
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
