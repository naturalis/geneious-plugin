package nl.naturalis.geneious.samplesheet;

import static nl.naturalis.geneious.gui.GridBagFormUtil.addFileSelector;
import static nl.naturalis.geneious.gui.GridBagFormUtil.addLabel;
import static nl.naturalis.geneious.gui.GridBagFormUtil.addTextFieldWithComment;
import static nl.naturalis.geneious.gui.GridBagFormUtil.createFormPanel;
import static nl.naturalis.geneious.gui.GridBagFormUtil.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

class SampleSheetSelector {

  private final AnnotatedPluginDocument[] selectedDocuments;

  private JDialog dialog;
  private JTextField fileTextField;
  private JCheckBox dummiesCheckBox;
  private JTextField sheetNoTextField;
  private JTextField skipLinesTextField;

  SampleSheetSelector(AnnotatedPluginDocument[] docs) {
    this.selectedDocuments = docs;
  }

  void show() {

    dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle("Select sample sheet");
    dialog.setLayout(new GridBagLayout());

    JPanel panel = createFormPanel(dialog);

    // FIRST ROW
    addLabel(panel, 0, "Sample sheet");
    fileTextField = new JTextField(50);
    addFileSelector(panel, 0, fileTextField, createBrowseButton());

    // SECOND ROW
    addLabel(panel, 1, "Dummies");
    dummiesCheckBox = new JCheckBox();
    dummiesCheckBox.setSelected(true);
    addCheckboxWithComment(panel, 1, dummiesCheckBox,
        "Create dummy documents for non-existing extract IDs");

    // THIRD ROW
    addLabel(panel, 2, "Skip lines");
    skipLinesTextField = new JTextField(4);
    skipLinesTextField.setText("1");
    addTextFieldWithComment(panel, 2, skipLinesTextField,
        "(Applicable for spreadsheets, CSV, TSV, etc.)");

    // FOURTH ROW
    addLabel(panel, 3, "Sheet number");
    sheetNoTextField = new JTextField(4);
    sheetNoTextField.setText("1");
    sheetNoTextField.setEnabled(false);
    addTextFieldWithComment(panel, 3, sheetNoTextField, "(Only applicable when importing from spreadsheet)");

    createOKCancelPanel(dialog, createOkButton());

    dialog.pack();
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.setVisible(true);
  }

  private JButton createBrowseButton() {
    JButton browseButton = new JButton("Browse");
    browseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(RuntimeSettings.INSTANCE.getCrsFolder());
        if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
          RuntimeSettings.INSTANCE.setCrsFolder(fc.getCurrentDirectory());
          File f = fc.getSelectedFile();
          if (f != null) {
            fileTextField.setText(f.getAbsolutePath());
            if (f.getName().endsWith(".xls")) {
              sheetNoTextField.setEnabled(true);
            } else {
              sheetNoTextField.setEnabled(false);
            }
          }
        }
      }
    });
    return browseButton;
  }

  private JButton createOkButton() {
    JButton okButton = new JButton("OK");
    okButton.setPreferredSize(new Dimension(100, okButton.getPreferredSize().height));
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        validateAndLaunch();
        dialog.dispose();
      }
    });
    return okButton;
  }

  private void validateAndLaunch() {
    if (StringUtils.isBlank(fileTextField.getText())) {
      showError("No sample sheet selected", "Please select a sample sheet");
      return;
    }
    File file = new File(fileTextField.getText());
    if (!file.isFile()) {
      String msg = String.format("No such file: \"%s\"", fileTextField.getText());
      showError("No such file", msg);
      return;
    }
    if (selectedDocuments.length == 0 && !dummiesCheckBox.isSelected()) {
      showError("No documents selected", "Please select at least one document or check to \"Create dummies\"");
      return;
    }
    SampleSheetImportConfig input = new SampleSheetImportConfig(selectedDocuments);
    input.setFile(file);
    input.setCreateDummies(dummiesCheckBox.isSelected());
    try {
      int i = Integer.parseInt(skipLinesTextField.getText());
      input.setSkipLines(i);
      RuntimeSettings.INSTANCE.setCrsSkipLines(i);
    } catch (NumberFormatException exc) {
      String msg = String.format("Invalid number: \"%s\"", skipLinesTextField.getText());
      showError("Invalid number", msg);
      return;
    }
    try {
      int i = Integer.parseInt(sheetNoTextField.getText().trim());
      input.setSheetNumber(i);
      RuntimeSettings.INSTANCE.setCrsSheetNum(i);
    } catch (NumberFormatException exc) {
      String msg = String.format("Invalid number: \"%s\"", sheetNoTextField.getText());
      showError("Invalid number", msg);
      return;
    }
    new SampleSheetImporter(input).process();
  }

  private void showError(String title, String message) {
    JOptionPane.showMessageDialog(dialog, message, title, JOptionPane.ERROR_MESSAGE);
  }

}
