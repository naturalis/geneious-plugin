package nl.naturalis.geneious.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import nl.naturalis.geneious.gui.log.LogLevel;

public class RuntimeSettings {

  private static final String SETTING_FILE = System.getProperty("user.home")
      + System.getProperty("file.separator") + ".nl.naturalis.genious.properties";

  public static final RuntimeSettings INSTANCE = new RuntimeSettings();

  private static final String TRACE_FILE_FOLDER = "TRACE_FILE_FOLDER";
  private static final String SAMPLE_SHEET_FOLDER = "SAMPLE_SHEET_FOLDER";
  private static final String CRS_FOLDER = "CRS_FILE_FOLDER";
  private static final String BOLD_FILE_FOLDER = "BOLD_FILE_FOLDER";
  private static final String REGENERATE_NOTE_TYPES = "REGENERATE_NOTE_TYPES";
  private static final String LOG_LEVEL = "LOG_LEVEL";

  private final Properties props;

  private File traceFileFolder;
  private File sampleSheetFolder;
  private File crsFolder;
  private File boldFileFolder;
  private Boolean regenerateNoteTypes;

  private RuntimeSettings() {
    props = new Properties();
    File f = new File(SETTING_FILE);
    if (f.exists()) {
      try {
        props.load(new FileInputStream(f));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public File getTraceFileFolder() {
    if (traceFileFolder == null) {
      String path = props.getProperty(TRACE_FILE_FOLDER, System.getProperty("user.home"));
      traceFileFolder = new File(path);
    }
    return traceFileFolder;
  }

  public void setTraceFileFolder(File traceFileFolder) {
    if (!traceFileFolder.equals(this.traceFileFolder)) {
      this.traceFileFolder = traceFileFolder;
      props.setProperty(TRACE_FILE_FOLDER, traceFileFolder.getAbsolutePath());
      saveSettings();
    }
  }

  public File getSampleSheetFolder() {
    if (sampleSheetFolder == null) {
      String path = props.getProperty(SAMPLE_SHEET_FOLDER, System.getProperty("user.home"));
      sampleSheetFolder = new File(path);
    }
    return sampleSheetFolder;
  }

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

  public File getBoldFileFolder() {
    if (boldFileFolder == null) {
      String path = props.getProperty(BOLD_FILE_FOLDER, System.getProperty("user.home"));
      sampleSheetFolder = new File(path);
    }
    return boldFileFolder;
  }

  public void setBoldFileFolder(File boldFileFolder) {
    if (!boldFileFolder.equals(this.boldFileFolder)) {
      this.boldFileFolder = boldFileFolder;
      props.setProperty(BOLD_FILE_FOLDER, boldFileFolder.getAbsolutePath());
      saveSettings();
    }
  }

  public LogLevel getLogLevel() {
    return LogLevel.valueOf(props.getProperty(LOG_LEVEL, LogLevel.INFO.name()));
  }

  /**
   * Whether or not document note types should always be regenerated, even if note type already
   * exists. This should really only be true during development, when the definition of a note type
   * may change.
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
    try {
      props.store(new FileOutputStream(SETTING_FILE), "Naturalis Geneious Plugins");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
