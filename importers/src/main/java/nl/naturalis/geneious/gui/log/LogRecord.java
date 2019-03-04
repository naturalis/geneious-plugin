package nl.naturalis.geneious.gui.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import static nl.naturalis.common.base.NExceptions.getRootStackTraceAsString;
import static nl.naturalis.common.base.NStrings.rpad;

public class LogRecord {

  private static final String NEWLINE = System.getProperty("line.separator");
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss SSS");

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
    String terminator = " | ";
    StringBuilder sb = new StringBuilder(128);
    sb.append(rpad(dtf.format(timestamp), 23, terminator));
    if (logLevel == LogLevel.DEBUG) {
      sb.append(rpad(clazz.getSimpleName(), 32, terminator));
    }
    sb.append(rpad(level, 6, terminator));
    if (!StringUtils.isEmpty(message)) {
      sb.append(message);
    }
    if (throwable != null) {
      sb.append(NEWLINE);
      sb.append(getRootStackTraceAsString(throwable));
    }
    return sb.toString();
  }

  public String toString() {
    return toString(LogLevel.INFO);
  }

}
