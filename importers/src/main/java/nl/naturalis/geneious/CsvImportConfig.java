package nl.naturalis.geneious;

import java.io.File;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * Defines the configuration for imports of CSV-like files, including TSV, XLS and XLSX files.
 *
 * @author Ayco Holleman
 */
public class CsvImportConfig {

  private  final List<AnnotatedPluginDocument> selectedDocuments;
  
  private File file;
  private int skipLines;
  private int sheetNumber;

  public CsvImportConfig(List<AnnotatedPluginDocument> selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
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

  public List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

}