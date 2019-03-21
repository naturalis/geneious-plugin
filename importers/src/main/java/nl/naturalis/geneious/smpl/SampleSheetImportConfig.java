package nl.naturalis.geneious.smpl;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.CsvImportConfig;

/**
 * Stores the user input provided via a Swing dialog.
 *
 * @author Ayco Holleman
 */
public class SampleSheetImportConfig extends CsvImportConfig {

  private boolean createDummies;

  SampleSheetImportConfig(AnnotatedPluginDocument[] selectedDocuments) {
    super(selectedDocuments);
  }

  boolean isCreateDummies() {
    return createDummies;
  }

  void setCreateDummies(boolean createDummies) {
    this.createDummies = createDummies;
  }

}
