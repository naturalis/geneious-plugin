package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;

import static nl.naturalis.common.base.NStringUtils.rtrim;

class Ab1FastaFileFilter extends FileFilter {

  static Set<String> getAb1Extensions() {
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

  static Set<String> getFastaExtensions() {
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

  Ab1FastaFileFilter() {
    exts = getAb1Extensions();
    exts.addAll(getFastaExtensions());
  }

  @Override
  public boolean accept(File f) {
    if (exts.isEmpty()) {
      return true;
    }
    return exts.stream().filter(ext -> f.getName().endsWith(ext)).findFirst().isPresent();
  }

  @Override
  public String getDescription() {
    return "AB1 and Fasta files";
  }

}
