package nl.naturalis.geneious.bold;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
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
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

class BOLDFileSelector {

  @SuppressWarnings("static-method")
  void show() {

    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle("Select BOLD file");

    JPanel root = new JPanel();
    root.setLayout(new GridLayout(3, 1));

    JPanel row0 = new JPanel();
    row0.add(new JLabel("BOLD file"));
    JTextField boldFileLocation = new JTextField(40);
    row0.add(boldFileLocation);
    row0.add(createBrowseButton(dialog, boldFileLocation));
    root.add(row0);

    List<AnnotatedPluginDocument> docs = DocumentUtilities.getSelectedDocuments();

    JPanel row1 = new JPanel();
    JCheckBox skipHeaderCheckbox = new JCheckBox();
    if (docs.isEmpty()) {
      skipHeaderCheckbox.setSelected(true);
    }
    row1.add(skipHeaderCheckbox);
    row1.add(new JLabel("Skip 1st line (header row) in BOLD file"));
    root.add(row1);

    JPanel row2 = new JPanel();
    row2.add(createCancelButton(dialog));
    row2.add(createOkButton(dialog, boldFileLocation, skipHeaderCheckbox, docs));
    root.add(row2);

    dialog.setContentPane(root);
    dialog.pack();
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.setVisible(true);
  }

  private static JButton createBrowseButton(JDialog dialog, JTextField boldFileLocation) {
    JButton browseButton = new JButton("Browse");
    browseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(RuntimeSettings.INSTANCE.getSampleSheetFolder());
        if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
          RuntimeSettings.INSTANCE.setSampleSheetFolder(fc.getCurrentDirectory());
          File f = fc.getSelectedFile();
          if (f != null) {
            boldFileLocation.setText(f.getAbsolutePath());
          }
        }
      }
    });
    return browseButton;
  }

  private static JButton createCancelButton(JDialog dialog) {
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.dispose();
      }
    });
    return cancelButton;
  }

  private static JButton createOkButton(JDialog dialog, JTextField boldFileField,
      JCheckBox skipHeaderCheckbox, List<AnnotatedPluginDocument> docs) {
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (StringUtils.isBlank(boldFileField.getText())) {
          JOptionPane.showMessageDialog(dialog, "Please select a BOLD file",
              "No BOLD file selected", JOptionPane.ERROR_MESSAGE);
        } else if (docs.isEmpty() && !skipHeaderCheckbox.isSelected()) {
          JOptionPane.showMessageDialog(dialog, "Please select at least one document to enrich",
              "No document selected", JOptionPane.ERROR_MESSAGE);
        } else {
          File boldFile = new File(boldFileField.getText());
          if (!boldFile.isFile()) {
            JOptionPane.showMessageDialog(dialog, "Invalid file: " + boldFile.getName(),
                "Invalid file", JOptionPane.ERROR_MESSAGE);

          } else {
            dialog.dispose();
            BOLDProcessor processor =
                new BOLDProcessor(boldFile, docs, skipHeaderCheckbox.isSelected());
            processor.process();
          }
        }
      }
    });
    return okButton;
  }

}
