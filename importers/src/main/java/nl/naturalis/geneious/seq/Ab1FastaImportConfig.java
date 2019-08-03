package nl.naturalis.geneious.seq;

import java.io.File;

import nl.naturalis.geneious.OperationConfig;

class Ab1FastaImportConfig extends OperationConfig {

  private File[] files;

  Ab1FastaImportConfig() {
    super(); // initializes target folder & selected documents
  }

  public File[] getFiles() {
    return files;
  }

  public void setFiles(File[] files) {
    this.files = files;
  }

  @Override
  public String getOperationName() {
    return Ab1FastaDocumentOperation.NAME;
  }

}
