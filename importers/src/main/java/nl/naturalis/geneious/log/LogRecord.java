package nl.naturalis.geneious.log;

import java.time.LocalDateTime;

/**
 * Contains all data required to write a single log message.
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

  /**
   * Creates a new log record for the specified class and log level and containing the provided message.
   * @param clazz
   * @param level
   * @param message
   */
  LogRecord(Class<?> clazz, LogLevel level, String message) {
    this(clazz, level, message, null);
  }

  /**
   * Creates a new log record for the specified class and log and containing the provided message and {@code Throwable}.
   * @param clazz
   * @param level
   * @param message
   * @param throwable
   */
  LogRecord(Class<?> clazz, LogLevel level, String message, Throwable throwable) {
    this.clazz = clazz;
    this.timestamp = LocalDateTime.now();
    this.level = level;
    this.message = message;
    this.throwable = throwable;
  }

}
