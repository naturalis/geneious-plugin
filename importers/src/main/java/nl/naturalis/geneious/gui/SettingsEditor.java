package nl.naturalis.geneious.gui;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

public class SettingsEditor {
  
  private JDialog dialog;
  
  private JTextField fileTextField;
  private JCheckBox dummiesCheckBox;
  private JTextField sheetNoTextField;
  
  public void show() {
    dialog = new JDialog(GuiUtilities.getMainFrame(), "Edit settings", true);
    
   
    dialog.pack();
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.setVisible(true);
  }

}
