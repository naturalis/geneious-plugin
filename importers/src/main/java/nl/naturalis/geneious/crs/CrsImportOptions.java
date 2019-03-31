package nl.naturalis.geneious.crs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.components.Dialogs.DialogIcon;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import nl.naturalis.geneious.ErrorCode;
import nl.naturalis.geneious.MessageProvider;
import nl.naturalis.geneious.MessageProvider.Message;
import nl.naturalis.geneious.util.SharedPreconditionValidator;

import static nl.naturalis.geneious.ErrorCode.SMPL_MISSING_SAMPLE_SHEET;

public class CrsImportOptions extends Options {

  private static final String SAMPLE_SHEET = "nl.naturalis.geneious.samplesheet.sampleSheet";
  private static final String LINES_TO_SKIP = "nl.naturalis.geneious.samplesheet.linesToSkip";
  private static final String SHEET_NAME = "nl.naturalis.geneious.samplesheet.sheetName";

  private static final OptionValue EMPTY_SHEET_NAME = new OptionValue("0", "--- only when importing spreadsheet ---");

  private final List<AnnotatedPluginDocument> documents;
  private final FileSelectionOption sampleSheet;
  private final IntegerOption linesToSkip;
  private final ComboBoxOption<OptionValue> sheetName;

  public CrsImportOptions(List<AnnotatedPluginDocument> documents) {

    this.documents = documents;

    sampleSheet = addFileSelectionOption();
    linesToSkip = addLinesToSkipOption();
    sheetName = addSheetNameOption();

    sampleSheet.addChangeListener(this::fileChanged);

  }

  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if (msg != null) {
      return msg;
    }
    SharedPreconditionValidator validator = new SharedPreconditionValidator(documents);
    Message message = validator.validate();
    if (message.getCode() != ErrorCode.OK) {
      return message.getMessage();
    }
    if (StringUtils.isBlank(sampleSheet.getValue())) {
      return MessageProvider.get(SMPL_MISSING_SAMPLE_SHEET);

    }
    return null; // Signals to Geneious it is allowed to execute the performOperation (-ish) methods of our plugin
  }

  CrsImportConfig createImportConfig() {
    CrsImportConfig cfg = new CrsImportConfig(documents);
    cfg.setFile(new File(sampleSheet.getValue()));
    cfg.setSkipLines(linesToSkip.getValue());
    cfg.setSheetNumber(Integer.parseInt(sheetName.getValue().getName()));
    return cfg;
  }

  private FileSelectionOption addFileSelectionOption() {
    FileSelectionOption opt = addFileSelectionOption(SAMPLE_SHEET, "Sample sheet", "");
    opt.setAllowMultipleSelection(false);
    opt.setFillHorizontalSpace(true);
    opt.setValue("");
    opt.setDescription("Select a sample sheet to import (allowed formats: CSV, TSV, XLS, CLSX)");
    return opt;
  }

  private IntegerOption addLinesToSkipOption() {
    String descr = "The number of files to skip within the selected file. Should be at least 1 if the file starts with a header line";
    IntegerOption opt = addIntegerOption(LINES_TO_SKIP, "Lines to skip", 1, 0, Integer.MAX_VALUE);
    opt.setDescription(descr);
    return opt;
  }

  private ComboBoxOption<OptionValue> addSheetNameOption() {
    ComboBoxOption<OptionValue> opt = addComboBoxOption(SHEET_NAME, "Sheet name", Arrays.asList(EMPTY_SHEET_NAME), EMPTY_SHEET_NAME);
    opt.setFillHorizontalSpace(true);
    opt.setDescription("The name of the sheet (tab) within the spreadsheet.");
    String fileName = sampleSheet.getValue();
    if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
      opt.setEnabled(true);
    } else {
      opt.setEnabled(false);
    }
    return opt;
  }

  private void fileChanged() {
    String fileName = sampleSheet.getValue();
    if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
      sheetName.setEnabled(true);
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
      sheetName.setEnabled(false);
      sheetName.setPossibleValues(Arrays.asList(EMPTY_SHEET_NAME));
      sheetName.setDefaultValue(EMPTY_SHEET_NAME);
    }
  }

}
