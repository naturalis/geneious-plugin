package nl.naturalis.geneious.gui;

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
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

public class SampleSheetSelector {

  public static class Selection {
    public File sampleSheet;
    public boolean createDummies;
  }

  @FunctionalInterface
  public static interface SelectionHandler {
    void processSampleSheet(Selection selection);
  }

  private final SelectionHandler selectionHandler;

  public SampleSheetSelector(SelectionHandler saelectionHandler) {
    this.selectionHandler = saelectionHandler;
  }

  public void show() {

    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle("Select sample sheet");

    JPanel root = new JPanel();
    root.setLayout(new GridLayout(3, 1));

    JPanel row0 = new JPanel();
    row0.add(new JLabel("Sample sheet"));
    JTextField sampleSheetLocation = new JTextField(40);
    row0.add(sampleSheetLocation);
    row0.add(createBrowseButton(dialog, sampleSheetLocation));
    root.add(row0);

    JPanel row1 = new JPanel();
    JCheckBox createDummiesCheckbox = new JCheckBox();
    if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
      row1.add(new JLabel(
          "No trace files selected. Dummy trace files will be created for unknown Extract IDs"));
    } else {
      row1.add(createDummiesCheckbox);
      row1.add(new JLabel("Create dummies for unknown Extract IDs"));
    }
    root.add(row1);

    JPanel row2 = new JPanel();
    row2.add(createCancelButton(dialog));
    row2.add(createOkButton(dialog, sampleSheetLocation, createDummiesCheckbox));
    root.add(row2);

    dialog.setContentPane(root);
    dialog.pack();
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.setVisible(true);
  }

  private static JButton createBrowseButton(JDialog dialog, JTextField sampleSheetLocation) {
    JButton browseButton = new JButton("Browse");
    browseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(RuntimeSettings.INSTANCE.getSampleSheetFolder());
        if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
          RuntimeSettings.INSTANCE.setSampleSheetFolder(fc.getCurrentDirectory());
          File f = fc.getSelectedFile();
          if (f != null) {
            sampleSheetLocation.setText(f.getAbsolutePath());
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

  private JButton createOkButton(JDialog dialog, JTextField sampleSheetLocation,
      JCheckBox checkbox) {
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (StringUtils.isBlank(sampleSheetLocation.getText())) {
          JOptionPane.showMessageDialog(dialog, "Please select a sample sheet",
              "No sample sheet selected", JOptionPane.ERROR_MESSAGE);
        } else {
          File f = new File(sampleSheetLocation.getText());
          if (!f.exists() || !f.isFile()) {
            JOptionPane.showMessageDialog(dialog, "Invalid file: " + f.getName(), "Invalid file",
                JOptionPane.ERROR_MESSAGE);

          } else {
            Selection selection = new Selection();
            selection.sampleSheet = f;
            selection.createDummies = checkbox.isSelected();
            dialog.dispose();
            selectionHandler.processSampleSheet(selection);
          }
        }
      }
    });
    return okButton;
  }

}
