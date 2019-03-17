package nl.naturalis.geneious.gui.log;

import java.util.HashMap;

import javax.swing.JDialog;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.GeneiousGUI;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;

/**
 * Keeps track of, and manages the loggers created by the classes participating in the various plugin actions.
 *
 * @author Ayco Holleman
 */
public class GuiLogManager {

  private static final GuiLogManager instance = new GuiLogManager();

  /**
   * Returns a logger for the specified class.
   * 
   * @param clazz
   * @return
   */
  public static GuiLogger getLogger(Class<?> clazz) {
    return instance.get(clazz);
  }

  /**
   * Called by action listener on checkbox in Tools -> Preferences.
   * 
   * @param title
   */
  public static void setDebug(boolean debug) {
    instance.writer.setLogLevel(debug ? DEBUG : INFO);
  }

  public static LogSession startSession(String title) {
    return new LogSession(instance.writer, title);
  }

  public static void showLogAndClose(String title) {
    instance.show(title);
    close();
  }

  public static void close() {

  }

  private final LogWriter writer;
  private final HashMap<Class<?>, GuiLogger> loggers;

  private GuiLogManager() {
    this.writer = new LogWriter();
    this.loggers = new HashMap<>();
  };

  private GuiLogger get(Class<?> clazz) {
    GuiLogger logger = loggers.get(clazz);
    if (logger == null) {
      loggers.put(clazz, logger = new GuiLogger(clazz, writer));
    }
    return logger;
  }

  private void show(String title) {
    writer.initialize(getLogLevel());
    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle(title);
    dialog.setContentPane(writer.getScrollPane());
    GeneiousGUI.scale(dialog, .95, .4, 960, 400);
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.pack();
    dialog.setVisible(true);
  }

  private static LogLevel getLogLevel() {
    return NaturalisPreferencesOptions.isDebug() ? DEBUG : INFO;
  }

}
