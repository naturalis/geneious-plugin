package nl.naturalis.geneious.log;

import java.util.HashMap;

import nl.naturalis.geneious.PluginSwingWorker;

/**
 * Manages the loggers created by the classes participating in an operation.
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
   * Start a new log session. Log sessions must be created using a try-with-resources block. Loggers should only log messages within the
   * try-with-resources block, otherwise the messages will not become visible.
   * 
   * @param title
   * @return
   */
  public static LogSession startSession(PluginSwingWorker<?> worker, String title) {
    return new LogSession(worker, instance.writer, title);
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
