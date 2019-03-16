package nl.naturalis.geneious.gui.log;

import java.awt.Font;
import java.time.format.DateTimeFormatter;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;

import static nl.naturalis.common.base.NExceptions.getRootStackTraceAsString;
import static nl.naturalis.common.base.NStrings.rpad;
import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;

/**
 * Accepts log records from loggers and turns them into messages, which it then sends to the Geneious GUI.
 *
 * @author Ayco Holleman
 */
class LogWriter {

  private static final String NEWLINE = System.getProperty("line.separator");
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss SSS");

  private LogLevel level;
  private JScrollPane pane;
  private JScrollBar scrollbar;
  private JTextArea area;

  LogWriter(LogLevel level) {
    reset(level);
  }

  void reset(LogLevel level) {
    this.level = level;
    this.area = new JTextArea();
    area.setEditable(false);
    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    this.pane = new JScrollPane(area);
    this.scrollbar = pane.getVerticalScrollBar();
  }

  void write(LogRecord record) {
    area.append(toString(record));
    area.setCaretPosition(area.getDocument().getLength());
    scrollbar.setValue(scrollbar.getMaximum());
  }

  LogLevel getLogLevel() {
    return level;
  }

  void setLogLevel(LogLevel level) {
    this.level = level;
  }

  JScrollPane getScrollPane() {
    return pane;
  }

  JTextArea getArea() {
    return area;
  }

  private String toString(LogRecord record) {
    String terminator = " | ";
    StringBuilder sb = new StringBuilder(64);
    sb.append(rpad(dtf.format(record.timestamp), 23, terminator));
    if (level == DEBUG) {
      sb.append(rpad(record.clazz.getSimpleName(), 32, terminator));
    }
    sb.append(rpad(level, 6, terminator));
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
