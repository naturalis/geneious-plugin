package nl.naturalis.geneious.log;

import javax.swing.JDialog;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.bold.BoldDocumentOperation;
import nl.naturalis.geneious.crs.CrsDocumentOperation;
import nl.naturalis.geneious.gui.GeneiousGUI;
import nl.naturalis.geneious.seq.Ab1FastaDocumentOperation;
import nl.naturalis.geneious.smpl.SampleSheetDocumentOperation;
import nl.naturalis.geneious.split.SplitNameDocumentOperation;

/**
 * An Object defining the boundaries within which loggers can safely log messages. The boundaries are defined
 * by the try-with-resources block used to set up a log session (see {@link GuiLogManager#startSession(String)
 * GUILogManager.startSession}). All log messages generated within the try-with-resources block will appear in
 * the Geneious GUI. All log messages generated outside the try-with-resources block go nowhere (actually they
 * are printed to standard out, but the user will not be aware of this). Therefore it's best to set up a log
 * session in a top-level class, which in our case means: {@link BoldDocumentOperation},
 * {@link CrsDocumentOperation}, {@link SampleSheetDocumentOperation}, {@link Ab1FastaDocumentOperation}
 * or {@link SplitNameDocumentOperation}. Note that the {@link #close()} method currently does nothing, so
 * in theory you could call {@code GuiLogManager.startSession} without using a try-with-resources block, but
 * using this construct nonetheless provides better visual feedback about when logging is legal.
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
