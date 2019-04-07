package nl.naturalis.geneious.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

import static com.biomatters.geneious.publicapi.components.Dialogs.showMessageDialog;

import static nl.naturalis.geneious.ErrorCode.CSV_NO_FILE_PROVIDED;
import static nl.naturalis.geneious.ErrorCode.CSV_UNSUPPORTED_FILE_TYPE;

public abstract class CsvImportOptions<T extends Enum<T>, U extends CsvImportConfig<T>> extends Options {

  private static final String FILE = "nl.naturalis.geneious.%s.file";
  private static final String LINES_TO_SKIP = "nl.naturalis.geneious.%s.skip";
  private static final String DELIMITER = "nl.naturalis.geneious.%s.delim";
  private static final String SHEET_NAME = "nl.naturalis.geneious.%s.sheet";

  private static final OptionValue NO_SHEET = new OptionValue("0", "--- (spreadsheet) ---");
  private static final OptionValue NO_DELIM = new OptionValue("0", "--- (CSV) ---");

  private static final List<OptionValue> DELIM_OPTIONS = Arrays.asList(
      new OptionValue("\t", "\\t"),
      new OptionValue(",", ","),
      new OptionValue(";", ";"),
      new OptionValue("|", "|"));

  private static final Set<String> ALLOWED_FILE_TYPES = ImmutableSet.of("csv", "tsv", "txt", "xls", "xlsx");

  private final String identifier;
  private final List<AnnotatedPluginDocument> documents;
  private final FileSelectionOption file;
  private final IntegerOption linesToSkip;
  private final ComboBoxOption<OptionValue> delimiter;
  private final ComboBoxOption<OptionValue> sheet; // Name of sheet (tab) within the spreadsheet

  public CsvImportOptions(List<AnnotatedPluginDocument> documents, String identifier) {
    this.documents = documents;
    this.identifier = identifier;
    this.file = addFileSelectionOption();
    this.linesToSkip = addLinesToSkipOption();
    this.delimiter = addDelimiterOption();
    this.sheet = addSheetNameOption();
    file.addChangeListener(this::fileChanged);
  }

  @Override
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
    if (StringUtils.isBlank(file.getValue())) {
      return MessageProvider.get(CSV_NO_FILE_PROVIDED);
    }
    String ext = FilenameUtils.getExtension(file.getValue());
    if (!ALLOWED_FILE_TYPES.contains(ext.toLowerCase())) {
      return MessageProvider.get(CSV_UNSUPPORTED_FILE_TYPE, ext);
    }
    return null; // Signals to Geneious it can continue
  }

  public abstract U createImportConfig();

  protected final U initializeStandardOptions(U cfg) {
    cfg.setSelectedDocuments(documents);
    cfg.setFile(new File(file.getValue()));
    cfg.setSkipLines(linesToSkip.getValue());
    cfg.setDelimiter(delimiter.getValue().getName());
    cfg.setSheetNumber(Integer.parseInt(sheet.getValue().getName()));
    return cfg;
  }

  protected String getLabelForFileName() {
    return "File";
  }

  protected int getNumberOfLinesToSkip() {
    return 1;
  }

  private FileSelectionOption addFileSelectionOption() {
    FileSelectionOption opt = addFileSelectionOption(name(FILE), getLabelForFileName(), "");
    opt.setAllowMultipleSelection(false);
    opt.setFillHorizontalSpace(true);
    opt.setValue("");
    opt.setDescription("Select a sample sheet to import (allowed formats: *.csv *.tsv *.txt *.xls *.xlsx)");
    return opt;
  }

  private IntegerOption addLinesToSkipOption() {
    String descr = "The number of lines to skip within the selected file";
    IntegerOption opt = addIntegerOption(name(LINES_TO_SKIP), "Lines to skip", getNumberOfLinesToSkip(), 0, Integer.MAX_VALUE);
    opt.setDescription(descr);
    return opt;
  }

  private ComboBoxOption<OptionValue> addDelimiterOption() {
    ComboBoxOption<OptionValue> opt = addComboBoxOption(name(DELIMITER), "Field separator", Arrays.asList(NO_DELIM), NO_DELIM);
    opt.setDescription("The character used to separate values within a row");
    if (CsvImportUtil.isCsvFile(file.getValue())) {
      opt.setPossibleValues(DELIM_OPTIONS);
      opt.setDefaultValue(DELIM_OPTIONS.get(0));
      opt.setEnabled(true);
    } else {
      opt.setEnabled(false);
    }
    return opt;
  }

  private ComboBoxOption<OptionValue> addSheetNameOption() {
    ComboBoxOption<OptionValue> opt = addComboBoxOption(name(SHEET_NAME), "Sheet name", Arrays.asList(NO_SHEET), NO_SHEET);
    opt.setFillHorizontalSpace(true);
    opt.setDescription("The name of the sheet (tab) within the spreadsheet.");
    if (CsvImportUtil.isSpreadsheet(file.getValue())) {
      opt.setEnabled(true);
      loadSheetNames();
    } else {
      opt.setEnabled(false);
    }
    return opt;
  }

  private void fileChanged() {
    sheet.setEnabled(false);
    delimiter.setEnabled(false);
    sheet.setPossibleValues(Arrays.asList(NO_SHEET));
    sheet.setDefaultValue(NO_SHEET);
    delimiter.setPossibleValues(Arrays.asList(NO_DELIM));
    delimiter.setDefaultValue(NO_DELIM);
    if (CsvImportUtil.isSpreadsheet(file.getValue())) {
      loadSheetNames();
      sheet.setEnabled(true);
    } else if (CsvImportUtil.isCsvFile(file.getValue())) {
      delimiter.setPossibleValues(DELIM_OPTIONS);
      delimiter.setDefaultValue(DELIM_OPTIONS.get(0));
      delimiter.setEnabled(true);
    } else {
      String msg = "Unsupported file type";
      showMessageDialog(msg, msg, GuiUtilities.getMainFrame(), DialogIcon.ERROR);
    }
  }

  private void loadSheetNames() {
    try {
      Workbook workbook = WorkbookFactory.create(new File(file.getValue()));
      List<OptionValue> names = new ArrayList<>(workbook.getNumberOfSheets());
      for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
        names.add(new OptionValue(String.valueOf(i), workbook.getSheetAt(i).getSheetName()));
      }
      sheet.setPossibleValues(names);
      sheet.setDefaultValue(names.get(0));
    } catch (Exception e) {
      String title = "Error reading spreadsheet";
      String msg = title + ": " + e;
      showMessageDialog(msg, title, GuiUtilities.getMainFrame(), DialogIcon.ERROR);
    }
  }

  private String name(String format) {
    return String.format(format, identifier);
  }

}
