package nl.naturalis.geneious.gui;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;

import static nl.naturalis.common.base.NStrings.rtrim;

/**
 * A file filter for the file selection popup of the AB1/Fasta import. Limits the visible files within a folder to those with a valid AB1 or
 * fasta extension, as defined in the Preferences tab of the Naturalis plugin.
 */
public class Ab1FastaFileFilter extends FileFilter {

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

  private final Set<String> exts;

  public Ab1FastaFileFilter() {
    exts = getAb1Extensions();
    exts.addAll(getFastaExtensions());
  }

  @Override
  public boolean accept(File f) {
    if (f.isDirectory() || exts.isEmpty()) {
      return true;
    }
    return exts.stream().filter(ext -> f.getName().endsWith(ext)).findFirst().isPresent();
  }

  @Override
  public String getDescription() {
    return "AB1 and Fasta files";
  }

}
