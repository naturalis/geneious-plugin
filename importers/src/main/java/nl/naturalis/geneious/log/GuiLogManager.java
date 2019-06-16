package nl.naturalis.geneious.log;

import java.util.HashMap;

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
   * Creates a new log session. Log sessions should be created using a try-with-resources block. Logger should only log
   * messages within the try-with-resources block, otherwise the user will not see them.
   * 
   * @param title
   * @return
   */
  public static LogSession startSession(String title) {
    return new LogSession(instance.writer, title);
  }

  private final LogWriter writer;
  private final HashMap<Class<?>, GuiLogger> loggers;

  private GuiLogManager() {
    this.writer = new LogWriter();
    this.loggers = new HashMap<>();
  }

  private GuiLogger getOrCreateLogger(Class<?> clazz) {
    GuiLogger logger = loggers.get(clazz);
    if (logger == null) {
      loggers.put(clazz, logger = new GuiLogger(clazz, writer));
    }
    return logger;
  }

}