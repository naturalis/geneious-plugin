package nl.naturalis.geneious.crs;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.csv.CsvImportOptions;

/**
 * Sets up a Geneious dialog requesting user input for the {@link CrsDocumentOperation CRS Import} operation.
 * Once the user click OK, this class produces a {@link CrsImportConfig} object, which is then passed on to
 * the {@link CrsImporter}.
 * 
 * @author Ayco Holleman
 *
 */
class CrsImportOptions extends CsvImportOptions<CrsColumn, CrsImportConfig> {

  public CrsImportOptions(List<AnnotatedPluginDocument> documents) {
    super(documents, "crs");
  }

  @Override
  public CrsImportConfig createImportConfig() {
    return initializeStandardOptions(new CrsImportConfig());
  }

}
