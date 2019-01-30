package nl.naturalis.geneious.crs;

import static nl.naturalis.geneious.gui.GridBagFormUtil.addFileSelector;
import static nl.naturalis.geneious.gui.GridBagFormUtil.addLabel;
import static nl.naturalis.geneious.gui.GridBagFormUtil.addTextFieldWithComment;
import static nl.naturalis.geneious.gui.GridBagFormUtil.createFormPanel;
import static nl.naturalis.geneious.gui.GridBagFormUtil.createOKCancelPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

class CrsFileSelector {

  private final AnnotatedPluginDocument[] selectedDocuments;

  private JDialog dialog;
  private JTextField crsFileTextField;
  private JTextField sheetNoTextField;
  private JTextField skipLinesTextField;

  CrsFileSelector(AnnotatedPluginDocument[] docs) {
    this.selectedDocuments = docs;
  }

  void show() {

    dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle("Select CRS file");
    dialog.setLayout(new GridBagLayout());

    JPanel panel = createFormPanel(dialog);

    // FIRST ROW
    addLabel(panel, 0, "CRS File");
    crsFileTextField = new JTextField(50);
    addFileSelector(panel, 0, crsFileTextField, createBrowseButton());

    // SECOND ROW
    addLabel(panel, 1, "Skip lines");
    skipLinesTextField = new JTextField(4);
    skipLinesTextField.setText("1");
    addTextFieldWithComment(panel, 1, skipLinesTextField,
        "(Applicable for spreadsheets, CSV, TSV, etc.)");

    // THIRD ROW
    addLabel(panel, 2, "Sheet number");
    sheetNoTextField = new JTextField(4);
    sheetNoTextField.setText("1");
    sheetNoTextField.setEnabled(false);
    addTextFieldWithComment(panel, 2, sheetNoTextField,
        "(Only applicable when importing from spreadsheet)");

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
            crsFileTextField.setText(f.getAbsolutePath());
            if (f.getName().endsWith(".xls")) {
              sheetNoTextField.setEnabled(true);
            }
            else {
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
    if (StringUtils.isBlank(crsFileTextField.getText())) {
      JOptionPane.showMessageDialog(dialog, "Please select a CRS file", "No CRS file selected",
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    File crsFile = new File(crsFileTextField.getText());
    if (!crsFile.isFile()) {
      JOptionPane.showMessageDialog(dialog, "Invalid file: " + crsFileTextField.getText(),
          "Invalid file", JOptionPane.ERROR_MESSAGE);
      return;
    }
    CrsProcessInput input = new CrsProcessInput(selectedDocuments);
    input.setFile(crsFile);
    try {
      int i = Integer.parseInt(skipLinesTextField.getText());
      input.setSkipLines(i);
      RuntimeSettings.INSTANCE.setCrsSkipLines(i);
    } catch (NumberFormatException exc) {
      JOptionPane.showMessageDialog(dialog, "Invalid number: " + skipLinesTextField.getText(),
          "Invalid number", JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      int i = Integer.parseInt(sheetNoTextField.getText().trim());
      input.setSheetNum(i);
      RuntimeSettings.INSTANCE.setCrsSheetNum(i);
    } catch (NumberFormatException exc) {
      JOptionPane.showMessageDialog(dialog, "Invalid number: " + sheetNoTextField.getText(),
          "Invalid number", JOptionPane.ERROR_MESSAGE);
      return;
    }
    new CrsProcessor(input).process();
  }

}
