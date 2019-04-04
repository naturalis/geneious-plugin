package nl.naturalis.geneious.bold;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.csv.CsvImportOptions;

class BoldImportOptions extends CsvImportOptions<BoldColumn, BoldImportConfig> {

  public BoldImportOptions(List<AnnotatedPluginDocument> documents) {
    super(documents, "bold");
  }

  @Override
  public BoldImportConfig createImportConfig() {
    return initializeStandardOptions(new BoldImportConfig());
  }

  protected int getNumberOfLinesToSkip() {
    return 3;
  }

}
