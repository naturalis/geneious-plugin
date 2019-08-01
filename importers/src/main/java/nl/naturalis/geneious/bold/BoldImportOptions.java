package nl.naturalis.geneious.bold;

import nl.naturalis.geneious.csv.CsvImportOptions;

/**
 * Underpins the user input dialog for the {@link BoldDocumentOperation BOLD Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class BoldImportOptions extends CsvImportOptions<BoldColumn, BoldImportConfig> {

  BoldImportOptions() {
    super("bold");
  }

  /**
   * Produces a object containing all the user input for the BOLD Import operation.
   */
  @Override
  public BoldImportConfig configureOperation() {
    return configureDefaults(new BoldImportConfig());
  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if(msg != null) {
      return msg;
    }
    if(linesToSkip.getValue() == 0) {
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
  
  protected boolean isSpreadsheetWithFormulas() {
    return true;
  }
}
