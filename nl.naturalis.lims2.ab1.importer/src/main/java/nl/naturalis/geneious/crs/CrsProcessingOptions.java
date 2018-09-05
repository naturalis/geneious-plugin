package nl.naturalis.geneious.crs;

import java.io.File;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

class CrsProcessingOptions {

  private AnnotatedPluginDocument[] selectedDocuments;
  private File file;
  private int skipLines;
  private int sheetNum;

  public CrsProcessingOptions(AnnotatedPluginDocument[] selectedDocuments) {
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

  public int getSheetNum() {
    return sheetNum;
  }

  public void setSheetNum(int sheetNum) {
    this.sheetNum = sheetNum;
  }

  public AnnotatedPluginDocument[] getSelectedDocuments() {
    return selectedDocuments;
  }

}
