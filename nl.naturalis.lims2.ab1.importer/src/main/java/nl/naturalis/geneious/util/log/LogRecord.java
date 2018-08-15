package nl.naturalis.geneious.util.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import nl.naturalis.geneious.util.Str;

public class LogRecord {

  private static final String NEWLINE = System.getProperty("line.separator");
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  private final LocalDateTime timestamp;
  private final LogLevel level;
  private final String message;
  private final Throwable throwable;

  LogRecord(LogLevel level, String message) {
    this(level, message, null);
  }

  LogRecord(LogLevel level, String message, Throwable throwable) {
    this.timestamp = LocalDateTime.now();
    this.level = level;
    this.message = message;
    this.throwable = throwable;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder(50);
    sb.append(Str.rpad(dtf.format(timestamp), 13, "| "));
    sb.append(Str.rpad(level, 7, "| "));
    sb.append(message);
    if (throwable != null) {
      sb.append(NEWLINE);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
      throwable.printStackTrace(new PrintStream(baos));
      try {
        sb.append(baos.toString("UTF-8"));
      } catch (UnsupportedEncodingException e) {
      }
    }
    return sb.toString();
  }

}
