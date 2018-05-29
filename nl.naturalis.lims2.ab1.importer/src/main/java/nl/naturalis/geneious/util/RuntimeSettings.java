package nl.naturalis.geneious.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class RuntimeSettings {

  private static final String SETTING_FILE = System.getProperty("user.home")
      + System.getProperty("file.separator") + ".nl.naturalis.genious.properties";

  public static final RuntimeSettings INSTANCE = new RuntimeSettings();

  private static final String LAST_SELECTED_FOLDER = "LAST_SELECTED_FOLDER";

  private final Properties props;

  private File lastSelectedFolder;

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

  public File getLastSelectedFolder() {
    if (lastSelectedFolder == null) {
      String path = props.getProperty(LAST_SELECTED_FOLDER);
      if (path == null) {
        path = System.getProperty("user.home");
      }
      return (lastSelectedFolder = new File(path));
    }
    return lastSelectedFolder;
  }

  public void setLastSelectedFolder(File lastSelectedFolder) {
    if (!lastSelectedFolder.equals(this.lastSelectedFolder)) {
      this.lastSelectedFolder = lastSelectedFolder;
      props.setProperty(LAST_SELECTED_FOLDER, lastSelectedFolder.getAbsolutePath());
      try {
        props.store(new FileOutputStream(SETTING_FILE), "Naturalis Geneious Plugins");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
