package nl.naturalis.geneious.util;

import java.io.File;
import java.util.List;

public class SpreadSheetReader {

  private final File file;

  private int sheet = 0;
  private int numRowsToSkip = 0;

  public SpreadSheetReader(File file) {
    this.file = file;
  }

  public List<String[]> readAllRows() {
    return null;
  }

  public void setSheet(int sheet) {
    this.sheet = sheet;
  }

  public void setNumRowsToSkip(int numRowsToSkip) {
    this.numRowsToSkip = numRowsToSkip;
  }

}
