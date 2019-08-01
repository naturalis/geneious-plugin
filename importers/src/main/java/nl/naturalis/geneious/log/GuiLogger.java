package nl.naturalis.geneious.log;

import static java.util.Arrays.copyOfRange;
import static nl.naturalis.geneious.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.log.LogLevel.ERROR;
import static nl.naturalis.geneious.log.LogLevel.FATAL;
import static nl.naturalis.geneious.log.LogLevel.INFO;
import static nl.naturalis.geneious.log.LogLevel.WARN;

import java.util.Collection;
import java.util.function.Supplier;

import nl.naturalis.common.base.ArrayUtil;

/**
 * Provides various ways of logging messages.
 * 
 * @author Ayco Holleman
 *
 */
public class GuiLogger {

  /**
   * Provides syntactic sugar when using the {@code Supplier}-based log methods. The first argument is presumed to be the message pattern
   * and the second the message arguments passed to {@code String.format()}.
   * 
   * @param pattern
   * @param args
   * @return
   */
  public static Object[] format(String pattern, Object... args) {
    return ArrayUtil.prefix(args, pattern);
  }

  /**
   * Returns "s" if the collection is empty or contains more than one element; otherwise an empty string.
   * 
   * @param c
   * @return
   */
  public static String plural(Collection<?> c) {
    return c.size() == 1 ? "" : "s";
  }

  /**
   * Returns "s" if {@code i} is 0 or greater than 1; otherwise an empty string.
   * 
   * @param i
   * @return
   */
  public static String plural(int i) {
    return i == 1 ? "" : "s";
  }

  private final Class<?> clazz;
  private final LogWriter writer;

  /**
   * Creates a logger for the specified class using the provided writer.
   * 
   * @param clazz
   * @param writer
   */
  GuiLogger(Class<?> clazz, LogWriter writer) {
    this.clazz = clazz;
    this.writer = writer;
  }

  /**
   * Whether or not DEBUG messages must be logged.
   * 
   * @return
   */
  public boolean isDebugEnabled() {
    return writer.getLogLevel().ordinal() >= DEBUG.ordinal();
  }

  /**
   * Whether or not INFO messages must be logged.
   * 
   * @return
   */
  public boolean isInfoEnabled() {
    return writer.getLogLevel().ordinal() >= INFO.ordinal();
  }

  /**
   * Whether or not WARN messages must be logged.
   * 
   * @return
   */
  public boolean isWarnEnabled() {
    return writer.getLogLevel().ordinal() >= WARN.ordinal();
  }

  /**
   * Logs a DEBUG message.
   * 
   * @param message
   * @param msgArgs
   */
  public void debug(String message, Object... msgArgs) {
    record(DEBUG, message, null, msgArgs);
  }

  /**
   * Logs a DEBUG message by calling the provided message supplier's {@code get()} method to retrieve the message.
   * 
   * @param messageSupplier
   */
  public void debug(Supplier<String> messageSupplier) {
    record(DEBUG, messageSupplier, null);
  }

  /**
   * Logs a DEBUG message by calling the provided message supplier's {@code get()} method. The first element in the array returned from
   * {@code get()} must be the message pattern; the remaining elements are presumed to be message arguments. See also
   * {@link #format(String, Object...) format}.
   * 
   * @param messageSupplier
   */
  public void debugf(Supplier<Object[]> messageSupplier) {
    recordf(DEBUG, messageSupplier, null);
  }

  /**
   * Logs an INFO message.
   * 
   * @param message
   * @param msgArgs
   */
  public void info(String message, Object... msgArgs) {
    record(INFO, message, null, msgArgs);
  }

  /**
   * Logs an INFO message by calling the provided message supplier's {@code get()} method to retrieve the message.
   * 
   * @param messageSupplier
   */
  public void info(Supplier<String> messageSupplier) {
    record(INFO, messageSupplier, null);
  }

  /**
   * Logs an INFO message by calling the provided message supplier's {@code get()} method. The first element in the array returned from
   * {@code get()} must be the message pattern; the remaining elements are presumed to be message arguments. See also
   * {@link #format(String, Object...) format}.
   * 
   * @param messageSupplier
   */
  public void infof(Supplier<Object[]> messageSupplier) {
    recordf(INFO, messageSupplier, null);
  }

  /**
   * Logs a WARN message.
   * 
   * @param message
   * @param msgArgs
   */
  public void warn(String message, Object... msgArgs) {
    record(WARN, message, null, msgArgs);
  }

  /**
   * Logs a WARN message by calling the provided message supplier's {@code get()} method to retrieve the message.
   * 
   * @param messageSupplier
   */
  public void warn(Supplier<String> messageSupplier) {
    record(WARN, messageSupplier, null);
  }

  /**
   * Logs a WARN message by calling the provided message supplier's {@code get()} method. The first element in the array returned from
   * {@code get()} must be the message pattern; the remaining elements are presumed to be message arguments. See also
   * {@link #format(String, Object...) format}.
   * 
   * @param messageSupplier
   */
  public void warnf(Supplier<Object[]> messageSupplier) {
    recordf(WARN, messageSupplier, null);
  }

  /**
   * Logs an ERROR message.
   * 
   * @param message
   * @param msgArgs
   */
  public void error(String message, Object... msgArgs) {
    record(ERROR, message, null, msgArgs);
  }

  /**
   * Logs an ERROR message including the provided exception's stack trace.
   * 
   * @param message
   * @param msgArgs
   */
  public void error(String message, Throwable throwable, Object... msgArgs) {
    record(ERROR, message, throwable, msgArgs);
  }

  /**
   * Logs a FATAL message.
   * 
   * @param message
   * @param msgArgs
   */
  public void fatal(String message, Object... msgArgs) {
    record(FATAL, message, null, msgArgs);
  }

  /**
   * Logs a FATAL message consisting of a plain stack trace only.
   * 
   * @param throwable
   */
  public void fatal(Throwable throwable) {
    record(FATAL, throwable.getMessage(), throwable);
  }

  /**
   * Logs an FATAL message including the provided exception's stack trace.
   * 
   * @param message
   * @param throwable
   * @param msgArgs
   */
  public void fatal(String message, Throwable throwable, Object... msgArgs) {
    record(FATAL, message, throwable, msgArgs);
  }

  private void record(LogLevel level, String msg, Throwable exc, Object... msgArgs) {
    if (level.ordinal() >= writer.getLogLevel().ordinal()) {
      if (msgArgs.length > 0) {
        writer.write(new LogRecord(clazz, level, String.format(msg, msgArgs), exc));
      } else {
        writer.write(new LogRecord(clazz, level, msg, exc));
      }
    }
  }

  private void record(LogLevel level, Supplier<String> msgSupplier, Throwable exc) {
    if (level.ordinal() >= writer.getLogLevel().ordinal()) {
      writer.write(new LogRecord(clazz, level, msgSupplier.get(), exc));
    }
  }

  private void recordf(LogLevel level, Supplier<Object[]> msgSupplier, Throwable exc) {
    if (level.ordinal() >= writer.getLogLevel().ordinal()) {
      Object[] chunks = msgSupplier.get();
      if (chunks.length == 1) {
        writer.write(new LogRecord(clazz, level, chunks[0].toString(), exc));
      } else {
        String msg = String.format(chunks[0].toString(), copyOfRange(chunks, 1, chunks.length));
        writer.write(new LogRecord(clazz, level, msg, exc));
      }
    }
  }

}
