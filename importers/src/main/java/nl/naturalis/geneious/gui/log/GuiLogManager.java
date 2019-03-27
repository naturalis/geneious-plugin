package nl.naturalis.geneious.gui.log;

import java.util.HashMap;

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
    return instance.getOrCreateLogger(clazz);
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

  private final LogWriter writer;
  private final HashMap<Class<?>, GuiLogger> loggers;

  private GuiLogManager() {
    this.writer = new LogWriter();
    this.loggers = new HashMap<>();
  };

  private GuiLogger getOrCreateLogger(Class<?> clazz) {
    GuiLogger logger = loggers.get(clazz);
    if (logger == null) {
      loggers.put(clazz, logger = new GuiLogger(clazz, writer));
    }
    return logger;
  }

}
