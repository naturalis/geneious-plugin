package nl.naturalis.geneious.gui;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import nl.naturalis.common.Check;

import static nl.naturalis.common.StringMethods.endsWith;
import static nl.naturalis.geneious.name.NameUtil.getCurrentAb1ExtensionsWithDot;
import static nl.naturalis.geneious.name.NameUtil.getCurrentFastaExtensionsWithDot;

/**
 * A file filter for the file selection popup of the AB1/Fasta import. Limits the visible files within a folder to those with a valid AB1 or
 * fasta extension, as defined in the Preferences tab of the Naturalis plugin.
 */
public class Ab1FastaFileFilter extends FileFilter {

  private final Set<String> exts;
  private final String description;

  public Ab1FastaFileFilter(boolean showAB1Files, boolean showFastaFiles) {
    Check.argument(showAB1Files || showFastaFiles, "Either show AB1 files or fasta files or both");
    exts = new HashSet<>();
    if (showAB1Files) {
      exts.addAll(getCurrentAb1ExtensionsWithDot());
      if (showFastaFiles) {
        exts.addAll(getCurrentFastaExtensionsWithDot());
        description = "AB1 and fasta files";
      } else {
        description = "AB1 files";
      }
    } else {
      exts.addAll(getCurrentFastaExtensionsWithDot());
      description = "Fasta files";
    }
  }

  @Override
  public boolean accept(File f) {
    if (f.isDirectory() || exts.isEmpty()) {
      return true;
    }
    return endsWith(f.getName(), true, exts);
  }

  @Override
  public String getDescription() {
    return description;
  }

}
