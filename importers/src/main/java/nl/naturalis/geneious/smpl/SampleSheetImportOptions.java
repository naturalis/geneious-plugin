package nl.naturalis.geneious.smpl;

import java.io.File;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;

public class SampleSheetImportOptions extends Options {

  private static final String SAMPLE_SHEET = "nl.naturalis.geneious.samplesheet.sampleSheet";
  private static final String CREATE_DUMMIES = "nl.naturalis.geneious.samplesheet.createDummies";
  private static final String LINES_TO_SKIP = "nl.naturalis.geneious.samplesheet.linesToSkip";
  private static final String SHEET_NO = "nl.naturalis.geneious.samplesheet.sheetNo";

  private final AnnotatedPluginDocument[] documents;
  private final FileSelectionOption sampleSheet;
  private final BooleanOption createDummies;
  private final IntegerOption linesToSkip;
  private final IntegerOption sheetNo;

  public SampleSheetImportOptions(AnnotatedPluginDocument[] documents) {
    this.documents = documents;
    sampleSheet = addFileSelectionOption(SAMPLE_SHEET, "Sample sheet", "");
    sampleSheet.setAllowMultipleSelection(true);
    createDummies = addBooleanOption(CREATE_DUMMIES, "Create dummy sequences for new extract IDs", Boolean.TRUE);
    linesToSkip = addIntegerOption(LINES_TO_SKIP, "Lines to skip", 1, 0, Integer.MAX_VALUE);
    sheetNo = addIntegerOption(SHEET_NO, "Sheet number", 1, 1, Integer.MAX_VALUE);
  }

  UserInput createImportConfig() {
    UserInput cfg = new UserInput(documents);
    cfg.setCreateDummies(createDummies.isEnabled());
    cfg.setFile(new File(sampleSheet.getValue()));
    cfg.setSkipLines(linesToSkip.getValue());
    cfg.setSheetNumber(sheetNo.getValue());
    return cfg;
  }

}
