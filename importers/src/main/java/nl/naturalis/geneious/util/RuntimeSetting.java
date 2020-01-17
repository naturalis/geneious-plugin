package nl.naturalis.geneious.util;

import nl.naturalis.common.Check;

public enum RuntimeSetting {

  SEQ_LAST_SELECTED_GENEIOUS_FOLDER("seq.lastSelectedGeneiousFolder"),
  SMPL_LAST_SELECTED_GENEIOUS_FOLDER("smpl.lastSelectedGeneiousFolder"),

  SEQ_LAST_SELECTED_FILE_SYSTEM_FOLDER("seq.lastSelectedFileSystemFolder"),
  SMPL_LAST_SELECTED_FILE_SYSTEM_FOLDER("smpl.lastSelectedFileSystemFolder"),
  CRS_LAST_SELECTED_FILE_SYSTEM_FOLDER("crs.lastSelectedFileSystemFolder"),
  BOLD_LAST_SELECTED_FILE_SYSTEM_FOLDER("bold.lastSelectedFileSystemFolder");

  private static final String NAME_PREFIX = "nl.naturalis.geneious.";

  public static RuntimeSetting parse(String name) {
    for (RuntimeSetting setting : values()) {
      if (setting.name.equals(name)) {
        return setting;
      }
    }
    return null;
  }

  public static RuntimeSetting forPackage(String packageName, String simpleName) {
    RuntimeSetting s = parse(NAME_PREFIX + packageName + "." + simpleName);
    Check.argument(s != null, "No RuntimeSetting corresponding to package=%s;simpleName=%s", packageName, simpleName);
    return s;
  }

  private String name;

  private RuntimeSetting(String name) {
    this.name = NAME_PREFIX + name;
  }

  public String toString() {
    return name;
  }

}
