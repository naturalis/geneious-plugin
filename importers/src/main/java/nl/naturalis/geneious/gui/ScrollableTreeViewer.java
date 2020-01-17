package nl.naturalis.geneious.gui;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getWritableDatabaseServiceRoots;
import static com.biomatters.geneious.publicapi.plugin.ServiceUtilities.getService;
import static nl.naturalis.geneious.util.History.history;
import static nl.naturalis.geneious.util.PluginUtils.isPingFolder;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.plugin.WritableDatabaseServiceTree;
import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.util.HistorySetting;

/**
 * Shows a folder tree within which user can select the target folder for AB1/Fasta files (AB1/Fasta Import operation) or for dummy
 * documents (Sample Sheet Import operation).
 * 
 * @author Ayco Holleman
 *
 */
public class ScrollableTreeViewer extends WritableDatabaseServiceTree {

  public static final String FOLDER_DISPLAY_TEXT0 = "Please select a valid Geneious target folder";
  public static final String FOLDER_DISPLAY_TEXT1 = "Geneious target folder: ";
  public static final String DATABASE_DISPLAY_TEXT0 = "Please select a valid target database";
  public static final String DATABASE_DISPLAY_TEXT1 = "Target database: ";

  public static final int PREFERRED_WIDTH = 400;

  public static boolean isValidTargetFolder(WritableDatabaseService folder) {
    return folder != null && !folder.isReadOnly() && !folder.isDeleted() && !isPingFolder(folder);
  }

  private final OperationOptions<?> options;
  private final JLabel geneiousFolderDisplay;
  private final HistorySetting setting;

  private boolean selectsDatabaseOnly;

  /**
   * Creates a <code>ScrollableTreeViewer</code> that updates target folder of the provided <code>OperationOptions</code> as the user makes
   * new selections in the tree viewer. Also, as the user selects a folder in the tree viewer, the last selected folder's unique ID is
   * stored so it survives the operation and the current Geneious session.
   * 
   * @param options
   * @param geneiousFolderDisplay
   * @param historyReader
   * @param historyWriter
   */
  public ScrollableTreeViewer(OperationOptions<?> options, JLabel geneiousFolderDisplay, HistorySetting toBeSaved) {
    super(getWritableDatabaseServiceRoots(), false, null);
    this.options = options;
    this.geneiousFolderDisplay = geneiousFolderDisplay;
    this.setting = toBeSaved;
    setup();
  }

  /**
   * Whether the tree viewer is used to select a target folder, or just the database to be used for querying and pinging.
   * 
   * @param selectsDatabaseOnly
   */
  public void selectsDatabaseOnly(boolean selectsDatabaseOnly) {
    this.selectsDatabaseOnly = selectsDatabaseOnly;
  }

  public JScrollPane getScrollPane() {
    JScrollPane p = new JScrollPane(this);
    p.setPreferredSize(new Dimension(PREFERRED_WIDTH, 110));
    return p;
  }

  public void updateDisplayText() {
    WritableDatabaseService folder = options.getTargetFolder();
    if (isValidTargetFolder(folder)) {
      displayFolderName(folder);
    } else {
      displayWarning();
    }
  }

  private void setup() {
    setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
    if (isValidTargetFolder(options.getTargetFolder())) {
      displayFolderName(options.getTargetFolder());
      setSelectedService(options.getTargetFolder());
      history().save(setting, options.getTargetFolder().getUniqueID());
    } else {
      WritableDatabaseService folder = getLastSelectedFolder();
      if (isValidTargetFolder(folder)) {
        displayFolderName(folder);
        setSelectedService(folder);
        options.setTargetFolder(folder);
        options.setTargetDatabase(folder.getPrimaryDatabaseRoot());
      } else {
        displayWarning();
      }
    }
    addTreeSelectionListener(e -> {
      folderSelectionChanged();
    });
  }

  private void folderSelectionChanged() {
    WritableDatabaseService folder = getSelectedService();
    if (isValidTargetFolder(folder)) {
      displayFolderName(folder);
      options.setTargetFolder(folder);
      options.setTargetDatabase(folder.getPrimaryDatabaseRoot());
      history().save(setting, folder.getUniqueID());
    } else {
      options.setTargetFolder(null);
      options.setTargetDatabase(null);
      displayWarning();
    }
  }

  private WritableDatabaseService getLastSelectedFolder() {
    String folderId = history().read(setting);
    return folderId == null ? null : (WritableDatabaseService) getService(folderId);
  }

  private void displayFolderName(WritableDatabaseService folder) {
    String msg;
    if (selectsDatabaseOnly) {
      msg = DATABASE_DISPLAY_TEXT1 + folder.getPrimaryDatabaseRoot().getFolderName();
    } else {
      msg = FOLDER_DISPLAY_TEXT1 + folder.getFolderName();
    }
    TextStyle.NORMAL.applyTo(geneiousFolderDisplay, msg);
  }

  private void displayWarning() {
    if (selectsDatabaseOnly) {
      TextStyle.WARNING.applyTo(geneiousFolderDisplay, DATABASE_DISPLAY_TEXT0);
    } else {
      TextStyle.WARNING.applyTo(geneiousFolderDisplay, FOLDER_DISPLAY_TEXT0);
    }
  }

}
