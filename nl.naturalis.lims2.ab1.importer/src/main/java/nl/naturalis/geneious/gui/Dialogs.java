package nl.naturalis.geneious.gui;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

public class Dialogs {

  public static void noDocumentsSelected() {
    showMessageDialog(GuiUtilities.getMainFrame(), "Please select at least one document",
        "No documents selected", ERROR_MESSAGE);
  }

}
