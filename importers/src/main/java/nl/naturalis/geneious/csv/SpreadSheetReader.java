package nl.naturalis.geneious.csv;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import nl.naturalis.geneious.NonFatalException;

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
   * @throws NonFatalException
   */
  List<String[]> readAllRows() throws EncryptedDocumentException, IOException, NonFatalException {
    try (Workbook workbook = WorkbookFactory.create(config.getFile())) {
      FormulaEvaluator evaluator = null;
      workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
      evaluator = workbook.getCreationHelper().createFormulaEvaluator();
      evaluator.evaluateAll();
      Sheet sheet = workbook.getSheetAt(config.getSheetNumber());
      List<String[]> rows = new ArrayList<>();
      for (Row row : sheet) {
        List<String> values = new ArrayList<>();
        for (Cell cell : row) {
          values.add(getCellValue(cell, evaluator));
        }
        rows.add(values.toArray(new String[values.size()]));
      }
      return rows;
    }
  }

  private static String getCellValue(Cell cell, FormulaEvaluator evaluator) throws NonFatalException {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        return getNumber((cell.getNumericCellValue()));
      case BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case FORMULA:
        CellValue cv = evaluator.evaluate(cell);
        switch (cv.getCellType()) {
          case STRING:
            return cv.getStringValue();
          case NUMERIC:
            return getNumber(cv.getNumberValue());
          case BOOLEAN:
            return String.valueOf(cv.getBooleanValue());
          case ERROR:
            throw badCell(cell);
          default:
            return StringUtils.EMPTY;
        }
      case ERROR:
        throw badCell(cell);
      default:
        return StringUtils.EMPTY;
    }
  }

  private static NonFatalException badCell(Cell cell) {
    String fmt = "Bad row at row %s, cell %s";
    String msg = String.format(fmt, cell.getRowIndex() + 1, cell.getColumnIndex());
    return new NonFatalException(msg);
  }

  private static String getNumber(double d) {
    // We really don't want scientific notation
    return new BigDecimal(d).toPlainString();
  }

}
