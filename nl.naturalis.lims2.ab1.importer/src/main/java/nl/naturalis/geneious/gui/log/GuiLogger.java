package nl.naturalis.geneious.gui.log;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.util.RuntimeSettings;

import static java.util.Arrays.copyOfRange;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.ERROR;
import static nl.naturalis.geneious.gui.log.LogLevel.FATAL;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;
import static nl.naturalis.geneious.gui.log.LogLevel.WARNING;

/**
 * A looger sending its output to the Geneious GUI.
 * 
 * @author Ayco Holleman
 *
 */
public class GuiLogger {

  /**
   * Provides some syntactic sugar when using the Supplier-based log methods. The first element is supposed to be the message pattern and
   * the remaining elements the message arguments passed to String.format.
   * 
   * @param messageElements
   * @return
   */
  public static Object[] format(Object... messageElements) {
    return messageElements;
  }

  private static final String NEWLINE = System.getProperty("line.separator");

  private final List<LogRecord> records = new ArrayList<>();
  private final LogLevel logLevel;

  public GuiLogger() {
    this(RuntimeSettings.INSTANCE.getLogLevel());
  }

  public GuiLogger(LogLevel logLevel) {
    this.logLevel = logLevel;
  }

  public void showLog(String title) {
    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle(title);
    JTextArea textArea = new JTextArea(20, 100);
    textArea.setEditable(false);
    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    for (LogRecord r : records) {
      textArea.append(r.toString());
      textArea.append(NEWLINE);
    }
    JScrollPane scrollPane = new JScrollPane(textArea);
    dialog.setContentPane(scrollPane);
    dialog.pack();
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.setVisible(true);
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

  public void warning(String message, Object... msgArgs) {
    record(WARNING, message, null, msgArgs);
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

  private void record(LogLevel level, String msg, Throwable t, Object... msgArgs) {
    if (level.ordinal() >= logLevel.ordinal()) {
      if (msgArgs.length > 0) {
        records.add(new LogRecord(level, String.format(msg, msgArgs), t));
      } else {
        records.add(new LogRecord(level, msg, t));
      }
    }
  }

  private void record(LogLevel level, Supplier<String> msgSupplier, Throwable t) {
    if (level.ordinal() >= logLevel.ordinal()) {
      records.add(new LogRecord(level, msgSupplier.get(), t));
    }
  }

  private void recordf(LogLevel level, Supplier<Object[]> msgSupplier, Throwable t) {
    if (level.ordinal() >= logLevel.ordinal()) {
      Object[] chunks = msgSupplier.get();
      if (chunks.length == 0) {
        throw new IllegalArgumentException("Supplied string array must contain at least one element");
      } else if (chunks.length == 1) {
        records.add(new LogRecord(level, chunks[0].toString(), t));
      } else {
        String msg = String.format(chunks[0].toString(), copyOfRange(chunks, 1, chunks.length));
        records.add(new LogRecord(level, msg, t));
      }
    }
  }

}
