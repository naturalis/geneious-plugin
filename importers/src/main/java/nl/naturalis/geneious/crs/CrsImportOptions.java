package nl.naturalis.geneious.crs;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.csv.CsvImportOptions;

class CrsImportOptions extends CsvImportOptions<CrsColumn, CrsImportConfig> {

  public CrsImportOptions(List<AnnotatedPluginDocument> documents) {
    super(documents, "crs");
  }

  @Override
  public CrsImportConfig createImportConfig() {
    return initializeStandardOptions(new CrsImportConfig());
  }

}
