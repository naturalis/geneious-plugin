package nl.naturalis.geneious.csv;

import static com.biomatters.geneious.publicapi.components.Dialogs.showMessageDialog;
import static nl.naturalis.geneious.csv.CsvImportUtil.isCsvFile;
import static nl.naturalis.geneious.csv.CsvImportUtil.isSpreadsheet;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.biomatters.geneious.publicapi.components.Dialogs.DialogIcon;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import com.google.common.base.Charsets;

import nl.naturalis.geneious.NaturalisPluginException;
import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.gui.ShowDialog;
import nl.naturalis.geneious.util.CharsetDetector;

/**
 * Abstract base class for classes underpinning a Geneious dialog that requests user input for the import of CSV-like
 * files.
 * 
 * @author Ayco Holleman
 *
 * @param <T> An {@code enum} providing symbolic constants for the columns in a CSV-like file.
 * @param <U> The type of object that will contain the user-provided values.
 */
public abstract class CsvImportOptions<T extends Enum<T>, U extends CsvImportConfig<T>> extends OperationOptions<U> {

  private static final String FILE = "nl.naturalis.geneious.%s.file";
  private static final String LINES_TO_SKIP = "nl.naturalis.geneious.%s.skip";
  private static final String DELIMITER = "nl.naturalis.geneious.%s.delim";
  private static final String SHEET_NAME = "nl.naturalis.geneious.%s.sheet";

  private static final OptionValue OPT_NOT_APPLICABLE = new OptionValue("0", "  n/a  ");

  private static final OptionValue DELIM_INIT = new OptionValue("0", "  --- csv/tsv/txt ---  ");
  private static final OptionValue SHEET_INIT = new OptionValue("0", "  --- spreadsheet ---  ");

  private static final List<OptionValue> DELIM_OPTIONS = Arrays.asList(
      new OptionValue("\t", "  tab  "),
      new OptionValue(",", "  comma  "),
      new OptionValue(";", "  semi-colon  "),
      new OptionValue("|", "  pipe  "));

  // An identifier provided by the subclasses to differentiate the option names.
  protected final String identifier;
  protected final FileSelectionOption file;
  protected final IntegerOption linesToSkip;
  protected final ComboBoxOption<OptionValue> delimiter;
  protected final ComboBoxOption<OptionValue> sheet; // Name of sheet (tab) within the spreadsheet

  public CsvImportOptions(String identifier) {
    this.identifier = identifier;
    this.file = addFileSelectionOption();
    this.linesToSkip = addLinesToSkipOption();
    this.delimiter = addDelimiterOption();
    this.sheet = supportSpreadsheet() ? addSheetNameOption() : null;
    file.addChangeListener(this::fileChanged);
  }

  /**
   * Verifies the validity of the user input. Returns null if the user input is valid, otherwise a message indicating
   * what's wrong.
   */
  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if(msg != null) {
      return msg;
    }
    if(StringUtils.isBlank(file.getValue())) {
      return "Please select a CSV file or spreadsheet to import";
    }
    String ext = getExtension(file.getValue());
    if(!supportedFileTypes().contains(ext.toLowerCase())) {
      String fmt = "Unsupported file type: %s. Supported file types: %s";
      return String.format(fmt, ext, supportedFileTypesAsString());
    }
    if(isCsvFile(file.getValue())) {
      try {
        Charset charset = CharsetDetector.detectEncoding(Paths.get(file.getValue()));
        if(charset.equals(Charsets.UTF_8)) {
          return null;
        }
        String fileName = FilenameUtils.getName(file.getValue());
        if(ShowDialog.continueWithDetectedCharset(fileName, charset)) {
          return null;
        }
        return "Please select another file";
      } catch(IOException e) {
        throw new NaturalisPluginException(e);
      }
    }
    return null; // Signals to Geneious it can continue
  }

  /**
   * Initializes the provided configuration object with settings common to all operations that import CSV or CSV-like
   * files (Sample Sheet Import, CRS Import and BOLD Import), for example the field delimiter.
   * 
   * @param config
   * @return
   */
  @Override
  protected U configureDefaults(U config) {
    super.configureDefaults(config);
    config.setFile(new File(file.getValue()));
    config.setSkipLines(linesToSkip.getValue());
    config.setDelimiter(delimiter.getValue().getName());
    if(supportSpreadsheet()) {
      config.setSheetNumber(Integer.parseInt(sheet.getValue().getName()));
    }
    return config;
  }

  /**
   * The text to display before the file selection field in the dialog. Default: "File".
   * 
   * @return
   */
  protected String getDefaultFileSelectionLabel() {
    return "File";
  }

  /**
   * Returns the default number of lines to skip (displayed when the dialog is opened for the very first time).
   * 
   * @return
   */
  protected int getDefaultNumLinesToSkip() {
    return 1;
  }

  /**
   * Whether or not to support spreadsheets (default: {@code false}).
   * 
   * @return
   */
  protected boolean supportSpreadsheet() {
    return false;
  }

  private FileSelectionOption addFileSelectionOption() {
    FileSelectionOption opt = addFileSelectionOption(
        name(FILE),
        getDefaultFileSelectionLabel(),
        "",
        new String[0],
        "Select",
        (x, y) -> supportedFileTypes().contains(getExtension(y)));
    opt.setAllowMultipleSelection(false);
    opt.setFillHorizontalSpace(true);
    opt.setSelectionType(JFileChooser.FILES_ONLY);
    opt.setValue("");
    opt.setDescription("Select a sample sheet to import. Supported formats: " + supportedFileTypesAsString());
    return opt;
  }

  private IntegerOption addLinesToSkipOption() {
    String descr = "The number of lines to skip within the selected file";
    IntegerOption opt = addIntegerOption(name(LINES_TO_SKIP), "Lines to skip", getDefaultNumLinesToSkip(), 0, Integer.MAX_VALUE);
    opt.setDescription(descr);
    return opt;
  }

  private ComboBoxOption<OptionValue> addDelimiterOption() {
    ComboBoxOption<OptionValue> opt = addComboBoxOption(name(DELIMITER), "Field separator", Arrays.asList(DELIM_INIT), DELIM_INIT);
    opt.setDescription("The character used to separate values within a row");
    opt.setEnabled(false);
    return opt;
  }

  private ComboBoxOption<OptionValue> addSheetNameOption() {
    ComboBoxOption<OptionValue> opt = addComboBoxOption(name(SHEET_NAME), "Sheet name", Arrays.asList(SHEET_INIT), SHEET_INIT);
    opt.setFillHorizontalSpace(true);
    opt.setDescription("The name of the sheet (a.k.a. tab) within the spreadsheet.");
    opt.setEnabled(false);
    return opt;
  }

  private void fileChanged() {
    if(StringUtils.isBlank(file.getValue())) {
      /*
       * When a file has been selected, and then you select another file, the change listener apparently fires twice. The
       * first time the file is empty again. The second time you get the new file.
       */
      return;
    }
    if(supportSpreadsheet()) { // must check that, otherwise the sheet isn't even there.
      sheet.setEnabled(false);
    }
    delimiter.setEnabled(false);
    if(supportSpreadsheet() && isSpreadsheet(file.getValue())) {
      loadSheetNames();
      sheet.setEnabled(true);
      delimiter.setPossibleValues(Arrays.asList(OPT_NOT_APPLICABLE));
      delimiter.setDefaultValue(OPT_NOT_APPLICABLE);
    } else if(CsvImportUtil.isCsvFile(file.getValue())) {
      delimiter.setPossibleValues(DELIM_OPTIONS);
      delimiter.setDefaultValue(DELIM_OPTIONS.get(0));
      delimiter.setEnabled(true);
      if(supportSpreadsheet()) {
        sheet.setPossibleValues(Arrays.asList(OPT_NOT_APPLICABLE));
        sheet.setDefaultValue(OPT_NOT_APPLICABLE);
      }
    } else {
      if(supportSpreadsheet()) {
        sheet.setPossibleValues(Arrays.asList(SHEET_INIT));
        sheet.setDefaultValue(SHEET_INIT);
      }
      delimiter.setPossibleValues(Arrays.asList(DELIM_INIT));
      delimiter.setDefaultValue(DELIM_INIT);
      String title = "Unsupported file type";
      StringBuilder sb = new StringBuilder(32);
      sb.append(title);
      String ext = getExtension(file.getValue());
      if(StringUtils.isNotBlank(ext)) {
        sb.append(": *.").append(ext);
      }
      showMessageDialog(sb.toString(), title, GuiUtilities.getMainFrame(), DialogIcon.ERROR);
    }
  }

  private void loadSheetNames() {
    try {
      Workbook workbook = WorkbookFactory.create(new File(file.getValue()));
      List<OptionValue> names = new ArrayList<>(workbook.getNumberOfSheets());
      for(int i = 0; i < workbook.getNumberOfSheets(); ++i) {
        names.add(new OptionValue(String.valueOf(i), "  " + workbook.getSheetAt(i).getSheetName() + "  "));
      }
      sheet.setPossibleValues(names);
      sheet.setDefaultValue(names.get(0));
    } catch(Exception e) {
      String title = "Error reading spreadsheet";
      String msg = title + ": " + e;
      showMessageDialog(msg, title, GuiUtilities.getMainFrame(), DialogIcon.ERROR);
    }
  }

  private String name(String format) {
    return String.format(format, identifier);
  }

  private ArrayList<String> supportedFileTypes() {
    ArrayList<String> types = new ArrayList<>(4);
    types.add("csv");
    types.add("tsv");
    types.add("txt");
    if(supportSpreadsheet()) {
      types.add("xls");
    }
    return types;
  }

  private String supportedFileTypesAsString() {
    return supportedFileTypes().stream().map(s -> "*." + s).collect(Collectors.joining("  "));
  }

}
