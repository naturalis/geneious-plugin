package nl.naturalis.geneious.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import nl.naturalis.common.io.NFiles;

/**
 * Stores various kinds of user actions like the most recently selected folder for sample sheets.
 */
public class RuntimeSettings {

  private static final String SYSPROP_HOME = System.getProperty("user.home");

  public static final File USER_HOME = new File(SYSPROP_HOME);
  public static final File WORK_DIR = NFiles.newFile(USER_HOME, ".nbc-geneious-plugin");
  public static final File CFG_FILE = NFiles.newFile(WORK_DIR, "nbc-geneious-plugin.properties");

  public static final RuntimeSettings INSTANCE = new RuntimeSettings();

  private static final String AB1_FASTA_FOLDER = "AB1_FASTA_FOLDER";

  private final Properties props;

  private File ab1FastaFolder;

  private RuntimeSettings() {
    props = new Properties();
    if (CFG_FILE.exists()) {
      try {
        props.load(new FileInputStream(CFG_FILE));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Get most recently selected AB1/fasta folder.
   * 
   * @return
   */
  public File getAb1FastaFolder() {
    if (ab1FastaFolder == null) {
      String path = props.getProperty(AB1_FASTA_FOLDER, SYSPROP_HOME);
      ab1FastaFolder = new File(path);
    }
    return ab1FastaFolder;
  }

  /**
   * Set most recently selected AB1/fasta folder.
   * 
   * @return
   */
  public void setAb1FastaFolder(File traceFileFolder) {
    if (!traceFileFolder.equals(this.ab1FastaFolder)) {
      this.ab1FastaFolder = traceFileFolder;
      props.setProperty(AB1_FASTA_FOLDER, traceFileFolder.getAbsolutePath());
      saveSettings();
    }
  }

  private void saveSettings() {
    try (BufferedOutputStream bos = new BufferedOutputStream(FileUtils.openOutputStream(CFG_FILE), 2048)) {
      props.store(new FileOutputStream(CFG_FILE), "Naturalis Geneious Plugins");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
