package nl.naturalis.geneious.gui.log;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import com.google.common.base.Preconditions;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.GeneiousGUI;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;

public class GuiLogManager {

  private static final String NEWLINE = System.getProperty("line.separator");

  public static final GuiLogManager instance = new GuiLogManager();

  public static GuiLogger getLogger(Class<?> clazz) {
    return instance.get(clazz);
  }

  public static void setDebug(boolean debug) {
    instance.logLevel = debug ? DEBUG : INFO;
    instance.loggers.forEach((k, v) -> v.reset(instance.logLevel));
  }

  public static void showLog(String title) {
    instance.show(title);
  }

  public static void showLogAndClose(String title) {
    instance.show(title);
    close();
  }

  public static void close() {
    instance.records = new ArrayList<>();
    instance.logLevel = getLogLevel();
    instance.loggers.forEach((k, v) -> v.reset(instance.logLevel, instance.records));
  }

  private final HashMap<Class<?>, GuiLogger> loggers;

  private List<LogRecord> records;
  private LogLevel logLevel;

  private GuiLogManager() {
    this.loggers = new HashMap<>();
    this.records = new ArrayList<>();
    this.logLevel = getLogLevel();
  };

  private GuiLogger get(Class<?> clazz) {
    Preconditions.checkNotNull(clazz, "clazz must not be null");
    GuiLogger logger = loggers.get(clazz);
    if (logger == null) {
      loggers.put(clazz, logger = new GuiLogger(clazz, records, logLevel));
    }
    return logger;
  }

  private void show(String title) {
    this.logLevel = getLogLevel();
    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle(title);
    JTextArea textArea = new JTextArea(20, 140);
    textArea.setEditable(false);
    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    if (records.size() == 0) {
      textArea.append("Nothing has been logged");
    } else {
      for (LogRecord r : records) {
        textArea.append(r.toString(logLevel));
        textArea.append(NEWLINE);
      }
    }
    JScrollPane scrollPane = new JScrollPane(textArea);
    dialog.setContentPane(scrollPane);
    GeneiousGUI.scale(dialog, .95, .4, 960, 400);
    dialog.pack();
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.setVisible(true);
  }

  private static LogLevel getLogLevel() {
    return NaturalisPreferencesOptions.isDebug() ? DEBUG : INFO;
  }

}
