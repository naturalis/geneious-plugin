package nl.naturalis.geneious.gui;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import org.virion.jam.framework.AbstractFrame;

import nl.naturalis.geneious.util.QueryUtils;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Various prefab alerts.
 */
public class ShowDialog {

  public static void noDocumentsSelected() {
    showMessageDialog(frame(), "Please select at least one document",
        "No documents selected", ERROR_MESSAGE);
  }

  public static void invalidEncoding(String enc) {
    String fmt = "Invalid character encoding: %s. Do not use the Naturalis plugin. Please modify Geneious startup script and "
        + "restart Geneious. Add the following argument to java command line: -Dfile.encoding=UTF-8";
    String msg = String.format(fmt, enc);
    showMessageDialog(frame(), msg, "Invalid character encoding", ERROR_MESSAGE);
  }

  public static void documentNotEditable() {
    String msg = "One or more selected documents cannot be updated by the plugin";
    showMessageDialog(frame(), msg, "Documents cannot be updated", ERROR_MESSAGE);
  }

  public static void documentsMustBeInSameFolder() {
    String msg = "Selecting documents from different folders not allowed. All documents must be in the same folder";
    showMessageDialog(frame(), msg, "Selecting documents from different folders not allowed", ERROR_MESSAGE);
  }

  public static void documentsMustBeInSameDatabase() {
    String msg = "Selecting documents from different databases not allowed. All documents must be in the same database";
    showMessageDialog(frame(), msg, "Selecting documents from different databases not allowed", ERROR_MESSAGE);
  }

  public static void pleaseSelectTargetFolder() {
    String msg = "Please select a target folder for your documents";
    showMessageDialog(frame(), msg, "No target folder selected", ERROR_MESSAGE);
  }

  public static void pleaseSelectDatabase() {
    String msg = "Please select a database";
    showMessageDialog(frame(), msg, "No database selected", ERROR_MESSAGE);
  }

  public static boolean confirmRegenerateAnnotationMetadata() {
    String fmt = "This is advanced functionality! Do you really want to update Naturalis annotation metadata for database \"%s\"?";
    String msg = String.format(fmt, QueryUtils.getTargetDatabaseName());
    int answer = showConfirmDialog(frame(), msg, "Update annotation metadata?", OK_CANCEL_OPTION, WARNING_MESSAGE);
    return answer == OK_OPTION;
  }

  private static AbstractFrame frame() {
    return GuiUtilities.getMainFrame();
  }

}
