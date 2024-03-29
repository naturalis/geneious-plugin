package nl.naturalis.geneious.seq;

import static nl.naturalis.geneious.gui.ScrollableTreeViewer.isValidTargetFolder;
import static nl.naturalis.geneious.util.History.history;
import static nl.naturalis.geneious.util.HistorySetting.SEQ_LAST_SELECTED_FILE_SYSTEM_FOLDER;
import static nl.naturalis.geneious.util.HistorySetting.SEQ_LAST_SELECTED_GENEIOUS_FOLDER;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.apache.commons.lang3.ArrayUtils;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.gui.Ab1FastaFileFilter;
import nl.naturalis.geneious.gui.GuiUtils;
import nl.naturalis.geneious.gui.ScrollableTreeViewer;
import nl.naturalis.geneious.gui.TextStyle;

/**
 * Underpins the user input dialog for the {@link Ab1FastaDocumentOperation AB1/Fasta Import} operation.
 * 
 * @author Ayco Holleman
 *
 */
class Ab1FastaOptions extends OperationOptions<Ab1FastaImportConfig> {

  private final JTextField sourceFilesDisplay;
  private final JLabel fileCountDisplay;
  private final JLabel geneiousFolderDisplay;
  private final ScrollableTreeViewer treeViewer;

  private File[] selectedFiles;

  Ab1FastaOptions() {

    fileCountDisplay = new JLabel("0 files selected");
    fileCountDisplay.setForeground(Color.BLACK);
    fileCountDisplay.addMouseListener(getMouseListener());

    sourceFilesDisplay = new JTextField();
    sourceFilesDisplay.setEditable(false);
    Dimension d = new Dimension(ScrollableTreeViewer.PREFERRED_WIDTH, sourceFilesDisplay.getPreferredSize().height);
    sourceFilesDisplay.setPreferredSize(d);
    sourceFilesDisplay.addMouseListener(getMouseListener());
    TextStyle.ENTER_VALUE.applyTo(sourceFilesDisplay, "Click to select AB1/Fasta files");

    geneiousFolderDisplay = new JLabel("XYZ"); // Just some text to enforce a height
    d = new Dimension(ScrollableTreeViewer.PREFERRED_WIDTH, geneiousFolderDisplay.getPreferredSize().height);
    geneiousFolderDisplay.setPreferredSize(d);

    treeViewer = new ScrollableTreeViewer(this, geneiousFolderDisplay, SEQ_LAST_SELECTED_GENEIOUS_FOLDER);

    addCustomComponent(fileCountDisplay);
    addCustomComponent(sourceFilesDisplay);
    addCustomComponent(geneiousFolderDisplay);
    addCustomComponent(treeViewer.getScrollPane());

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
    if (!isValidTargetFolder(getTargetFolder())) {
      return ScrollableTreeViewer.FOLDER_DISPLAY_TEXT0;
    }
    return null;
  }

  @Override
  public Ab1FastaImportConfig configureOperation() {
    Ab1FastaImportConfig config = super.configureDefaults(new Ab1FastaImportConfig());
    config.setFiles(selectedFiles);
    return config;
  }

  private static JFileChooser newFileChooser() {
    String initDir = history().read(SEQ_LAST_SELECTED_FILE_SYSTEM_FOLDER, System.getProperty("user.home"));
    JFileChooser fc = new JFileChooser(initDir);
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
          history().save(SEQ_LAST_SELECTED_FILE_SYSTEM_FOLDER, (fc.getCurrentDirectory().getAbsolutePath()));
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
          if (selectedFiles.length == 1) {
            fileCountDisplay.setText("1 files selected");
          } else {
            fileCountDisplay.setText(String.format("%d files selected", selectedFiles.length));
          }
          sourceFilesDisplay.setText(sb.toString());
          sourceFilesDisplay.setToolTipText(fileCountDisplay.getText());
          sourceFilesDisplay.setCaretPosition(0);
          TextStyle.NORMAL.applyTo(sourceFilesDisplay);
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
