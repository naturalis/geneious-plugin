package nl.naturalis.geneious.gui.log;

import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.copyOfRange;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.ERROR;
import static nl.naturalis.geneious.gui.log.LogLevel.FATAL;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;
import static nl.naturalis.geneious.gui.log.LogLevel.WARN;

/**
 * Sends log messages to the Geneious GUI. 
 * 
 * @author Ayco Holleman
 *
 */
public class GuiLogger {

  /**
   * Provides syntactic sugar when using Supplier-based log methods. The first element is assumed to be the message pattern and the
   * remaining arguments the message arguments passed to String.format.
   * 
   * @param args
   * @return
   */
  public static Object[] format(String pattern, Object... args) {
    Object[] objs = new Object[args.length + 1];
    objs[0] = pattern;
    System.arraycopy(args, 0, objs, 1, args.length);
    return objs;
  }

  private final Class<?> clazz;

  private List<LogRecord> records;
  private LogLevel logLevel;

  GuiLogger(Class<?> clazz, List<LogRecord> records, LogLevel logLevel) {
    this.clazz = clazz;
    this.logLevel = logLevel;
    this.records = records;
  }

  void reset(LogLevel level) {
    this.logLevel = level;
  }

  void reset(LogLevel logLevel, List<LogRecord> records) {
    this.logLevel = logLevel;
    this.records = records;
  }

  /**
   * Whether or not DEBUG messages will be logged.
   * 
   * @return
   */
  public boolean isDebugEnabled() {
    return logLevel.ordinal() >= DEBUG.ordinal();
  }

  /**
   * Whether or not INFO messages will be logged.
   * 
   * @return
   */
  public boolean isInfoEnabled() {
    return logLevel.ordinal() >= INFO.ordinal();
  }

  /**
   * Whether or not WARN messages will be logged.
   * 
   * @return
   */
  public boolean isWarnEnabled() {
    return logLevel.ordinal() >= WARN.ordinal();
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

  public void infof(Supplier<Object[]> msgSupplier) {
    recordf(INFO, msgSupplier, null);
  }

  public void warn(String message, Object... msgArgs) {
    record(WARN, message, null, msgArgs);
  }

  public void warnf(Supplier<Object[]> msgSupplier) {
    recordf(WARN, msgSupplier, null);
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

  private void record(LogLevel level, String msg, Throwable exc, Object... msgArgs) {
    if (level.ordinal() >= logLevel.ordinal()) {
      if (msgArgs.length > 0) {
        records.add(new LogRecord(clazz, level, String.format(msg, msgArgs), exc));
      } else {
        records.add(new LogRecord(clazz, level, msg, exc));
      }
    }
  }

  private void record(LogLevel level, Supplier<String> msgSupplier, Throwable exc) {
    if (level.ordinal() >= logLevel.ordinal()) {
      records.add(new LogRecord(clazz, level, msgSupplier.get(), exc));
    }
  }

  private void recordf(LogLevel level, Supplier<Object[]> msgSupplier, Throwable exc) {
    if (level.ordinal() >= logLevel.ordinal()) {
      Object[] chunks = msgSupplier.get();
      if (chunks.length == 1) {
        records.add(new LogRecord(clazz, level, chunks[0].toString(), exc));
      } else {
        String msg = String.format(chunks[0].toString(), copyOfRange(chunks, 1, chunks.length));
        records.add(new LogRecord(clazz, level, msg, exc));
      }
    }
  }

}
