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

  private static final ThreadLocal<GuiLogManager> tl = ThreadLocal.withInitial(GuiLogManager::new);
  private static final String NEWLINE = System.getProperty("line.separator");

  public static GuiLogger getLogger(Class<?> clazz) {
    GuiLogManager mgr = tl.get();
    if (mgr.level == null) {
      LogLevel level = NaturalisPreferencesOptions.isDebug() ? DEBUG : INFO;
      mgr.initialize(level);
    }
    return mgr.get(clazz);
  }

  public static void showLog(String title) {
    tl.get().show(title);
  }

  public static void showLogAndClose(String title) {
    tl.get().show(title);
    tl.remove();
  }

  public static void close() {
    tl.remove();
  }

  private LogLevel level;
  private List<LogRecord> records;
  private HashMap<Class<?>, GuiLogger> loggers = new HashMap<>();

  private GuiLogManager() {};

  private void initialize(LogLevel level) {
    this.level = level;
    this.records = new ArrayList<>();
    this.loggers = new HashMap<>();
  }

  private GuiLogger get(Class<?> clazz) {
    Preconditions.checkNotNull(clazz, "clazz must not be null");
    GuiLogger gl = loggers.get(clazz);
    if (gl == null) {
      gl = new GuiLogger(clazz, level, records);
    }
    return gl;
  }

  private void show(String title) {
    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle(title);
    JTextArea textArea = new JTextArea(20, 100);
    textArea.setEditable(false);
    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    if (records == null) {
      textArea.append("Nothing has been logged");
    } else {
      for (LogRecord r : records) {
        textArea.append(r.toString());
        textArea.append(NEWLINE);
      }
    }
    JScrollPane scrollPane = new JScrollPane(textArea);
    dialog.setContentPane(scrollPane);
    GeneiousGUI.position(dialog);
    dialog.pack();
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.setVisible(true);
  }

}
