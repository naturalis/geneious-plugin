package nl.naturalis.geneious.crs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

class CrsFileSelector {

  private final CrsProcessor processor;
  private final AnnotatedPluginDocument[] selectedDocuments;

  private JDialog dialog;
  private JTextField crsFileTextField;
  private JTextField sheetNoTextField;
  private JTextField skipLinesTextField;

  private JCheckBox skipHeaderCheckbox;

  CrsFileSelector(CrsProcessor processor, AnnotatedPluginDocument[] docs) {
    this.processor = processor;
    this.selectedDocuments = docs;
  }

  void show() {

    dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle("Select CRS file");
    dialog.setLayout(new GridBagLayout());

    // FIRST ROW

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 1f;
    c.anchor = GridBagConstraints.NORTH;

    JPanel panel0 = new JPanel(new GridBagLayout());
    panel0.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    dialog.add(panel0, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    JLabel label = new JLabel("CRS File", SwingConstants.RIGHT);
    panel0.add(label, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1f;
    c.anchor = GridBagConstraints.WEST;
    JPanel panel1 = new JPanel(new GridBagLayout());
    panel0.add(panel1, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1f;
    crsFileTextField = new JTextField(50);
    panel1.add(crsFileTextField, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 0;
    c.anchor = GridBagConstraints.WEST;
    panel1.add(createBrowseButton(), c);

    // SECOND ROW

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Start with line", SwingConstants.RIGHT);
    panel0.add(label, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1f;
    c.anchor = GridBagConstraints.WEST;
    panel1 = new JPanel(new GridBagLayout());
    panel0.add(panel1, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    skipLinesTextField = new JTextField(4);
    skipLinesTextField.setText("1");
    panel1.add(skipLinesTextField, c);
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    panel1.add(new JLabel("(Applicable for spreadsheets, CSV, TSV, etc.)"), c);

    // THIRD ROW

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    label = new JLabel("Sheet number", SwingConstants.RIGHT);
    panel0.add(label, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1f;
    c.anchor = GridBagConstraints.WEST;
    panel1 = new JPanel(new GridBagLayout());
    panel0.add(panel1, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    sheetNoTextField = new JTextField(4);
    sheetNoTextField.setText("1");
    sheetNoTextField.setEnabled(false);
    panel1.add(sheetNoTextField, c);
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    panel1.add(new JLabel("(Only applicable when importing from spreadsheet)"), c);

    // OK / CANCEL

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1f;
    c.weighty = 0;
    c.anchor = GridBagConstraints.SOUTH;

    panel0 = new JPanel(new GridBagLayout());
    panel0.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    dialog.add(panel0, c);

    panel0.add(createOkButton());
    panel0.add(createCancelButton());

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
            } else {
              sheetNoTextField.setEnabled(false);
            }
          }
        }
      }
    });
    return browseButton;
  }

  private JButton createCancelButton() {
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setPreferredSize(new Dimension(100, cancelButton.getPreferredSize().height));
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.dispose();
      }
    });
    return cancelButton;
  }

  private JButton createOkButton() {
    JButton okButton = new JButton("OK");
    okButton.setPreferredSize(new Dimension(100, okButton.getPreferredSize().height));
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (StringUtils.isBlank(crsFileTextField.getText())) {
          JOptionPane.showMessageDialog(dialog, "Please select a CRS file", "No CRS file selected",
              JOptionPane.ERROR_MESSAGE);
        } else {
          File crsFile = new File(crsFileTextField.getText());
          if (!crsFile.isFile()) {
            JOptionPane.showMessageDialog(dialog, "Invalid file: " + crsFileTextField.getText(),
                "Invalid file", JOptionPane.ERROR_MESSAGE);

          } else {
            dialog.dispose();
            CrsProcessingOptions options = new CrsProcessingOptions();
            options.setSkipHeader(skipHeaderCheckbox.isSelected());
            processor.initialize(crsFile, options, selectedDocuments);
            processor.process();
          }
        }
      }
    });
    return okButton;
  }

}
