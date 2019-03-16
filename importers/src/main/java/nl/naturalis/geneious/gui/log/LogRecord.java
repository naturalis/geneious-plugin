package nl.naturalis.geneious.gui.log;

import java.time.LocalDateTime;

/**
 * An immutable Java been containing all data to write out a log message.
 *
 * @author Ayco Holleman
 */
class LogRecord {

  /**
   * The class of the logger that submitted the log record.
   */
  final Class<?> clazz;
  /**
   * Time when the log record was created.
   */
  final LocalDateTime timestamp;
  /**
   * The message type of the log record was submitted.
   */
  final LogLevel level;
  /**
   * The basic message submitted by the logger.
   */
  final String message;
  /**
   * An optional {@code Exception} object provided by the logger, used to print out stack traces.
   */
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

}
