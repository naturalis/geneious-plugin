package nl.naturalis.geneious.smpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.components.Dialogs.DialogIcon;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class SampleSheetImportOptions extends Options {

  private static final String SAMPLE_SHEET = "nl.naturalis.geneious.samplesheet.sampleSheet";
  private static final String CREATE_DUMMIES = "nl.naturalis.geneious.samplesheet.createDummies";
  private static final String LINES_TO_SKIP = "nl.naturalis.geneious.samplesheet.linesToSkip";
  private static final String SHEET_NAME = "nl.naturalis.geneious.samplesheet.sheetName";

  private static final OptionValue EMPTY_SHEET_NAME = new OptionValue("0", "--- only when importing spreadsheet ---");

  private final List<AnnotatedPluginDocument> documents;
  private final FileSelectionOption sampleSheet;
  private final BooleanOption createDummies;
  private final IntegerOption linesToSkip;
  private final ComboBoxOption<OptionValue> sheetName;

  public SampleSheetImportOptions(List<AnnotatedPluginDocument> documents) {

    this.documents = documents;

    sampleSheet = addFileSelectionOption(SAMPLE_SHEET, "Sample sheet", "");
    sampleSheet.setAllowMultipleSelection(false);
    sampleSheet.setFillHorizontalSpace(true);
    sampleSheet.setValue("");
    sampleSheet.setDescription("Select a sample sheet to import (allowed formats: CSV, TSV, XLS, CLSX)");

    createDummies = addBooleanOption(CREATE_DUMMIES, "Create dummy sequences for new extract IDs", Boolean.TRUE);

    linesToSkip = addIntegerOption(LINES_TO_SKIP, "Lines to skip", 1, 0, Integer.MAX_VALUE);
    linesToSkip.setDescription("The number of files to skip with the selected file.");

    sheetName = addComboBoxOption(SHEET_NAME, "Sheet name", Arrays.asList(EMPTY_SHEET_NAME), EMPTY_SHEET_NAME);
    sheetName.setFillHorizontalSpace(true);
    sheetName.setDescription("The name of the sheet (tab) within the spreadsheet.");

    sampleSheet.addChangeListener(this::loadSheetNames);
  }

  SampleSheetImportConfig createImportConfig() {
    SampleSheetImportConfig cfg = new SampleSheetImportConfig(documents);
    cfg.setCreateDummies(createDummies.isEnabled());
    cfg.setFile(new File(sampleSheet.getValue()));
    cfg.setSkipLines(linesToSkip.getValue());
    cfg.setSheetNumber(Integer.parseInt(sheetName.getValue().getName()));
    return cfg;
  }

  private void loadSheetNames() {
    String fileName = sampleSheet.getValue();
    if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
      try {
        Workbook workbook = WorkbookFactory.create(new File(sampleSheet.getValue()));
        List<OptionValue> names = new ArrayList<>(workbook.getNumberOfSheets());
        for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
          names.add(new OptionValue(String.valueOf(i), workbook.getSheetAt(i).getSheetName()));
        }
        sheetName.setPossibleValues(names);
        sheetName.setDefaultValue(names.get(0));
      } catch (Exception e) {
        Dialogs.showMessageDialog("Error reading spreadsheet: " + e,
            "Error reading spreadsheet",
            GuiUtilities.getMainFrame(),
            DialogIcon.ERROR);
      }
    } else {
      sheetName.setPossibleValues(Arrays.asList(EMPTY_SHEET_NAME));
      sheetName.setDefaultValue(EMPTY_SHEET_NAME);
    }
  }

}
