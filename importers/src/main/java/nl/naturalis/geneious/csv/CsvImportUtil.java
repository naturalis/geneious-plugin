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

  static final List<String> spreadSheetFileExts = Arrays.asList("xls", "xlsx");

  private CsvImportUtil() {}

  /**
   * Returns true if the file name's extension is "xls",\; false otherwise.
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
    return spreadSheetFileExts.stream().anyMatch(ext::equals);
  }

  /**
   * Returns true if the file name's extension is "csv", "tsv" or "txt"; false oterwise.
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
    return ext.equals("csv") || ext.equals("tsv") || ext.equals("txt");
  }

}
