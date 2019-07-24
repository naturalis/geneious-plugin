package nl.naturalis.geneious.crs;

import nl.naturalis.geneious.csv.CsvImportOptions;

/**
 * Underpins the user input dialog for the {@link CrsDocumentOperation CRS Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class CrsImportOptions extends CsvImportOptions<CrsColumn, CrsImportConfig> {

  CrsImportOptions() {
    super("crs");
  }

  @Override
  public CrsImportConfig configureOperation() {
    return configureDefaults(new CrsImportConfig());
  }

}
