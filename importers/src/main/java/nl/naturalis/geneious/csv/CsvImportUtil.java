package nl.naturalis.geneious.csv;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

/**
 * Utility class providing assistence with the import of CSV-like files.
 * 
 * @author Ayco Holleman
 *
 */
public class CsvImportUtil {

  /**
   * Valid file extensions for CSV files: csv, tsv, txt.
   */
  static final List<String> csvFileExtension = Arrays.asList("csv", "tsv", "txt");
  /**
   * Valid file extensions for spreadsheets: xls, xlsx.
   */
  static final List<String> spreadSheetFileExtension = Arrays.asList("xls", "xlsx");

  private CsvImportUtil() {}

  /**
   * Returns true if the file name's extension is "csv", "tsv" or "txt", false oterwise.
   * 
   * @param fileName
   * @return
   */
  public static boolean isCsvFile(String fileName) {
    String ext = FilenameUtils.getExtension(fileName);
    if (ext == null) {
      return false;
    }
    ext = ext.toLowerCase();
    return csvFileExtension.stream().anyMatch(ext::equals);
  }

  /**
   * Returns true if the file name's extension is "xls" or "xlsx", false otherwise.
   * 
   * @param fileName
   * @return
   */
  public static boolean isSpreadsheet(String fileName) {
    String ext = FilenameUtils.getExtension(fileName);
    if (ext == null) {
      return false;
    }
    ext = ext.toLowerCase();
    return spreadSheetFileExtension.stream().anyMatch(ext::equals);
  }

}
