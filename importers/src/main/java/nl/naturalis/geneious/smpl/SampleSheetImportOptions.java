package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getWritableDatabaseServiceRoots;
import static nl.naturalis.geneious.util.PluginUtils.getPath;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import com.biomatters.geneious.publicapi.plugin.WritableDatabaseServiceTree;
import nl.naturalis.geneious.csv.CsvImportOptions;
import nl.naturalis.geneious.util.PluginUtils;

/**
 * Underpins the user input dialog for the {@link SampleSheetDocumentOperation Sample Sheet Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class SampleSheetImportOptions extends CsvImportOptions<SampleSheetColumn, SampleSheetImportConfig> {

  private static final String CREATE_DUMMIES_LABEL = "Create dummy sequences for rows containing new extract IDs";
  private static final String FOLDER_LABEL0 = "Please select a folder for dummy documents";
  private static final String FOLDER_LABEL1 = "Folder for dummy documents: ";
  private static final String FOLDER_LABEL2 = "Error: no documents selected and dummy document creation disabled ";
  private static final String FOLDER_LABEL3 = "Database for this operation: ";

  private final BooleanOption createDummies;
  private final JLabel folderLabel;
  private final WritableDatabaseServiceTree folderTree;

  public SampleSheetImportOptions() {
    super("smpl");
    createDummies = addDummiesOption();
    folderLabel = new JLabel(FOLDER_LABEL0);
    folderTree = new WritableDatabaseServiceTree(getWritableDatabaseServiceRoots(), false, null);
    if (getTargetFolder() != null) {
      folderTree.setSelectedService(getTargetFolder());
      disableFolderSelection();
    }
    folderTree.setBorder(BorderFactory.createLoweredBevelBorder());
    folderTree.addTreeSelectionListener(e -> {
      setTargetFolder(folderTree.getSelectedService());
      setTargetDatabase(folderTree.getSelectedService().getPrimaryDatabaseRoot());
      folderLabel.setText(FOLDER_LABEL1 + PluginUtils.getPath(getTargetFolder()));
    });
    dummiesOptionChanged();
    addCustomComponent(folderLabel);
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
    if (getSelectedDocuments().isEmpty() && !createDummies.getValue()) {
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
    folderLabel.setForeground(Color.BLACK);
    folderLabel.setFont(folderLabel.getFont().deriveFont(Font.PLAIN));
    if (createDummies.getValue()) {
      if (getTargetFolder() == null) {
        if (folderTree.getSelectedService() != null) {
          folderLabel.setText(FOLDER_LABEL1 + getPath(folderTree.getSelectedService()));
        } else {
          folderLabel.setText(FOLDER_LABEL0);
        }
      } else {
        folderLabel.setText(FOLDER_LABEL1 + getPath(getTargetFolder()));
      }
    } else if (getSelectedDocuments().isEmpty()) {
      folderLabel.setForeground(Color.RED);
      folderLabel.setFont(folderLabel.getFont().deriveFont(Font.ITALIC));
      folderLabel.setText(FOLDER_LABEL2);
    } else {
      folderLabel.setText(FOLDER_LABEL3 + getPath(getTargetDatabase()));
    }
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
