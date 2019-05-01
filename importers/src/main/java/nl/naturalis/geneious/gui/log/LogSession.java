package nl.naturalis.geneious.gui.log;

import javax.swing.JDialog;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.gui.GeneiousGUI;
import nl.naturalis.geneious.seq.SequenceImportDocumentOperation;

/**
 * An Object defining the boundaries within which loggers can safely log messages. In other words, loggers must not be
 * used outside the boundaries of a log session. It's best to set up a log session in a top-level class, which in our
 * case means: the {@code DocumentOperation} implementation classes like {@link SequenceImportDocumentOperation}.
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
    writer.initialize();
    JDialog dialog = new JDialog(GuiUtilities.getMainFrame());
    dialog.setTitle(title);
    dialog.setContentPane(writer.getScrollPane());
    GeneiousGUI.scale(dialog, .5, .4, 600, 400);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
  }

}
