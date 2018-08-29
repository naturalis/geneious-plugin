package nl.naturalis.geneious.crs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

class CrsFileSelector {

  private final CrsProcessor processor;
  private final AnnotatedPluginDocument[] selectedDocuments;

  private JDialog dialog;
  private JTextField crsFileTextField;
  private JCheckBox skipHeaderCheckbox;

  CrsFileSelector(CrsProcessor processor, AnnotatedPluginDocument[] docs) {
    this.processor = processor;
    this.selectedDocuments = docs;
  }

  void show() {

    dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle("Select CRS file");

    JPanel root = new JPanel();
    root.setLayout(new GridLayout(3, 1));

    JPanel row0 = new JPanel();
    row0.add(new JLabel("CRS file"));
    crsFileTextField = new JTextField(40);
    row0.add(crsFileTextField);
    row0.add(createBrowseButton());
    root.add(row0);

    JPanel row1 = new JPanel();
    skipHeaderCheckbox = new JCheckBox();
    skipHeaderCheckbox.setSelected(true);
    row1.add(skipHeaderCheckbox);
    row1.add(new JLabel("Skip 1st line (header row) in CRS file"));
    root.add(row1);

    JPanel row2 = new JPanel();
    row2.add(createCancelButton());
    row2.add(createOkButton());
    root.add(row2);

    dialog.setContentPane(root);
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
          }
        }
      }
    });
    return browseButton;
  }

  private JButton createCancelButton() {
    JButton cancelButton = new JButton("Cancel");
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
