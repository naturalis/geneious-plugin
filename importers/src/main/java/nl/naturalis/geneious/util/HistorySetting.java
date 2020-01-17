package nl.naturalis.geneious.util;

import nl.naturalis.common.Check;

public enum HistorySetting {

  SEQ_LAST_SELECTED_GENEIOUS_FOLDER("seq.lastSelectedGeneiousFolder"),
  SMPL_LAST_SELECTED_GENEIOUS_FOLDER("smpl.lastSelectedGeneiousFolder"),

  SEQ_LAST_SELECTED_FILE_SYSTEM_FOLDER("seq.lastSelectedFileSystemFolder"),
  SMPL_LAST_SELECTED_FILE_SYSTEM_FOLDER("smpl.lastSelectedFileSystemFolder"),
  CRS_LAST_SELECTED_FILE_SYSTEM_FOLDER("crs.lastSelectedFileSystemFolder"),
  BOLD_LAST_SELECTED_FILE_SYSTEM_FOLDER("bold.lastSelectedFileSystemFolder"),

  SMPL_LAST_SELECTED_SHEET("smpl.lastSelectedSheet"),
  CRS_LAST_SELECTED_SHEET("crs.lastSelectedSheet"),
  BOLD_LAST_SELECTED_SHEET("bold.lastSelectedSheet"),

  SMPL_LAST_SELECTED_DELIMITER("smpl.lastSelectedDelimiter"),
  CRS_LAST_SELECTED_DELIMITER("crs.lastSelectedDelimiter"),
  BOLD_LAST_SELECTED_DELIMITER("bold.lastSelectedDelimiter");

  private static final String NAME_PREFIX = "nl.naturalis.geneious.";

  public static HistorySetting parse(String name) {
    for (HistorySetting setting : values()) {
      if (setting.name.equals(name)) {
        return setting;
      }
    }
    return null;
  }

  public static HistorySetting forPackage(String packageName, String simpleName) {
    HistorySetting s = parse(NAME_PREFIX + packageName + "." + simpleName);
    Check.argument(s != null, "No RuntimeSetting corresponding to package=%s;simpleName=%s", packageName, simpleName);
    return s;
  }

  private String name;

  private HistorySetting(String name) {
    this.name = NAME_PREFIX + name;
  }

  public String toString() {
    return name;
  }

}
