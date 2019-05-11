package nl.naturalis.geneious.csv;

import java.io.File;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.common.collection.EnumToIntMap;

/**
 * Contains configuration settings for imports of CSV-like files (including spreadsheets).
 *
 * @author Ayco Holleman
 */
public abstract class CsvImportConfig<T extends Enum<T>> {

  private List<AnnotatedPluginDocument> selectedDocuments;
  private File file;
  private String delimiter;
  private int skipLines;
  private int sheetNumber;

  public List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  public void setSelectedDocuments(List<AnnotatedPluginDocument> selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public int getSkipLines() {
    return skipLines;
  }

  public void setSkipLines(int skipLines) {
    this.skipLines = skipLines;
  }

  public int getSheetNumber() {
    return sheetNumber;
  }

  public void setSheetNumber(int sheetNumber) {
    this.sheetNumber = sheetNumber;
  }

  /**
   * Returns a mapping of symbolic column names to actual column numbers. N.B. all subclasses of CsvImportConfig currently return a hard-coded
   * map. However in the future, we might need user input to properly configure a column mapping.
   * 
   * @return
   */
  public abstract EnumToIntMap<T> getColumnNumbers();

}
