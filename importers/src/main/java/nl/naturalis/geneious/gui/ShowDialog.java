package nl.naturalis.geneious.gui;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.nio.charset.Charset;

import org.virion.jam.framework.AbstractFrame;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.util.Ping;
import nl.naturalis.geneious.util.QueryUtils;

/**
 * Various prefab alerts and dialogs.
 */
public class ShowDialog {

  /**
   * Message informing the user that no database has been selected yet.
   */
  public static void pleaseSelectDatabase() {
    String msg = "Please select a database";
    showMessageDialog(frame(), msg, "No database selected", ERROR_MESSAGE);
  }

  /**
   * Confirmation dialog asking the user whether he/she really wants to re-generate the the note type definitions for the
   * Naturalis-specific annotations.
   * 
   * @return
   */
  public static boolean confirmRegenerateAnnotationMetadata() {
    String msg = "This is advanced functionality that will impact the entire database. Only do this if you"
        + "understand the consequences! Do you really want to update Naturalis annotation metadata for database "
        + QueryUtils.getTargetDatabaseName() + "?";
    int answer = showConfirmDialog(frame(), msg, "Update annotation metadata?", OK_CANCEL_OPTION, WARNING_MESSAGE);
    return answer == OK_OPTION;
  }

  /**
   * Confirmation dialog asking the user whether he/she wants to continue importing file even though the plugin has
   * detected its encoding is not UTF-8.
   * 
   * @param fileName
   * @param charset
   * @return
   */
  public static boolean continueWithDetectedCharset(String fileName, Charset charset) {
    String fmt = "It seems like the character encoding of %s is %s. Since Geneious expects text to be encoded as UTF-8, "
        + "this may cause diacritics like Ç, Ű or × (for hybrids) to be stored and displayed incorrectly in Geneious.\n\n"
        + "Do you want to continue?";
    String msg = String.format(fmt, fileName, charset);
    int answer = showConfirmDialog(frame(), msg, "Potentially invalid character encoding", OK_CANCEL_OPTION, WARNING_MESSAGE);
    return answer == OK_OPTION;
  }

  /**
   * Message informing that the ping history has been cleared. See {@link Ping}.
   */
  public static void pingHistoryCleared() {
    showMessageDialog(frame(), "Ping history cleared");
  }

  private static AbstractFrame frame() {
    return GuiUtilities.getMainFrame();
  }

}
