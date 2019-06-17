package nl.naturalis.geneious.csv;

import java.io.File;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.common.collection.EnumToIntMap;

/**
 * Contains the user input driving the import of CSV-like files (BOLD Import, CRS Import, Sample Sheet Import).
 * Subclasses may specify additionally required user input (e.g. the sample sheet importer needs to know whether or not
 * to create dummies), but they all need to know things like the field delimiter.
 *
 * @author Ayco Holleman
 */
public abstract class CsvImportConfig<T extends Enum<T>> {

  private List<AnnotatedPluginDocument> selectedDocuments;
  private File file;
  private String delimiter;
  private int skipLines;
  private int sheetNumber;

  /**
   * Returns the documents selected by the user in the GUI.
   * 
   * @return
   */
  public List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  /**
   * Sets the documents selected by the user in the GUI.
   * 
   * @param selectedDocuments
   */
  public void setSelectedDocuments(List<AnnotatedPluginDocument> selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
  }

  /**
   * Returns the file to import.
   * 
   * @return
   */
  public File getFile() {
    return file;
  }

  /**
   * Sets the file to import
   * 
   * @param file
   */
  public void setFile(File file) {
    this.file = file;
  }

  /**
   * Returns the field delimiter (not applicable when importing spread sheets).
   * 
   * @return
   */
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * Sets the field delimiter (not applicable when importing spread sheets).
   * 
   * @param delimiter
   */
  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  /**
   * Returns the number of header rows within the file to be imported.
   * 
   * @return
   */
  public int getSkipLines() {
    return skipLines;
  }

  /**
   * Sets the number of header rows within the file to be imported.
   * 
   * @param skipLines
   */
  public void setSkipLines(int skipLines) {
    this.skipLines = skipLines;
  }

  /**
   * Returns the sheet number of the sheet containing the data (not applicable when impoerting CSV files).
   * 
   * @return
   */
  public int getSheetNumber() {
    return sheetNumber;
  }

  /**
   * Sets the sheet number of the sheet containing the data (not applicable when impoerting CSV files).
   * 
   * @param sheetNumber
   */
  public void setSheetNumber(int sheetNumber) {
    this.sheetNumber = sheetNumber;
  }

  /**
   * Returns a mapping of symbolic column names to actual column numbers. We use symbolic column names (e.g. see
   * {@code BoldColumn}) rather than column numbers to make the code less error prone. All subclasses of
   * {@code CsvImportConfig} currently return a hard-coded map. However in the future, we might need user input to
   * properly configure a column mapping or to make it less rigid.
   * 
   * @return
   */
  public abstract EnumToIntMap<T> getColumnNumbers();

}
