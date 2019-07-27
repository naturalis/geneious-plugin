package nl.naturalis.geneious.csv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
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

  private final File file;

  private int sheetNumber = 0;

  /**
   * Creates a {@code SpreadSheetReader} for the provided file.
   * 
   * @param file
   */
  SpreadSheetReader(File file) {
    this.file = file;
  }

  String[] getSheetNames() throws EncryptedDocumentException, IOException {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try (Workbook workbook = WorkbookFactory.create(file)) {
      Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
      String[] names = new String[workbook.getNumberOfSheets()];
      for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
        names[i] = workbook.getSheetAt(i).getSheetName();
      }
      return names;
    } finally {
      Thread.currentThread().setContextClassLoader(cl);
    }
  }

  /**
   * Loads and returns all rows of the speadsheet.
   * 
   * @return
   * @throws EncryptedDocumentException
   * @throws IOException
   */
  List<String[]> readAllRows() throws EncryptedDocumentException, IOException {
    try (Workbook workbook = WorkbookFactory.create(file)) {
      if (sheetNumber >= workbook.getNumberOfSheets()) {
        String fmt = "Sheet number exceeds number of sheets in spreadsheet (%s)";
        throw new NaturalisPluginException(String.format(fmt, workbook.getNumberOfSheets()));
      }
      Sheet sheet = workbook.getSheetAt(sheetNumber);
      DataFormatter dataFormatter = new DataFormatter();
      List<String[]> rows = new ArrayList<>();
      for (Row row : sheet) {
        List<String> values = new ArrayList<>();
        for (Cell cell : row) {
          values.add(dataFormatter.formatCellValue(cell));
        }
        rows.add(values.toArray(new String[values.size()]));
      }
      return rows;
    }
  }

  /**
   * Sets the number of the sheet to read the rows from.
   * 
   * @param sheetNumber
   */
  void setSheetNumber(int sheetNumber) {
    this.sheetNumber = sheetNumber;
  }

}
