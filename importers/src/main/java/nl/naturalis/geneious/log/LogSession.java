package nl.naturalis.geneious.log;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import nl.naturalis.geneious.gui.GeneiousGUI;

/**
 * An Object defining the boundaries within which loggers can safely log messages. The boundaries are defined by the try-with-resources
 * block used to set up a log session (see {@link GuiLogManager#startSession(String) GUILogManager.startSession}). All log messages
 * generated within the try-with-resources block will appear in the Geneious GUI. All log messages generated outside the try-with-resources
 * block go nowhere (actually they are printed to standard out, but the user will not be aware of this). Therefore it's best to set up a log
 * session in a top-level class.
 *
 * @author Ayco Holleman
 */
public final class LogSession implements AutoCloseable {

  private final LogWriter writer;
  private final String title;

  /**
   * Starts a new log session.
   * 
   * @param writer
   * @param title
   */
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
    writer.getArea().addMouseListener(new MouseAdapter() {
      private Dimension windowSize;

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
          if (windowSize == null) {
            windowSize = dialog.getSize();
            dialog.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
          } else {
            dialog.setSize(windowSize);
            dialog.setLocationRelativeTo(null);
            windowSize = null;
          }
        }
      }
    });
    dialog.setTitle(title);
    dialog.setContentPane(writer.getScrollPane());
    GeneiousGUI.scale(dialog, .5, .4, 600, 400);
    dialog.pack();
    dialog.setLocationRelativeTo(null);
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setVisible(true);
  }

}
