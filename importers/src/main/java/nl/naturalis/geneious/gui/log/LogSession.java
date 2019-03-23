package nl.naturalis.geneious.gui.log;

import javax.swing.JDialog;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.GeneiousGUI;
import nl.naturalis.geneious.seq.Ab1FastaDocumentOperation;

import static nl.naturalis.geneious.gui.log.LogLevel.DEBUG;
import static nl.naturalis.geneious.gui.log.LogLevel.INFO;

/**
 * An Object defining the boundaries within which logger can safely log messages. In other words, loggers must not be used outside the
 * boundaries of a log session. That means it's best to set up a log session in a top-level class (which in our case means: the
 * framework-plumbing classes like {@link Ab1FastaDocumentOperation}).
 *
 * @author Ayco Holleman
 */
public final class LogSession implements AutoCloseable {

  private final LogWriter writer;
  private final String title;

  LogSession(LogWriter writer, String title) {
    this.writer = writer;
    this.title = title;
    start();
  }

  @Override
  public void close() {
    // ...
  }

  private void start() {
    writer.initialize(getLogLevel());
    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle(title);
    dialog.setContentPane(writer.getScrollPane());
    GeneiousGUI.scale(dialog, .8, .4, 600, 400);
    dialog.setLocationRelativeTo(GuiUtilities.getMainFrame());
    dialog.pack();
    dialog.setVisible(true);
  }

  private static LogLevel getLogLevel() {
    return NaturalisPreferencesOptions.isDebug() ? DEBUG : INFO;
  }

}
