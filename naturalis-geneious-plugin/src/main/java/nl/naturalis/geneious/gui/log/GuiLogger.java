package nl.naturalis.geneious.gui.log;

import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.copyOfRange;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.ERROR;
import static nl.naturalis.geneious.gui.log.LogLevel.FATAL;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;
import static nl.naturalis.geneious.gui.log.LogLevel.WARNING;

/**
 * A logger implementation that sends messages to the Geneious GUI.
 * 
 * @author Ayco Holleman
 *
 */
public class GuiLogger {

  /**
   * Provides some syntactic sugar when using Supplier-based log methods like {@code #debugf(Supplier) debugf}. The first element is assumed
   * to be the message pattern and the remaining elements the message arguments passed to String.format.
   * 
   * @param messageElements
   * @return
   */
  public static Object[] format(Object... messageElements) {
    return messageElements;
  }

  private final Class<?> clazz;

  private List<LogRecord> records;
  private LogLevel logLevel;

  GuiLogger(Class<?> clazz, List<LogRecord> records, LogLevel level) {
    this.clazz = clazz;
    this.logLevel = level;
    this.records = records;
  }

  void reset(LogLevel level) {
    this.logLevel = level;
  }

  void reset(LogLevel level, List<LogRecord> records) {
    this.logLevel = level;
    this.records = records;
  }

  public void debug(String message, Object... msgArgs) {
    record(DEBUG, message, null, msgArgs);
  }

  public void debug(Supplier<String> msgSupplier) {
    record(DEBUG, msgSupplier, null);
  }

  public void debugf(Supplier<Object[]> msgSupplier) {
    recordf(DEBUG, msgSupplier, null);
  }

  public void info(String message, Object... msgArgs) {
    record(INFO, message, null, msgArgs);
  }

  public void warn(String message, Object... msgArgs) {
    record(WARNING, message, null, msgArgs);
  }

  public void warnf(Supplier<Object[]> msgSupplier) {
    recordf(WARNING, msgSupplier, null);
  }

  public void error(String message, Object... msgArgs) {
    record(ERROR, message, null, msgArgs);
  }

  public void error(String message, Throwable throwable, Object... msgArgs) {
    record(ERROR, message, throwable, msgArgs);
  }

  public void fatal(String message, Object... msgArgs) {
    record(FATAL, message, null, msgArgs);
  }

  public void fatal(String message, Throwable throwable, Object... msgArgs) {
    record(FATAL, message, throwable, msgArgs);
  }

  private void record(LogLevel lvl, String msg, Throwable t, Object... msgArgs) {
    if (lvl.ordinal() >= logLevel.ordinal()) {
      if (msgArgs.length > 0) {
        records.add(new LogRecord(clazz, lvl, String.format(msg, msgArgs), t));
      } else {
        records.add(new LogRecord(clazz, lvl, msg, t));
      }
    }
  }

  private void record(LogLevel lvl, Supplier<String> msgSupplier, Throwable t) {
    if (lvl.ordinal() >= logLevel.ordinal()) {
      records.add(new LogRecord(clazz, lvl, msgSupplier.get(), t));
    }
  }

  private void recordf(LogLevel lvl, Supplier<Object[]> msgSupplier, Throwable t) {
    if (lvl.ordinal() >= logLevel.ordinal()) {
      Object[] chunks = msgSupplier.get();
      if (chunks.length == 0) {
        throw new IllegalArgumentException("Supplied string array must contain at least one element");
      } else if (chunks.length == 1) {
        records.add(new LogRecord(clazz, lvl, chunks[0].toString(), t));
      } else {
        String msg = String.format(chunks[0].toString(), copyOfRange(chunks, 1, chunks.length));
        records.add(new LogRecord(clazz, lvl, msg, t));
      }
    }
  }

}
