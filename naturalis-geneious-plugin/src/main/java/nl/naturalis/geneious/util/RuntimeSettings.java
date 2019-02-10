package nl.naturalis.geneious.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import nl.naturalis.geneious.gui.log.LogLevel;

/**
 * Stores various kinds of user actions like the most recently selected folder for sample sheets.
 */
public class RuntimeSettings {

  public static final String WORK_DIR = System.getProperty("user.home") + File.separator + ".naturalis-geneious-plugin";
  private static final String CFG_FILE = WORK_DIR + File.separator + "naturalis-geneious-plugin.properties";

  public static final RuntimeSettings INSTANCE = new RuntimeSettings();

  private static final String AB1_FASTA_FOLDER = "AB1_FASTA_FOLDER";
  private static final String SAMPLE_SHEET_FOLDER = "SAMPLE_SHEET_FOLDER";
  private static final String BOLD_FOLDER = "BOLD_FOLDER";
  private static final String CRS_FOLDER = "CRS_FILE_FOLDER";
  private static final String REGENERATE_NOTE_TYPES = "REGENERATE_NOTE_TYPES";
  private static final String LOG_LEVEL = "LOG_LEVEL";

  private static final String CRS_SKIP_LINES = "CRS_SKIP_LINES";
  private static final String CRS_SHEET_NUM = "CRS_SHEET_NUM";

  private final Properties props;

  private File ab1FastaFolder;
  private File sampleSheetFolder;
  private File crsFolder;
  private File boldFolder;
  private Boolean regenerateNoteTypes;

  private RuntimeSettings() {
    props = new Properties();
    File f = new File(CFG_FILE);
    if (f.exists()) {
      try {
        props.load(new FileInputStream(f));
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
      String path = props.getProperty(AB1_FASTA_FOLDER, System.getProperty("user.home"));
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

  /**
   * Get most recently selected sample sheet folder.
   * 
   * @return
   */
  public File getSampleSheetFolder() {
    if (sampleSheetFolder == null) {
      String path = props.getProperty(SAMPLE_SHEET_FOLDER, System.getProperty("user.home"));
      sampleSheetFolder = new File(path);
    }
    return sampleSheetFolder;
  }

  /**
   * Set most recently selected sample sheet folder.
   * 
   * @return
   */
  public void setSampleSheetFolder(File sampleSheetFolder) {
    if (!sampleSheetFolder.equals(this.sampleSheetFolder)) {
      this.sampleSheetFolder = sampleSheetFolder;
      props.setProperty(SAMPLE_SHEET_FOLDER, sampleSheetFolder.getAbsolutePath());
      saveSettings();
    }
  }

  public File getCrsFolder() {
    if (crsFolder == null) {
      String path = props.getProperty(CRS_FOLDER, System.getProperty("user.home"));
      crsFolder = new File(path);
    }
    return crsFolder;
  }

  public void setCrsFolder(File crsFolder) {
    if (!crsFolder.equals(this.crsFolder)) {
      this.crsFolder = crsFolder;
      props.setProperty(CRS_FOLDER, crsFolder.getAbsolutePath());
      saveSettings();
    }
  }

  public int getCrsSkipLines() {
    if (props.containsKey(CRS_SKIP_LINES)) {
      return Integer.parseInt(props.getProperty(CRS_SKIP_LINES));
    }
    props.setProperty(CRS_SKIP_LINES, "1");
    return 1;
  }

  public void setCrsSkipLines(int linesToSkip) {
    if (getCrsSkipLines() != linesToSkip) {
      props.setProperty(CRS_SKIP_LINES, String.valueOf(linesToSkip));
      saveSettings();
    }
  }

  public int getCrsSheetNum() {
    if (props.containsKey(CRS_SHEET_NUM)) {
      return Integer.parseInt(props.getProperty(CRS_SHEET_NUM));
    }
    props.setProperty(CRS_SHEET_NUM, "0");
    return 0;
  }

  public void setCrsSheetNum(int sheetNum) {
    if (getCrsSheetNum() != sheetNum) {
      props.setProperty(CRS_SHEET_NUM, String.valueOf(sheetNum));
      saveSettings();
    }
  }

  public File getBoldFolder() {
    if (boldFolder == null) {
      String path = props.getProperty(BOLD_FOLDER, System.getProperty("user.home"));
      sampleSheetFolder = new File(path);
    }
    return boldFolder;
  }

  public void setBoldFolder(File boldFolder) {
    if (!boldFolder.equals(this.boldFolder)) {
      this.boldFolder = boldFolder;
      props.setProperty(BOLD_FOLDER, boldFolder.getAbsolutePath());
      saveSettings();
    }
  }

  public LogLevel getLogLevel() {
    return LogLevel.valueOf(props.getProperty(LOG_LEVEL, LogLevel.INFO.name()));
  }

  /**
   * Whether or not document note types should always be regenerated, even if note type already exists. This should really only be true
   * during development, when the definition of a note type may change.
   * 
   * @return
   */
  public boolean regenerateNoteTypes() {
    if (regenerateNoteTypes == null) {
      String val = props.getProperty(REGENERATE_NOTE_TYPES);
      if (val == null) {
        return (regenerateNoteTypes = Boolean.FALSE);
      }
      props.setProperty(REGENERATE_NOTE_TYPES, val);
      regenerateNoteTypes = Boolean.valueOf(val);
    }
    return regenerateNoteTypes.booleanValue();
  }

  private void saveSettings() {
    File f = new File(CFG_FILE);
    if (!f.exists()) {
      File d = new File(WORK_DIR);
      if (!d.exists()) {
        d.mkdir();
      }
    }
    try {
      props.store(new FileOutputStream(CFG_FILE), "Naturalis Geneious Plugins");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
