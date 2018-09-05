package nl.naturalis.geneious.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GridBagFormUtil {

  public static void addLabel(JPanel panel, int row, String label) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = row;
    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    panel.add(new JLabel(label, SwingConstants.RIGHT), c);
  }

  public static void addFileSelector(JPanel panel, int row, JTextField fileField,
      JButton browseButton) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = row;
    c.weightx = 1f;
    c.anchor = GridBagConstraints.WEST;
    JPanel subPanel = new JPanel(new GridBagLayout());
    panel.add(subPanel, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1f;
    subPanel.add(fileField, c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 0;
    c.anchor = GridBagConstraints.WEST;
    subPanel.add(browseButton, c);
  }

  public static void addTextFieldWithComment(JPanel panel, int row, JTextField input,
      String comment) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = row;
    c.weightx = 1f;
    c.anchor = GridBagConstraints.WEST;
    JPanel subPanel = new JPanel(new GridBagLayout());
    panel.add(subPanel, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    subPanel.add(input, c);
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    subPanel.add(new JLabel(comment), c);
  }

  public static void addCheckboxWithComment(JPanel panel, int row, JCheckBox input,
      String comment) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = row;
    c.weightx = 1f;
    c.anchor = GridBagConstraints.WEST;
    JPanel subPanel = new JPanel(new GridBagLayout());
    panel.add(subPanel, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    subPanel.add(input, c);
    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    subPanel.add(new JLabel(comment), c);
  }

  public static JPanel createFormPanel(JDialog dialog) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 1f;
    c.anchor = GridBagConstraints.NORTH;

    JPanel panel0 = new JPanel(new GridBagLayout());
    panel0.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    dialog.add(panel0, c);
    return panel0;
  }

  public static void createOKCancelPanel(JDialog dialog, JButton okButton) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1f;
    c.weighty = 0;
    c.anchor = GridBagConstraints.SOUTH;
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    dialog.add(panel, c);
    panel.add(okButton);
    panel.add(createCancelButton(dialog));
  }

  private static JButton createCancelButton(JDialog dialog) {
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

}
