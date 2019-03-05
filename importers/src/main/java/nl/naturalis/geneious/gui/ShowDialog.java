package nl.naturalis.geneious.gui;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Various prefab alerts.
 */
public class ShowDialog {

  public static void noDocumentsSelected() {
    showMessageDialog(GuiUtilities.getMainFrame(), "Please select at least one document",
        "No documents selected", ERROR_MESSAGE);
  }

  public static void invalidEncoding(String enc) {
    String fmt = "Invalid character encoding: %s. Do not use the Naturalis plugin. Please modify Geneious startup script and "
        + "restart Geneious. Add the following argument to java command line: -Dfile.encoding=UTF-8";
    String msg = String.format(fmt, enc);
    showMessageDialog(GuiUtilities.getMainFrame(), msg, "Invalid character encoding", ERROR_MESSAGE);
  }

  public static void documentNotEditable() {
    String msg = "One or more selected documents cannot be updated by the plugin";
    showMessageDialog(GuiUtilities.getMainFrame(), msg, "Documents cannot be updated", ERROR_MESSAGE);
  }

  public static void documentsMustBeInSameFolder() {
    String msg = "Selecting documents from different folders not allowed. All documents must be in the same folder";
    showMessageDialog(GuiUtilities.getMainFrame(), msg, "Selecting documents from different folders not allowed", ERROR_MESSAGE);
  }

  public static void documentsMustBeInSameDatabase() {
    String msg = "Selecting documents from different databases not allowed. All documents must be in the same database";
    showMessageDialog(GuiUtilities.getMainFrame(), msg, "Selecting documents from different databases not allowed", ERROR_MESSAGE);
  }

  public static void pleaseSelectTargetFolder() {
    String msg = "Please select a target folder for your documents";
    showMessageDialog(GuiUtilities.getMainFrame(), msg, "No target folder selected", ERROR_MESSAGE);
  }

}
