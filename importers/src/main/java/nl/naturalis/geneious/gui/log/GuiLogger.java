package nl.naturalis.geneious.gui.log;

import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Arrays.copyOfRange;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.ERROR;
import static nl.naturalis.geneious.gui.log.LogLevel.FATAL;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;
import static nl.naturalis.geneious.gui.log.LogLevel.WARN;

/**
 * A logger implementation that is rather tightly coupled to Geneious and Swing, in that sends sends messages to a
 * JTextArea rather than to regular destinations like a file ro console.
 * 
 * @author Ayco Holleman
 *
 */
public class GuiLogger {

  /**
   * Provides syntactic sugar when using Supplier-based log methods. The first element is assumed to be the message
   * pattern and the remaining arguments the message arguments passed to String.format.
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

  public static String plural(Collection<?> c) {
    return c.size() == 1 ? "" : "s";
  }

  private final Class<?> clazz;
  private final LogWriter writer;

  GuiLogger(Class<?> clazz, LogWriter writer) {
    this.clazz = clazz;
    this.writer = writer;
  }

  /**
   * Whether or not DEBUG messages will be logged.
   * 
   * @return
   */
  public boolean isDebugEnabled() {
    return writer.getLogLevel().ordinal() >= DEBUG.ordinal();
  }

  /**
   * Whether or not INFO messages will be logged.
   * 
   * @return
   */
  public boolean isInfoEnabled() {
    return writer.getLogLevel().ordinal() >= INFO.ordinal();
  }

  /**
   * Whether or not WARN messages will be logged.
   * 
   * @return
   */
  public boolean isWarnEnabled() {
    return writer.getLogLevel().ordinal() >= WARN.ordinal();
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

  public void info(Supplier<String> msgSupplier) {
    record(INFO, msgSupplier, null);
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

  public void fatal(Throwable throwable) {
    record(FATAL, throwable.getMessage(), throwable);
  }

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
