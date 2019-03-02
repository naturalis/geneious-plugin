package nl.naturalis.geneious.gui;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import com.google.common.base.Preconditions;

import nl.naturalis.geneious.util.DocumentUtils;

/**
 * A file filter for the file selection popup of the AB1/Fasta import. Limits the visible files within a folder to those with a valid AB1 or
 * fasta extension, as defined in the Preferences tab of the Naturalis plugin.
 */
public class Ab1FastaFileFilter extends FileFilter {

  private final Set<String> exts;
  private final String description;

  public Ab1FastaFileFilter(boolean showAB1Files, boolean showFastaFiles) {
    Preconditions.checkArgument(showAB1Files || showFastaFiles, "Either show AB1 files or fasta files or both");
    exts = new HashSet<>();
    if (showAB1Files) {
      exts.addAll(DocumentUtils.getAb1Extensions());
      if (showFastaFiles) {
        exts.addAll(DocumentUtils.getFastaExtensions());
        description = "AB1 and fasta files";
      } else {
        description = "AB1 files";
      }
    } else {
      exts.addAll(DocumentUtils.getFastaExtensions());
      description = "Fasta files";
    }
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
    return description;
  }

}
