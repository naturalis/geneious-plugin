package nl.naturalis.geneious.seq;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getWritableDatabaseServiceRoots;
import static nl.naturalis.geneious.util.PluginUtils.getPath;
import static nl.naturalis.geneious.util.PluginUtils.isPingFolder;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.apache.commons.lang3.ArrayUtils;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.plugin.WritableDatabaseServiceTree;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.gui.Ab1FastaFileFilter;
import nl.naturalis.geneious.gui.GeneiousGUI;

/**
 * Underpins the user input dialog for the {@link Ab1FastaDocumentOperation AB1/Fasta Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class Ab1FastaOptions extends OperationOptions<Ab1FastaImportConfig> {

  private static final String FOLDER_LABEL0 = "Please select a target folder";
  private static final String FOLDER_LABEL1 = "Target folder: ";

  private final StringOption ab1FastaDir;

  private final JTextField filesDisplay;
  private final JLabel fileCountLabel;

  private final JLabel folderDisplay;
  private final WritableDatabaseServiceTree folderTree;

  private File[] selectedFiles;

  Ab1FastaOptions() {

    ab1FastaDir = addStringOption("nl.naturalis.geneious.seq.dir", "", "");
    ab1FastaDir.setHidden();

    fileCountLabel = new JLabel("0 files selected");
    fileCountLabel.setForeground(Color.BLACK);
    fileCountLabel.addMouseListener(getMouseListener());
    addCustomComponent(fileCountLabel);

    filesDisplay = new JTextField(40);
    filesDisplay.setEditable(false);
    filesDisplay.addMouseListener(getMouseListener());
    addCustomComponent(filesDisplay);

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
    GeneiousGUI.scale(fc, .6, .5, 800, 560);
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
          filesDisplay.setText(sb.toString());
          if (selectedFiles.length == 1) {
            fileCountLabel.setText("1 file selected");
          } else {
            fileCountLabel.setText(String.format("%d files selected", selectedFiles.length));
          }
          filesDisplay.setToolTipText(fileCountLabel.getText());
          filesDisplay.setCaretPosition(0);
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        if (e.getComponent() == fileCountLabel) {
          HashMap<TextAttribute, Object> attribs = new HashMap<TextAttribute, Object>();
          attribs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
          attribs.put(TextAttribute.FOREGROUND, Color.BLUE);
          fileCountLabel.setFont(fileCountLabel.getFont().deriveFont(attribs));
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (e.getComponent() == fileCountLabel) {
          HashMap<TextAttribute, Object> attribs = new HashMap<TextAttribute, Object>();
          attribs.put(TextAttribute.UNDERLINE, null);
          attribs.put(TextAttribute.FOREGROUND, Color.BLACK);
          fileCountLabel.setFont(fileCountLabel.getFont().deriveFont(attribs));
        }
      }
    };
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
