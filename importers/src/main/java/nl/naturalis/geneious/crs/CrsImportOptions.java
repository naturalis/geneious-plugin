package nl.naturalis.geneious.crs;

import nl.naturalis.geneious.csv.CsvImportOptions;

/**
 * Configures a Geneious dialog requesting user input for the {@link CrsDocumentOperation CRS Import} operation. Once
 * the user click OK, this class produces a {@link CrsImportConfig} object, which is then passed on to the
 * {@link CrsSwingWorker}.
 * 
 * @author Ayco Holleman
 *
 */
class CrsImportOptions extends CsvImportOptions<CrsColumn, CrsImportConfig> {

  public CrsImportOptions() {
    super("crs");
  }

  /**
   * Produces a object containing all the user input for the Sample Sheet Import operation.
   */
  @Override
  public CrsImportConfig createImportConfig() {
    return initializeStandardOptions(new CrsImportConfig());
  }

}
