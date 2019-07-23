package nl.naturalis.geneious.seq;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.ArrayUtils;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.gui.Ab1FastaFileFilter;
import nl.naturalis.geneious.gui.GeneiousGUI;

class Ab1FastaOptions extends OperationOptions<Ab1FastaImportConfig> {

  private static final String MSG_CHOOSE_FILES = "Choose AB1/fasta files to import";

  private final JTextField filesDisplay;
  private final JLabel filesLabel = new JLabel(MSG_CHOOSE_FILES);
  private final JLabel fileCountLabel = new JLabel(" ");
  private final StringOption ab1FastaDir;

  private File[] selectedFiles;

  Ab1FastaOptions() {
    ab1FastaDir = addStringOption("nl.naturalis.geneious.seq.dir", "", "");
    ab1FastaDir.setHidden();
    addCustomComponent(filesLabel);
    beginAlignHorizontally(null, false);
    filesDisplay = new JTextField(40);
    filesDisplay.setEditable(false);
    addCustomComponent(filesDisplay);
    createButtonOption();
    endAlignHorizontally();
    addCustomComponent(fileCountLabel);
  }

  @Override
  public String verifyOptionsAreValid() {
    if(ArrayUtils.isEmpty(selectedFiles)) {
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

  private ButtonOption createButtonOption() {
    String name = "nl.naturalis.geneious.seq.button";
    ButtonOption button = addButtonOption(name, "", "Select");
    button.addActionListener(e -> {
      JFileChooser fc = newFileChooser();
      if(fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
        ab1FastaDir.setValue(fc.getCurrentDirectory().getAbsolutePath());
        selectedFiles = fc.getSelectedFiles();
        StringBuilder sb = new StringBuilder(64);
        for(int i = 0; i < Math.min(10, selectedFiles.length); ++i) {
          if(i > 0) {
            sb.append(", ");
          }
          sb.append(selectedFiles[i].getName());
        }
        if(selectedFiles.length > 10) {
          sb.append(" ... ").append(selectedFiles.length - 10).append(" more file(s)");
        }
        filesDisplay.setText(sb.toString());
        if(selectedFiles.length == 1) {
          fileCountLabel.setText("1 file selected");
        } else {
          fileCountLabel.setText(String.format("%d files selected", selectedFiles.length));
        }
        filesDisplay.setToolTipText(fileCountLabel.getText());
        filesDisplay.setCaretPosition(0);
      }
    });
    return button;
  }

  private JFileChooser newFileChooser() {
    JFileChooser fc = new JFileChooser(ab1FastaDir.getValue());
    fc.setDialogTitle(MSG_CHOOSE_FILES);
    fc.setMultiSelectionEnabled(true);
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(true, true));
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(true, false));
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(false, true));
    fc.setAcceptAllFileFilterUsed(false);
    GeneiousGUI.scale(fc, .6, .5, 800, 560);
    return fc;
  }
}
