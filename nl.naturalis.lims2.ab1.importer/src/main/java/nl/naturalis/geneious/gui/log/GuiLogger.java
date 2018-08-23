package nl.naturalis.geneious.gui.log;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.ERROR;
import static nl.naturalis.geneious.gui.log.LogLevel.FATAL;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;
import static nl.naturalis.geneious.gui.log.LogLevel.WARNING;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

/**
 * Collects log messages in order to send them to the Geneious UI.
 * 
 * @author Ayco Holleman
 *
 */
public class GuiLogger {

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
        msg = String.format(msg, msgArgs);
      }
      records.add(new LogRecord(level, msg, t));
    }
  }

}
