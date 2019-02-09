package nl.naturalis.geneious.gui.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Charsets;

import nl.naturalis.geneious.util.Str;

public class LogRecord {

  private static final String NEWLINE = System.getProperty("line.separator");
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  final Class<?> clazz;
  final LocalDateTime timestamp;
  final LogLevel level;
  final String message;
  final Throwable throwable;

  LogRecord(Class<?> clazz, LogLevel level, String message) {
    this(clazz, level, message, null);
  }

  LogRecord(Class<?> clazz, LogLevel level, String message, Throwable throwable) {
    this.clazz = clazz;
    this.timestamp = LocalDateTime.now();
    this.level = level;
    this.message = message;
    this.throwable = throwable;
  }

  public String toString(LogLevel logLevel) {
    StringBuilder sb = new StringBuilder(50);
    sb.append(Str.rpad(dtf.format(timestamp), 13, "| "));
    if (logLevel == LogLevel.DEBUG) {
      sb.append(Str.rpad(clazz.getSimpleName(), 15, "| "));
    }
    sb.append(Str.rpad(level, 7, "| "));
    sb.append(message);
    if (throwable != null) {
      sb.append(NEWLINE);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
      throwable.printStackTrace(new PrintStream(baos));
      sb.append(baos.toString(Charsets.UTF_8));
    }
    return sb.toString();
  }

  public String toString() {
    return toString(LogLevel.INFO);
  }

}
