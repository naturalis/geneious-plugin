package nl.naturalis.geneious.smpl;

import com.biomatters.geneious.publicapi.documents.DocumentUtilities;

import nl.naturalis.geneious.csv.CsvImportOptions;

/**
 * Configures a Geneious dialog requesting user input for the {@link SampleSheetDocumentOperation Sample Sheet Import}
 * operation. Once the user click OK, this class produces a {@link SampleSheetImportConfig} object, which is then passed
 * on to the {@link SampleSheetSwingWorker}.
 * 
 * @author Ayco Holleman
 *
 */
class SampleSheetImportOptions extends CsvImportOptions<SampleSheetColumn, SampleSheetImportConfig> {

  private static final String CREATE_DUMMIES_LABEL = "Create dummy sequences for rows containing new extract IDs";

  private final BooleanOption dummies;

  public SampleSheetImportOptions() {
    super("smpl");
    this.dummies = addDummiesOption();
  }

  /**
   * Produces a object containing all the user input for the Sample Sheet Import operation.
   */
  @Override
  public SampleSheetImportConfig configureOperation() {
    SampleSheetImportConfig cfg = initializeStandardOptions(new SampleSheetImportConfig());
    cfg.setCreateDummies(dummies.getValue());
    return cfg;
  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if(msg != null) {
      return msg;
    }
    if(DocumentUtilities.getSelectedDocuments().isEmpty() && !dummies.getValue().booleanValue()) {
      return String.format("Please select at least one document or check \"%s\"", CREATE_DUMMIES_LABEL);
    }
    return null;
  }

  /**
   * Returns the text in front of the file selection field: "Sample sheet".
   */
  @Override
  protected String getDefaultFileSelectionLabel() {
    return "Sample sheet";
  }

  private BooleanOption addDummiesOption() {
    String name = "nl.naturalis.geneious.smpl.dummies";
    BooleanOption opt = addBooleanOption(name, CREATE_DUMMIES_LABEL, Boolean.TRUE);
    opt.setHelp("You can choose to create placeholder documents (a.k.a. dummies) for sample sheet rows that refer"
        + "to yet-to-be imported AB1 or fasta sequences. The placeholder document then acquires the annotations "
        + "present in the sample sheet row. Once you import the real sequence, the annotations will be copied "
        + "from the placeholder document to the sequence document, and the placeholder document will be deleted.");
    return opt;
  }

}
