package nl.naturalis.geneious.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import nl.naturalis.geneious.NaturalisPluginException;

/**
 * A simple reader for MS spreadsheets. Loads and returns all rows at once.
 * 
 * @author Ayco Holleman
 *
 */
class SpreadSheetReader {

  /**
   * Returns the names of the sheets with the spreadsheet.
   * 
   * @return
   * @throws EncryptedDocumentException
   * @throws IOException
   */
  static String[] getSheetNames(File file) throws EncryptedDocumentException, IOException {
    try (Workbook workbook = WorkbookFactory.create(file)) {
      String[] names = new String[workbook.getNumberOfSheets()];
      for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
        names[i] = workbook.getSheetAt(i).getSheetName();
      }
      return names;
    }
  }

  private final CsvImportConfig<?> config;

  /**
   * Creates a {@code SpreadSheetReader} for the provided file.
   * 
   * @param file
   */
  SpreadSheetReader(CsvImportConfig<?> config) {
    this.config = config;
  }

  /**
   * Loads and returns all rows of the speadsheet.
   * 
   * @return
   * @throws EncryptedDocumentException
   * @throws IOException
   */
  List<String[]> readAllRows() throws EncryptedDocumentException, IOException {
    try (Workbook workbook = WorkbookFactory.create(config.getFile())) {
      if (config.getSheetNumber() >= workbook.getNumberOfSheets()) {
        String fmt = "Sheet number exceeds number of sheets in spreadsheet (%s)";
        throw new NaturalisPluginException(String.format(fmt, workbook.getNumberOfSheets()));
      }
      FormulaEvaluator evaluator = null;
      if (config.isSpreadsheetWithFormulas()) {
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        evaluator.evaluateAll();
      }
      Sheet sheet = workbook.getSheetAt(config.getSheetNumber());
      List<String[]> rows = new ArrayList<>();
      for (Row row : sheet) {
        List<String> values = new ArrayList<>();
        for (Cell cell : row) {
          switch (cell.getCellType()) {
            case STRING:
              values.add(cell.getStringCellValue());
              break;
            case NUMERIC:
              values.add(getNumber((cell.getNumericCellValue())));
              break;
            case BOOLEAN:
              values.add(String.valueOf(cell.getBooleanCellValue()));
              break;
            case FORMULA:
              if (!config.isSpreadsheetWithFormulas()) {
                throw new NaturalisPluginException("Formulas not supported");
              }
              CellValue cv = evaluator.evaluate(cell);
              switch (cv.getCellType()) {
                case STRING:
                  values.add(cv.getStringValue());
                  break;
                case NUMERIC:
                  values.add(getNumber(cv.getNumberValue()));
                  break;
                case BOOLEAN:
                  values.add(String.valueOf(cv.getBooleanValue()));
                  break;
                case ERROR:
                  values.add("<bad spreadsheet cell>");
                  break;
                default:
                  values.add("");
                  break;
              }
              break;
            case ERROR:
              values.add("<bad spreadsheet cell>");
              break;
            default:
              values.add("");
              break;
          }
        }
        rows.add(values.toArray(new String[values.size()]));
      }
      return rows;
    }
  }

  private static String getNumber(double d) {
    int i = (int) d;
    return (i == d) ? String.valueOf(i) : String.valueOf(d);
  }

}
