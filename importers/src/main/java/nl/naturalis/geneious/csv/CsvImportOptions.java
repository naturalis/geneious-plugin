package nl.naturalis.geneious.csv;

import static com.biomatters.geneious.publicapi.components.Dialogs.showMessageDialog;
import static java.util.Arrays.asList;
import static nl.naturalis.geneious.csv.CsvImportUtil.isCsvFile;
import static nl.naturalis.geneious.csv.CsvImportUtil.isSpreadsheet;
import static nl.naturalis.geneious.util.RuntimeSettings.runtimeSettings;
import static org.apache.commons.io.FilenameUtils.getExtension;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;
import com.biomatters.geneious.publicapi.components.Dialogs.DialogIcon;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import com.google.common.base.Charsets;
import nl.naturalis.common.StringMethods;
import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.gui.GuiUtils;
import nl.naturalis.geneious.gui.ScrollableTreeViewer;
import nl.naturalis.geneious.gui.ShowDialog;
import nl.naturalis.geneious.gui.TextStyle;
import nl.naturalis.geneious.util.CharsetDetector;
import nl.naturalis.geneious.util.RuntimeSetting;

/**
 * Abstract base class for classes underpinning a Geneious dialog that requests user input for the import of CSV-like files.
 * 
 * @author Ayco Holleman
 *
 * @param <T> An {@code enum} providing symbolic constants for the columns in a CSV-like file.
 * @param <U> The type of object that will contain the user-provided values.
 */
public abstract class CsvImportOptions<T extends Enum<T>, U extends CsvImportConfig<T>> extends OperationOptions<U> {

  private static final String LINES_TO_SKIP = "nl.naturalis.geneious.%s.skip";
  private static final String DELIMITER = "nl.naturalis.geneious.%s.delim";
  private static final String SHEET_NAME = "nl.naturalis.geneious.%s.sheet";
  private static final String SELECTED_DELIM = "nl.naturalis.geneious.%s.selectedDelim";
  private static final String SELECTED_SHEET = "nl.naturalis.geneious.%s.selectedSheet";

  private static final OptionValue NOT_APPLICABLE = new OptionValue("-1", "  n/a  ");
  private static final OptionValue DELIM_INIT = new OptionValue("-1", "  --- csv/tsv/txt ---  ");
  private static final OptionValue SHEET_INIT = new OptionValue("-1", "  --- spreadsheet ---  ");

  private static final List<OptionValue> DELIM_OPTIONS = asList(
      new OptionValue("\t", "  tab  "),
      new OptionValue(",", "  comma  "),
      new OptionValue(";", "  semi-colon  "),
      new OptionValue("|", "  pipe  "));

  // An identifier provided by the subclasses to differentiate the option names.
  private final String identifier;

  private final JTextField sourceFileDisplay;
  private final IntegerOption linesToSkip;
  private final ComboBoxOption<OptionValue> delimiter;
  private final ComboBoxOption<OptionValue> sheet;
  private final IntegerOption selectedDelim;
  private final IntegerOption selectedSheet;

  private boolean sourceFileSelected = false;
  private String fileChangedErrorMessage = null;

  public CsvImportOptions(String identifier) {
    this.identifier = identifier;

    this.sourceFileDisplay = addFileSelectionOption();
    addCustomComponent(sourceFileDisplay);

    this.linesToSkip = addLinesToSkipOption();
    this.delimiter = addDelimiterOption();
    this.sheet = supportSpreadsheet() ? addSheetOption() : null;
    this.selectedDelim = addSelectedDelimOption();
    this.selectedSheet = addSelectedSheetOption();
  }

  /**
   * Verifies the validity of the user input. Returns null if the user input is valid, otherwise a message indicating what's wrong.
   */
  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if (msg != null) {
      return msg;
    }
    if (fileChangedErrorMessage != null) {
      return fileChangedErrorMessage;
    }
    if (!sourceFileSelected) {
      return "Please select the " + getFileType() + " to be imported";
    }
    String path = sourceFileDisplay.getToolTipText();
    String ext = getExtension(path);
    if (!supportedFileNameExtensions().contains(ext.toLowerCase())) {
      String fmt = "Unsupported file type: %s. Supported file types: %s";
      return String.format(fmt, ext, supportedExtensionsAsString());
    }
    if (isCsvFile(path)) {
      try {
        Charset charset = CharsetDetector.detectEncoding(Paths.get(path));
        if (charset.equals(Charsets.UTF_8)) {
          return null;
        }
        String fileName = FilenameUtils.getName(path);
        if (ShowDialog.continueWithDetectedCharset(fileName, charset)) {
          return null;
        }
        return "Please select another file";
      } catch (IOException e) {
        return e.getMessage();
      }
    }
    return null; // Signals to Geneious it can continue
  }

  /**
   * Initializes the provided configuration object with settings common to all operations that import CSV or CSV-like files (Sample Sheet
   * Import, CRS Import and BOLD Import), for example the field delimiter.
   * 
   * @param config
   * @return
   */
  @Override
  protected U configureDefaults(U config) {
    super.configureDefaults(config);
    config.setFile(new File(sourceFileDisplay.getToolTipText()));
    config.setSkipLines(linesToSkip.getValue());
    config.setDelimiter(delimiter.getValue().getName());
    if (supportSpreadsheet()) {
      config.setSheetNumber(Integer.parseInt(sheet.getValue().getName()));
    }
    return config;
  }

  /**
   * Returns the current value of the "Lines to skip" field in the options panel.
   * 
   * @return
   */
  protected final int getLinesToSkip() {
    return linesToSkip.getValue();
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
   * Whether or not to support spreadsheets (default: {@code false}). Can be overriden by subclasses.
   * 
   * @return
   */
  protected boolean supportSpreadsheet() {
    return false;
  }

  private JTextField addFileSelectionOption() {
    JTextField fileDisplay = new JTextField();
    fileDisplay.setEditable(false);
    Dimension d = new Dimension(ScrollableTreeViewer.PREFERRED_WIDTH, fileDisplay.getPreferredSize().height);
    fileDisplay.setPreferredSize(d);
    fileDisplay.addMouseListener(getMouseListener());
    TextStyle.ENTER_VALUE.applyTo(fileDisplay, "Click to select a " + getFileType());
    return fileDisplay;
  }

  private IntegerOption addLinesToSkipOption() {
    String descr = "The number of lines to skip within the selected file";
    IntegerOption opt = addIntegerOption(getOptionName(LINES_TO_SKIP), "Lines to skip", getDefaultNumLinesToSkip(), 0, Integer.MAX_VALUE);
    opt.setDescription(descr);
    return opt;
  }

  private ComboBoxOption<OptionValue> addDelimiterOption() {
    ComboBoxOption<OptionValue> opt = addComboBoxOption(getOptionName(DELIMITER), "Field separator", asList(DELIM_INIT), DELIM_INIT);
    opt.setDescription("The character used to separate values within a row");
    opt.setEnabled(false);
    opt.addChangeListener(() -> {
      OptionValue v = opt.getPossibleOptionValues().get(0);
      if (v != DELIM_INIT && v != NOT_APPLICABLE) {
        for (int i = 0; i < opt.getPossibleOptionValues().size(); ++i) {
          if (opt.getPossibleOptionValues().get(i) == opt.getValue()) {
            selectedDelim.setValue(i);
          }
        }
      }
    });
    return opt;
  }

  private ComboBoxOption<OptionValue> addSheetOption() {
    ComboBoxOption<OptionValue> opt = addComboBoxOption(getOptionName(SHEET_NAME), "Sheet name", asList(SHEET_INIT), SHEET_INIT);
    opt.setFillHorizontalSpace(true);
    opt.setDescription("The name of the sheet (a.k.a. tab) within the spreadsheet.");
    opt.setEnabled(false);
    opt.addChangeListener(() -> {
      OptionValue v = opt.getPossibleOptionValues().get(0);
      if (v != SHEET_INIT && v != NOT_APPLICABLE) {
        for (int i = 0; i < opt.getPossibleOptionValues().size(); ++i) {
          if (opt.getPossibleOptionValues().get(i) == opt.getValue()) {
            selectedSheet.setValue(i);
          }
        }
      }
    });
    return opt;
  }

  private IntegerOption addSelectedDelimOption() {
    IntegerOption opt = addIntegerOption(getOptionName(SELECTED_DELIM), "", Integer.MIN_VALUE);
    opt.setHidden();
    return opt;
  }

  private IntegerOption addSelectedSheetOption() {
    IntegerOption opt = addIntegerOption(getOptionName(SELECTED_SHEET), "", Integer.MIN_VALUE);
    opt.setHidden();
    return opt;
  }

  private MouseListener getMouseListener() {
    return new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        JFileChooser fc = newFileChooser();
        if (fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
          if (fc.getSelectedFile() != null) {
            sourceFileSelected = true;
            RuntimeSetting setting = RuntimeSetting.forPackage(identifier, "lastSelectedFileSystemFolder");
            runtimeSettings().write(setting, (fc.getCurrentDirectory().getAbsolutePath()));
            TextStyle.NORMAL.applyTo(sourceFileDisplay, fc.getSelectedFile().getName());
            sourceFileDisplay.setToolTipText(fc.getSelectedFile().getAbsolutePath());
            sourceFileChanged(fc.getSelectedFile().getAbsolutePath());
          }
        }
      }
    };
  }

  private JFileChooser newFileChooser() {
    RuntimeSetting setting = RuntimeSetting.forPackage(identifier, "lastSelectedFileSystemFolder");
    String initDir = runtimeSettings().getOrDefault(setting, System.getProperty("user.home"));
    JFileChooser fc = new JFileChooser(initDir);
    String fileType = getFileType();
    fc.setDialogTitle("Select " + fileType);
    fc.setMultiSelectionEnabled(false);
    fc.setFileFilter(new FileFilter() {

      @Override
      public String getDescription() {
        return fileType + "s";
      }

      @Override
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        return supportedFileNameExtensions().contains(getExtension(f.getName()));
      }
    });
    GuiUtils.scale(fc, .6, .5, 800, 560);
    return fc;
  }

  private void sourceFileChanged(String newPath) {
    fileChangedErrorMessage = null;
    prepareComboBoxes();
    if (isCsvFile(newPath)) {
      loadDelimitersIntoComboBox();
    } else if (supportSpreadsheet() && isSpreadsheet(newPath)) {
      loadSheetNamesIntoCombobox();
    } else {
      initializeComboBoxes();
      String msg = "Unsupported file type";
      showMessageDialog(msg, msg, GuiUtilities.getMainFrame(), DialogIcon.ERROR);
    }
  }

  private void initializeComboBoxes() {
    delimiter.setEnabled(false);
    delimiter.setPossibleValues(asList(DELIM_INIT));
    delimiter.setDefaultValue(DELIM_INIT);
    if (supportSpreadsheet()) {
      sheet.setEnabled(false);
      sheet.setPossibleValues(asList(SHEET_INIT));
      sheet.setDefaultValue(SHEET_INIT);
    }
  }

  private void prepareComboBoxes() {
    delimiter.setEnabled(false);
    delimiter.setPossibleValues(asList(NOT_APPLICABLE));
    delimiter.setDefaultValue(NOT_APPLICABLE);
    if (supportSpreadsheet()) {
      sheet.setEnabled(false);
      sheet.setPossibleValues(asList(NOT_APPLICABLE));
      sheet.setDefaultValue(NOT_APPLICABLE);
    }
  }

  private void loadDelimitersIntoComboBox() {
    /*
     * We must first store the value of selectedDelim and only then set the possible values for the combox! setPossibleValues() triggers the
     * change listener on the combobox, which updates the value of selectedDelim.
     */
    int x = selectedDelim.getValue().intValue();
    delimiter.setPossibleValues(DELIM_OPTIONS);
    if (x >= 0 && x < DELIM_OPTIONS.size()) {
      delimiter.setDefaultValue(DELIM_OPTIONS.get(x));
      delimiter.setValue(DELIM_OPTIONS.get(x));
    }
    delimiter.setEnabled(true);
  }

  private void loadSheetNamesIntoCombobox() {
    String[] sheetNames;
    try {
      sheetNames = SpreadSheetReader.getSheetNames(new File(sourceFileDisplay.getToolTipText()));
    } catch (Exception e) {
      String title = "Error reading spreadsheet";
      StringBuilder sb = new StringBuilder(100);
      sb.append(title).append(". ").append(e.getMessage());
      if (!StringMethods.endsWith(e.getMessage(), false, ".", "!", "?")) {
        sb.append(".");
      }
      sb.append(" Please cancel the current operation and try again after you have fixed the problem.");
      fileChangedErrorMessage = sb.toString();
      showMessageDialog(fileChangedErrorMessage, title, GuiUtilities.getMainFrame(), DialogIcon.ERROR);
      return;
    }
    List<OptionValue> options = new ArrayList<>(sheetNames.length);
    for (int i = 0; i < sheetNames.length; ++i) {
      String label = sheetNames[i];
      OptionValue opt = new OptionValue(String.valueOf(i), "  " + label + "  ");
      options.add(opt);
    }
    int x = selectedSheet.getValue().intValue();
    sheet.setPossibleValues(options);
    if (x >= 0 && x < options.size()) {
      sheet.setDefaultValue(options.get(x));
      sheet.setValue(options.get(x));
    }
    sheet.setEnabled(true);
  }

  private String getOptionName(String format) {
    return String.format(format, identifier);
  }

  private String getFileType() {
    switch (identifier) {
      case "smpl":
        return "sample sheet";
      case "crs":
        return "CRS file";
      default:
        return "BOLD file";
    }
  }

  private ArrayList<String> supportedFileNameExtensions() {
    ArrayList<String> types = new ArrayList<>(5);
    types.addAll(CsvImportUtil.csvFileExtension);
    if (supportSpreadsheet()) {
      types.addAll(CsvImportUtil.spreadSheetFileExtension);
    }
    return types;
  }

  private String supportedExtensionsAsString() {
    return supportedFileNameExtensions().stream().map(s -> "*." + s).collect(Collectors.joining("  "));
  }

}
