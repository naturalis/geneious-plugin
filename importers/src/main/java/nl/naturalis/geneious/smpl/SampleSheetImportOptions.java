package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.plugin.ServiceUtilities.getService;
import static nl.naturalis.geneious.gui.ScrollableTreeViewer.isValidTargetFolder;
import java.awt.Dimension;
import javax.swing.JLabel;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import nl.naturalis.geneious.csv.CsvImportOptions;
import nl.naturalis.geneious.gui.ScrollableTreeViewer;
import nl.naturalis.geneious.gui.TextStyle;
import static nl.naturalis.geneious.util.RuntimeSettings.*;
import static nl.naturalis.geneious.util.RuntimeSetting.*;

/**
 * Underpins the user input dialog for the {@link SampleSheetDocumentOperation Sample Sheet Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class SampleSheetImportOptions extends CsvImportOptions<SampleSheetColumn, SampleSheetImportConfig> {

  private static final String CREATE_DUMMIES_LABEL = "Create dummies for new extract IDs";

  private final BooleanOption createDummies;
  private final JLabel geneiousFolderDisplay;
  private final ScrollableTreeViewer treeViewer;

  public SampleSheetImportOptions() {

    super("smpl");

    createDummies = addDummiesOption();

    geneiousFolderDisplay = new JLabel("XYZ"); // Just some text to enforce a height
    geneiousFolderDisplay.setToolTipText("Please select a folder for the dummy documents");
    Dimension d = new Dimension(ScrollableTreeViewer.PREFERRED_WIDTH, geneiousFolderDisplay.getPreferredSize().height);
    geneiousFolderDisplay.setPreferredSize(d);

    treeViewer = new ScrollableTreeViewer(this,
        geneiousFolderDisplay,
        () -> {
          String folderId = runtimeSettings().get(SMPL_LAST_SELECTED_GENEIOUS_FOLDER);
          return folderId == null ? null : (WritableDatabaseService) getService(folderId);
        },
        folder -> runtimeSettings().write(SMPL_LAST_SELECTED_GENEIOUS_FOLDER, folder.getUniqueID()));

    dummiesOptionChanged();

    addCustomComponent(geneiousFolderDisplay);
    addCustomComponent(treeViewer.getScrollPane());
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
      if (!isValidTargetFolder(getTargetFolder())) {
        if (createDummies.getValue()) {
          return ScrollableTreeViewer.FOLDER_DISPLAY_TEXT0;
        }
        return ScrollableTreeViewer.DATABASE_DISPLAY_TEXT0;
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
    treeViewer.selectsDatabaseOnly(!createDummies.getValue());
    if (!createDummies.getValue() && getSelectedDocuments().isEmpty()) {
      TextStyle.WARNING.applyTo(geneiousFolderDisplay, "Error: no documents selected! ");
    } else {
      treeViewer.updateDisplayText();
    }
  }

}
