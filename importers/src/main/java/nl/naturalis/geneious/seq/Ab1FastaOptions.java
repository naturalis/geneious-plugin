package nl.naturalis.geneious.seq;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getWritableDatabaseServiceRoots;
import static nl.naturalis.geneious.util.PluginUtils.isPingFolder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.apache.commons.lang3.ArrayUtils;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;
import com.biomatters.geneious.publicapi.plugin.WritableDatabaseServiceTree;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.common.StringMethods;
import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.gui.Ab1FastaFileFilter;
import nl.naturalis.geneious.gui.GuiUtils;
import nl.naturalis.geneious.gui.TextStyle;

/**
 * Underpins the user input dialog for the {@link Ab1FastaDocumentOperation AB1/Fasta Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class Ab1FastaOptions extends OperationOptions<Ab1FastaImportConfig> {

  private final StringOption ab1FastaDir;
  private final StringOption targetFolderId;

  private final JTextField filesDisplay;
  private final JLabel fileCountDisplay;
  private final JLabel folderDisplay;
  private final WritableDatabaseServiceTree folderTree;

  private File[] selectedFiles;

  Ab1FastaOptions() {

    ab1FastaDir = addStringOption("nl.naturalis.geneious.seq.dir", "", "");
    ab1FastaDir.setHidden();

    targetFolderId = addStringOption("nl.naturalis.geneious.seq.target", "", "");
    targetFolderId.setHidden();

    fileCountDisplay = new JLabel("0 files selected");
    fileCountDisplay.setForeground(Color.BLACK);
    fileCountDisplay.addMouseListener(getMouseListener());

    filesDisplay = new JTextField();
    filesDisplay.setEditable(false);
    filesDisplay.setPreferredSize(new Dimension(450, filesDisplay.getPreferredSize().height));
    filesDisplay.addMouseListener(getMouseListener());


    if (getTargetFolder() == null) {
      folderDisplay = new JLabel("Please select a target folder");
    } else {
      folderDisplay = new JLabel("Target folder: " + getTargetFolder().getFolderName());
    }

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
        targetFolderId.setValue(folder.getUniqueID());
        setTargetDatabase(folder.getPrimaryDatabaseRoot());
        TextStyle style = isPingFolder(folder) ? TextStyle.WARNING : TextStyle.NORMAL;
        style.applyTo(folderDisplay, "Target folder: " + folder.getFolderName());
      });
    }

    addCustomComponent(fileCountDisplay);
    addCustomComponent(filesDisplay);
    addCustomComponent(folderDisplay);
    addCustomComponent(folderTree);

  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if (msg != null) {
      return msg;
    }
    if (ArrayUtils.isEmpty(selectedFiles)) {
      return "Please select at least one AB1 or Fasta file";
    }
    return null;
  }

  @Override
  public Ab1FastaImportConfig configureOperation() {
    Ab1FastaImportConfig config = super.configureDefaults(new Ab1FastaImportConfig());
    config.setFiles(selectedFiles);
    return config;
  }

  private JFileChooser newFileChooser() {
    JFileChooser fc = new JFileChooser(ab1FastaDir.getValue());
    fc.setDialogTitle("Select AB1/Fasta files");
    fc.setMultiSelectionEnabled(true);
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(true, true));
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(true, false));
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(false, true));
    fc.setAcceptAllFileFilterUsed(false);
    GuiUtils.scale(fc, .6, .5, 800, 560);
    return fc;
  }

  private MouseListener getMouseListener() {
    return new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        JFileChooser fc = newFileChooser();
        if (fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
          ab1FastaDir.setValue(fc.getCurrentDirectory().getAbsolutePath());
          selectedFiles = fc.getSelectedFiles();
          StringBuilder sb = new StringBuilder(64);
          for (int i = 0; i < Math.min(10, selectedFiles.length); ++i) {
            if (i > 0) {
              sb.append(", ");
            }
            sb.append(selectedFiles[i].getName());
          }
          if (selectedFiles.length > 10) {
            sb.append(" ... ").append(selectedFiles.length - 10).append(" more file(s)");
          }
          fileCountDisplay.setText(String.format("%d file(s) selected", selectedFiles.length));
          filesDisplay.setText(sb.toString());
          filesDisplay.setToolTipText(fileCountDisplay.getText());
          filesDisplay.setCaretPosition(0);
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        if (e.getComponent() == fileCountDisplay) {
          TextStyle.HYPERLINK.applyTo(fileCountDisplay);
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (e.getComponent() == fileCountDisplay) {
          TextStyle.NORMAL.applyTo(fileCountDisplay);
        }
      }
    };
  }

}
