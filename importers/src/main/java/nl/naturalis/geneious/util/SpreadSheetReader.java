package nl.naturalis.geneious.util;

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

public class SpreadSheetReader {

  private final File file;

  private int sheetNumber = 0;
  private int skipRows = 0;

  public SpreadSheetReader(File file) {
    this.file = file;
  }

  public List<String[]> readAllRows() throws EncryptedDocumentException, IOException {
    Workbook workbook = WorkbookFactory.create(file);
    if (sheetNumber >= workbook.getNumberOfSheets()) {
      String fmt = "Sheet number exceeds number of sheets in spreadsheet (%s)";
      throw new NaturalisPluginException(String.format(fmt, workbook.getNumberOfSheets()));
    }
    Sheet sheet = workbook.getSheetAt(sheetNumber);
    DataFormatter dataFormatter = new DataFormatter();
    List<String[]> rows = new ArrayList<>();
    int skipped = 0;
    for (Row row : sheet) {
      if (skipped++ < skipRows) {
        continue;
      }
      List<String> values = new ArrayList<>();
      for (Cell cell : row) {
        values.add(dataFormatter.formatCellValue(cell));
      }
      rows.add(values.toArray(new String[values.size()]));
    }
    return rows;
  }

  public int getSheetNumber() {
    return sheetNumber;
  }

  public void setSheetNumber(int sheetNumber) {
    this.sheetNumber = sheetNumber;
  }

  public int getSkipRows() {
    return skipRows;
  }

  public void setSkipRows(int skipRows) {
    this.skipRows = skipRows;
  }

}
