package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;

public class Ab1FastaFileFilter extends FileFilter {

  static final Ab1FastaFileFilter INSTANCE = new Ab1FastaFileFilter();

  @Override
  public boolean accept(File f) {
    if (exts.contains("*")) {
      return true;
    }
    return exts.stream().filter(ext -> f.getName().endsWith("." + ext)).findFirst().isPresent();
  }

  @Override
  public String getDescription() {
    return "AB1 and Fasta files";
  }

  private final Set<String> exts;

  private Ab1FastaFileFilter() {
    exts = getFileExtensions();
  }

  private static Set<String> getFileExtensions() {
    Set<String> exts = new HashSet<>();
    String s = NaturalisPreferencesOptions.STATE.getAb1Extensions();
    if (StringUtils.isBlank(s)) {
      exts.add("*");
    } else {
      Arrays.stream(s.split(",")).forEach(x -> exts.add(x.trim().toLowerCase()));
    }
    s = NaturalisPreferencesOptions.STATE.getFastaExtensions();
    if (StringUtils.isBlank(s)) {
      exts.add("*");
    } else {
      Arrays.stream(s.split(",")).forEach(x -> exts.add(x.trim().toLowerCase()));
    }
    return exts;
  }

}
