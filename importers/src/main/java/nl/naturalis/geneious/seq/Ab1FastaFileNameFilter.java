package nl.naturalis.geneious.seq;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import nl.naturalis.geneious.util.DocumentUtils;

public class Ab1FastaFileNameFilter implements FilenameFilter {

  private final Set<String> exts;

  public Ab1FastaFileNameFilter(boolean showAB1Files, boolean showFastaFiles) {
    exts = new HashSet<>();
    if(showAB1Files) {
      exts.addAll(DocumentUtils.getAb1Extensions());
      if(showFastaFiles) {
        exts.addAll(DocumentUtils.getFastaExtensions());
      } else {
      }
    } else {
      exts.addAll(DocumentUtils.getFastaExtensions());
    }
  }

  @Override
  public boolean accept(File dir, String name) {
    return exts.stream().filter(name::endsWith).findFirst().isPresent();
  }

}
