package nl.naturalis.geneious.gui;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

public class ShowDialog {

  public static void noDocumentsSelected() {
    showMessageDialog(GuiUtilities.getMainFrame(), "Please select at least one document",
        "No documents selected", ERROR_MESSAGE);
  }

  public static void invalidEncoding(String enc) {
    String fmt = "Invalid character encoding: %s. Do not use the Naturalis plugin. Please modify Geneious startup script first. "
        + "Add the following argument to Java command line:\n\n-Dfile.encoding=UTF-8";
    String msg = String.format(fmt, enc);
    showMessageDialog(GuiUtilities.getMainFrame(), msg, "Invalid character encoding", ERROR_MESSAGE);
  }

}
