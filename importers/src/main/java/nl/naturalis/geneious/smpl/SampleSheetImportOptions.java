package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getWritableDatabaseServiceRoots;
import static nl.naturalis.geneious.util.PluginUtils.getPath;
import static nl.naturalis.geneious.util.PluginUtils.isPingFolder;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;
import com.biomatters.geneious.publicapi.plugin.WritableDatabaseServiceTree;
import nl.naturalis.common.StringMethods;
import nl.naturalis.geneious.csv.CsvImportOptions;
import nl.naturalis.geneious.gui.GuiUtils;
import nl.naturalis.geneious.gui.TextStyle;

/**
 * Underpins the user input dialog for the {@link SampleSheetDocumentOperation Sample Sheet Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class SampleSheetImportOptions extends CsvImportOptions<SampleSheetColumn, SampleSheetImportConfig> {

  private static final String CREATE_DUMMIES_LABEL = "Create dummy documents for new extract IDs";

  private static final String FOLDER_DISPLAY_TEXT0 = "Please select a folder for the dummy documents";
  private static final String FOLDER_DISPLAY_TEXT1 = "Target folder: ";
  private static final String FOLDER_DISPLAY_TEXT2 = "No documents selected and dummy document creation disabled ";
  private static final String FOLDER_DISPLAY_TEXT3 = "Database for this operation: ";

  private final StringOption targetFolderId;
  private final BooleanOption createDummies;
  private final JLabel folderDisplay;
  private final WritableDatabaseServiceTree folderTree;

  public SampleSheetImportOptions() {

    super("smpl");

    targetFolderId = addStringOption("nl.naturalis.geneious.smpl.target", "", "");
    targetFolderId.setHidden();

    createDummies = addDummiesOption();

    folderDisplay = new JLabel(FOLDER_DISPLAY_TEXT0);

    folderTree = new WritableDatabaseServiceTree(getWritableDatabaseServiceRoots(), false, null);
    folderTree.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
    folderTree.setPreferredSize(new Dimension(450, 200));
    if (getTargetFolder() != null) {
      folderTree.setSelectedService(getTargetFolder());
      GuiUtils.paralyse(folderTree);
    } else {
      if (!StringMethods.isEmpty(targetFolderId.getValue())) {
        try {
          WritableDatabaseService last = (WritableDatabaseService) ServiceUtilities.getService(targetFolderId.getValue());
          folderTree.setSelectedService(last);
        } catch (Exception e) {
          // Folder may have been deleted or something
        }
      }
      folderTree.addTreeSelectionListener(e -> {
        WritableDatabaseService folder = folderTree.getSelectedService();
        setTargetFolder(folder);
        setTargetDatabase(folder.getPrimaryDatabaseRoot());
        TextStyle style = isPingFolder(folder) ? TextStyle.WARNING : TextStyle.NORMAL;
        style.applyTo(folderDisplay, FOLDER_DISPLAY_TEXT1 + folder.getFolderName());
      });
    }

    createDummiesOptionChanged();

    addCustomComponent(folderDisplay);
    addCustomComponent(folderTree);
  }

  @Override
  public SampleSheetImportConfig configureOperation() {
    SampleSheetImportConfig cfg = configureDefaults(new SampleSheetImportConfig());
    cfg.setCreateDummies(createDummies.getValue());
    return cfg;
  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if (msg != null) {
      return msg;
    } else if (createDummies.getValue()) {
      if (getTargetFolder() == null) {
        return FOLDER_DISPLAY_TEXT0;
      } else if (isPingFolder(getTargetFolder())) {
        return "Illegal target folder: " + getPath(getTargetFolder());
      }
    } else if (getSelectedDocuments().isEmpty()) {
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

  @Override
  protected boolean supportSpreadsheet() {
    return true;
  }

  private BooleanOption addDummiesOption() {
    String name = "nl.naturalis.geneious.smpl.dummies";
    BooleanOption opt = addBooleanOption(name, CREATE_DUMMIES_LABEL, Boolean.TRUE);
    opt.setHelp("You can choose to create placeholder documents (a.k.a. dummies) for sample sheet rows that refer "
        + "to yet-to-be imported AB1 or fasta sequences. The placeholder document then acquires the annotations "
        + "present in the sample sheet row. Once you import the real sequence, the annotations will be copied "
        + "from the placeholder document to the sequence document, and the placeholder document will be deleted.");
    opt.addChangeListener(() -> createDummiesOptionChanged());
    return opt;
  }

  private void createDummiesOptionChanged() {
    if (createDummies.getValue()) {
      if (getTargetFolder() == null) {
        if (folderTree.getSelectedService() != null) {
          WritableDatabaseService folder = folderTree.getSelectedService();
          TextStyle style = isPingFolder(folder) ? TextStyle.WARNING : TextStyle.NORMAL;
          style.applyTo(folderDisplay, FOLDER_DISPLAY_TEXT1 + folder.getFolderName());
        } else {
          TextStyle.NORMAL.applyTo(folderDisplay, FOLDER_DISPLAY_TEXT0);
        }
      } else {
        TextStyle style = isPingFolder(getTargetFolder()) ? TextStyle.WARNING : TextStyle.NORMAL;
        style.applyTo(folderDisplay, FOLDER_DISPLAY_TEXT1 + getTargetFolder().getFolderName());
      }
    } else if (getSelectedDocuments().isEmpty()) {
      TextStyle.WARNING.applyTo(folderDisplay, FOLDER_DISPLAY_TEXT2);
    } else {
      TextStyle.NORMAL.applyTo(folderDisplay, FOLDER_DISPLAY_TEXT3 + getTargetDatabase().getFolderName());
    }
  }

}
