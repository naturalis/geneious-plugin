package nl.naturalis.geneious.bold;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.MessageProvider;
import nl.naturalis.geneious.csv.CsvImportOptions;

import static nl.naturalis.geneious.ErrorCode.CSV_UNSUPPORTED_FILE_TYPE;

class BoldImportOptions extends CsvImportOptions<BoldColumn, BoldImportConfig> {

  public BoldImportOptions(List<AnnotatedPluginDocument> documents) {
    super(documents, "bold");
  }

  @Override
  public BoldImportConfig createImportConfig() {
    return initializeStandardOptions(new BoldImportConfig());
  }

  @Override
  protected int getDefaultNumLinesToSkip() {
    return 3;
  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if (msg != null) {
      return msg;
    }
    if (linesToSkip.getValue() == 0) {
      return MessageProvider.get(CSV_UNSUPPORTED_FILE_TYPE);
    }
    return null;
  }

}
