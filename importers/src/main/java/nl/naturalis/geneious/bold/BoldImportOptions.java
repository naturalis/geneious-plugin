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

  @Override
  protected int getDefaultNumLinesToSkip() {
    return 3;
  }

  @Override
  protected boolean supportSpreadsheet() {
    return true;
  }
}
