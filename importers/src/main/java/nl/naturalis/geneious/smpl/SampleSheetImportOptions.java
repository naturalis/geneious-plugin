package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getWritableDatabaseServiceRoots;
import static nl.naturalis.geneious.util.PluginUtils.getPath;
import static nl.naturalis.geneious.util.PluginUtils.isPingFolder;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.plugin.WritableDatabaseServiceTree;
import nl.naturalis.geneious.csv.CsvImportOptions;

/**
 * Underpins the user input dialog for the {@link SampleSheetDocumentOperation Sample Sheet Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class SampleSheetImportOptions extends CsvImportOptions<SampleSheetColumn, SampleSheetImportConfig> {

  private static final String CREATE_DUMMIES_LABEL = "Create dummy documents for rows containing new extract IDs";
  private static final String FOLDER_LABEL0 = "Please select a folder for the dummy documents";
  private static final String FOLDER_LABEL1 = "Target folder: ";
  private static final String FOLDER_LABEL2 = "No documents selected and dummy document creation disabled!  ";
  private static final String FOLDER_LABEL3 = "Database for this operation: ";

  private final BooleanOption createDummies;
  private final JLabel folderDisplay;
  private final WritableDatabaseServiceTree folderTree;

  public SampleSheetImportOptions() {
    super("smpl");
    createDummies = addDummiesOption();
    folderDisplay = new JLabel(FOLDER_LABEL0);
    folderTree = new WritableDatabaseServiceTree(getWritableDatabaseServiceRoots(), false, null);
    if (getTargetFolder() != null) {
      folderTree.setSelectedService(getTargetFolder());
      disableFolderSelection();
    }
    folderTree.setBorder(BorderFactory.createLoweredBevelBorder());
    folderTree.addTreeSelectionListener(e -> {
      WritableDatabaseService folder = folderTree.getSelectedService();
      setTargetFolder(folder);
      setTargetDatabase(folder.getPrimaryDatabaseRoot());
      setFolderDisplayText(isPingFolder(folder), FOLDER_LABEL1 + getPath(folder));
    });
    dummiesOptionChanged();
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
    }
    if (createDummies.getValue()) {
      if (isPingFolder(getTargetFolder())) {
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
    opt.addChangeListener(() -> dummiesOptionChanged());
    return opt;
  }

  private void dummiesOptionChanged() {
    if (createDummies.getValue()) {
      if (getTargetFolder() == null) {
        if (folderTree.getSelectedService() != null) {
          WritableDatabaseService folder = folderTree.getSelectedService();
          setFolderDisplayText(isPingFolder(folder), FOLDER_LABEL1 + getPath(folder));
        } else {
          setFolderDisplayText(false, FOLDER_LABEL0);
        }
      } else {
        setFolderDisplayText(isPingFolder(getTargetFolder()), FOLDER_LABEL1 + getPath(getTargetFolder()));
      }
    } else if (getSelectedDocuments().isEmpty()) {
      setFolderDisplayText(true, FOLDER_LABEL2);
    } else {
      setFolderDisplayText(false, FOLDER_LABEL3 + getPath(getTargetDatabase()));
    }
  }

  private void setFolderDisplayText(boolean isWarning, String text) {
    HashMap<TextAttribute, Object> attribs = new HashMap<TextAttribute, Object>();
    if (isWarning) {
      attribs.put(TextAttribute.FOREGROUND, Color.RED);
      attribs.put(TextAttribute.FONT, Font.ITALIC);
    } else {
      attribs.put(TextAttribute.FOREGROUND, Color.BLACK);
      attribs.put(TextAttribute.FONT, Font.PLAIN);
    }
    folderDisplay.setFont(folderDisplay.getFont().deriveFont(attribs));
    folderDisplay.setText(text);
  }

  /*
   * For some reason the setEnabled and setEditable methods don't have any effect, maybe because the Geneious subclass of JTree does some
   * "clever" things. Therefore we go in hard and simply remove all relevant listeners from the component.
   */
  private void disableFolderSelection() {
    folderTree.setFocusable(false);
    MouseListener[] mouseListeners = folderTree.getMouseListeners();
    for (MouseListener listener : mouseListeners) {
      folderTree.removeMouseListener(listener);
    }
    // This isn't really necessary, because setFocusable(false) does work, so the user can never tab to the component and then use the arrow
    // keys to navigate the tree. But we do it anyhow.
    KeyListener[] keyListeners = folderTree.getKeyListeners();
    for (KeyListener listener : keyListeners) {
      folderTree.removeKeyListener(listener);
    }
    // Won't work:
    // folderTree.setEnabled(false);
    // folderTree.setEditable(false);
  }

}
