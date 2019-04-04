package nl.naturalis.geneious.smpl;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.csv.CsvImportOptions;

public class SampleSheetImportOptions extends CsvImportOptions<SampleSheetColumn, SampleSheetImportConfig> {

  private final BooleanOption dummies;

  public SampleSheetImportOptions(List<AnnotatedPluginDocument> documents) {
    super(documents, "smpl");
    this.dummies = addDummiesOption();
  }

  private BooleanOption addDummiesOption() {
    String name = "nl.naturalis.geneious.smpl.dummies";
    String descr = "Create dummy sequences for rows containing new extract IDs";
    BooleanOption opt = addBooleanOption(name, descr, Boolean.TRUE);
    return opt;
  }

  @Override
  protected String getLabelForFileName() {
    return "Sample sheet";
  }

  @Override
  public SampleSheetImportConfig createImportConfig() {
    SampleSheetImportConfig cfg = initializeStandardOptions(new SampleSheetImportConfig());
    cfg.setCreateDummies(dummies.getValue());
    return cfg;
  }

}
