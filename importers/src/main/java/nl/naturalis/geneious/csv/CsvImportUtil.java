package nl.naturalis.geneious.csv;

import org.apache.commons.io.FilenameUtils;

public class CsvImportUtil {

  private CsvImportUtil() {}

  public static boolean isSpreadsheet(String fileName) {
    String ext = FilenameUtils.getExtension(fileName);
    if (ext == null) {
      return false;
    }
    ext = ext.toLowerCase();
    return ext.equals("xls") || ext.equals("xlsx");
  }

  public static boolean isCsvFile(String fileName) {
    String ext = FilenameUtils.getExtension(fileName);
    if (ext == null) {
      return false;
    }
    ext = ext.toLowerCase();
    return ext.equals("csv") || ext.equals("tsv") || ext.equals("txt");
  }

}
