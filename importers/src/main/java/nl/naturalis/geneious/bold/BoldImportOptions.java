package nl.naturalis.geneious.bold;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.csv.CsvImportOptions;

/**
 * Sets up a Geneious dialog requesting user input for the {@link BoldDocumentOperation BOLD Import} operation. Once the
 * user click OK, this class produces a {@link BoldImportConfig} object, which is then passed on to the
 * {@link BoldImporter}.
 * 
 * @author Ayco Holleman
 *
 */
class BoldImportOptions extends CsvImportOptions<BoldColumn, BoldImportConfig> {

  public BoldImportOptions(List<AnnotatedPluginDocument> documents) {
    super(documents, "bold");
  }

  /**
   * Produces a object containing all the user input for the BOLD Import operation.
   */
  @Override
  public BoldImportConfig createImportConfig() {
    return initializeStandardOptions(new BoldImportConfig());
  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if (msg != null) {
      return msg;
    }
    if (linesToSkip.getValue() == 0) {
      return "\"Lines to skip\" must not be 0 (zero). Bold files must have at least one header line containing " +
          "the column names (usually line 3).";
    }
    return null;
  }

  /**
   * Returns the default number of lines to skip within the spreadsheet.
   */
  @Override
  protected int getDefaultNumLinesToSkip() {
    return 3;
  }

  /**
   * Returns {@code true}.
   */
  @Override
  protected boolean supportSpreadsheet() {
    return true;
  }
}
