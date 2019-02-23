package nl.naturalis.geneious.gui;

import java.io.File;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import nl.naturalis.geneious.util.DocumentUtils;

/**
 * A file filter for the file selection popup of the AB1/Fasta import. Limits the visible files within a folder to those with a valid AB1 or
 * fasta extension, as defined in the Preferences tab of the Naturalis plugin.
 */
public class Ab1FastaFileFilter extends FileFilter {

  private final Set<String> exts;

  public Ab1FastaFileFilter() {
    exts = DocumentUtils.getAb1Extensions();
    exts.addAll(DocumentUtils.getFastaExtensions());
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
