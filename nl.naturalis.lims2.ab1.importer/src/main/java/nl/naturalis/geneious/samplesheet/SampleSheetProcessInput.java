package nl.naturalis.geneious.samplesheet;

import java.io.File;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

class SampleSheetProcessInput {

  private final AnnotatedPluginDocument[] selectedDocuments;
  private File file;
  private boolean createDummies;
  private int skipLines;
  private int sheetNum;

  SampleSheetProcessInput(AnnotatedPluginDocument[] selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
  }

  AnnotatedPluginDocument[] getSelectedDocuments() {
    return selectedDocuments;
  }

  File getFile() {
    return file;
  }

  void setFile(File file) {
    this.file = file;
  }

  boolean isCreateDummies() {
    return createDummies;
  }

  void setCreateDummies(boolean createDummies) {
    this.createDummies = createDummies;
  }

  int getSkipLines() {
    return skipLines;
  }

  void setSkipLines(int skipLines) {
    this.skipLines = skipLines;
  }

  int getSheetNum() {
    return sheetNum;
  }

  void setSheetNum(int sheetNum) {
    this.sheetNum = sheetNum;
  }

}
