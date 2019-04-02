package nl.naturalis.geneious.smpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.components.Dialogs.DialogIcon;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import nl.naturalis.geneious.ErrorCode;
import nl.naturalis.geneious.MessageProvider;
import nl.naturalis.geneious.MessageProvider.Message;
import nl.naturalis.geneious.util.SharedPreconditionValidator;

import static nl.naturalis.geneious.ErrorCode.NOT_CSV_OR_SPREADSHEET;
import static nl.naturalis.geneious.ErrorCode.SMPL_MISSING_SAMPLE_SHEET;
import static nl.naturalis.geneious.ErrorCode.SMPL_NO_DOCUMENTS_SELECTED;

public class SampleSheetImportOptions extends Options {

  private static final String SAMPLE_SHEET = "nl.naturalis.geneious.smpl.sampleSheet";
  private static final String CREATE_DUMMIES = "nl.naturalis.geneious.smpl.createDummies";
  private static final String LINES_TO_SKIP = "nl.naturalis.geneious.smpl.linesToSkip";
  private static final String DELIMITER = "nl.naturalis.geneious.smpl.delimiter";
  private static final String SHEET_NAME = "nl.naturalis.geneious.smpl.sheetName";

  private static final OptionValue EMPTY_SHEET_NAME = new OptionValue("0", "--- only when importing spreadsheet ---");
  
  private static final Set<String> ALLOWED_FILE_TYPES = ImmutableSet.of("csv", "tsv", "txt", "xls", "xlsx");

  private final List<AnnotatedPluginDocument> documents;
  private final FileSelectionOption sampleSheet;
  private final BooleanOption createDummies;
  private final IntegerOption linesToSkip;
  private final ComboBoxOption<OptionValue> delimiter;
  private final ComboBoxOption<OptionValue> sheetName;

  public SampleSheetImportOptions(List<AnnotatedPluginDocument> documents) {
    this.documents = documents;
    sampleSheet = addFileSelectionOption();
    createDummies = addCreateDummiesOption();
    linesToSkip = addLinesToSkipOption();
    delimiter = addDelimiterOption();
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
    if (documents.size() == 0 && createDummies.getValue() == Boolean.FALSE) {
      return MessageProvider.get(SMPL_NO_DOCUMENTS_SELECTED);
    }
    String ext = FilenameUtils.getExtension(sampleSheet.getValue());
    if (!ALLOWED_FILE_TYPES.contains(ext.toLowerCase())) {
      return MessageProvider.get(NOT_CSV_OR_SPREADSHEET, ext);
    }
    return null; // Signals to Geneious it is allowed to execute the performOperation (-ish) methods of our plugin
  }

  SampleSheetImportConfig createImportConfig() {
    SampleSheetImportConfig cfg = new SampleSheetImportConfig(documents);
    cfg.setCreateDummies(createDummies.getValue());
    cfg.setFile(new File(sampleSheet.getValue()));
    cfg.setSkipLines(linesToSkip.getValue());
    cfg.setDelimiter(delimiter.getValue().getName());
    cfg.setSheetNumber(Integer.parseInt(sheetName.getValue().getName()));
    return cfg;
  }

  private FileSelectionOption addFileSelectionOption() {
    FileSelectionOption opt = addFileSelectionOption(SAMPLE_SHEET, "Sample sheet", "");
    opt.setAllowMultipleSelection(false);
    opt.setFillHorizontalSpace(true);
    opt.setValue("");
    opt.setDescription("Select a sample sheet to import");
    return opt;
  }

  private BooleanOption addCreateDummiesOption() {
    String descr = "Create dummy sequences for rows containing new extract IDs";
    BooleanOption opt = addBooleanOption(CREATE_DUMMIES, descr, Boolean.TRUE);
    return opt;
  }

  private IntegerOption addLinesToSkipOption() {
    String descr = "The number of files to skip within the selected file. Should be at least 1 if the file starts with a header line";
    IntegerOption opt = addIntegerOption(LINES_TO_SKIP, "Lines to skip", 1, 0, Integer.MAX_VALUE);
    opt.setDescription(descr);
    return opt;
  }

  private ComboBoxOption<OptionValue> addDelimiterOption() {
    List<OptionValue> options = new ArrayList<>(8);
    options.add(new OptionValue("\t", "\\t"));
    options.add(new OptionValue(",", ","));
    options.add(new OptionValue(";", ";"));
    ComboBoxOption<OptionValue> opt = addComboBoxOption(DELIMITER, "Field separator", options, options.get(0));
    opt.setDescription("The character used to separate values within a row");
    String fileName = sampleSheet.getValue();
    if (fileName.endsWith(".csv") || fileName.endsWith(".tsv") || fileName.endsWith(".txt")) {
      opt.setEnabled(true);
    } else {
      opt.setEnabled(false);
    }
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
      delimiter.setEnabled(false);
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
      if (fileName.endsWith(".csv") || fileName.endsWith(".tsv") || fileName.endsWith(".txt")) {
        delimiter.setEnabled(true);
      }
      sheetName.setEnabled(false);
      sheetName.setPossibleValues(Arrays.asList(EMPTY_SHEET_NAME));
      sheetName.setDefaultValue(EMPTY_SHEET_NAME);
    }
  }

}
