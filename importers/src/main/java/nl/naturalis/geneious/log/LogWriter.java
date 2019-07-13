package nl.naturalis.geneious.log;

import java.awt.Font;
import java.time.format.DateTimeFormatter;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import static nl.naturalis.common.base.ExceptionTools.getRootStackTraceAsString;
import static nl.naturalis.common.base.StringTools.rpad;
import static nl.naturalis.geneious.Settings.*;
import static nl.naturalis.geneious.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.log.LogLevel.INFO;

/**
 * Accepts log records from loggers and turns them into messages, which it then sends to the Geneious GUI.
 *
 * @author Ayco Holleman
 */
class LogWriter {

  private static final String NEWLINE = System.getProperty("line.separator");
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");

  private LogLevel logLevel = INFO;
  private JScrollPane pane;
  private JScrollBar scrollbar;
  private JTextArea area;

  LogWriter() {
    logLevel = settings().isDebug() ? DEBUG : INFO;
  }

  /**
   * Prepares a new, empty log window.
   */
  void initialize() {
    logLevel = settings().isDebug() ? DEBUG : INFO;
    area = new JTextArea();
    area.setEditable(false);
    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    pane = new JScrollPane(area);
    scrollbar = pane.getVerticalScrollBar();
  }

  /**
   * Writes the provided {@link LogRecord} to the log window.
   * 
   * @param record
   */
  void write(LogRecord record) {
    if (area == null) {
      // A logger attempts to write outside of a log session. This can occasionally happen if Geneious calls plugin code while
      // initializing. It should never happen when the plugin itself is in control !!
      System.out.println("[OUTSIDE LOG SESSION] - " + toString(record));
    } else {
      area.append(toString(record));
      area.setCaretPosition(area.getDocument().getLength());
      scrollbar.setValue(scrollbar.getMaximum());
    }
  }

  /**
   * Returns the current log level.
   * 
   * @return
   */
  LogLevel getLogLevel() {
    return logLevel;
  }

  /**
   * Sets the log level.
   * 
   * @param level
   */
  void setLogLevel(LogLevel level) {
    this.logLevel = level;
  }

  /**
   * Returns the log window.
   * 
   * @return
   */
  JScrollPane getScrollPane() {
    return pane;
  }

  /**
   * Returns the text area within the log window.
   * 
   * @return
   */
  JTextArea getArea() {
    return area;
  }

  private String toString(LogRecord record) {
    String terminator = " | ";
    StringBuilder sb = new StringBuilder(160);
    sb.append(rpad(dtf.format(record.timestamp), 23, terminator));
    if (logLevel == DEBUG) {
      sb.append(rpad(record.clazz.getSimpleName(), 22, terminator));
    }
    sb.append(rpad(record.level, 6, terminator));
    if (!StringUtils.isEmpty(record.message)) {
      sb.append(record.message);
    }
    if (record.throwable != null) {
      sb.append(NEWLINE);
      sb.append(getRootStackTraceAsString(record.throwable));
    }
    sb.append(NEWLINE);
    return sb.toString();
  }

}
