package nl.naturalis.geneious;

import javax.swing.SwingWorker;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.Ping;

/**
 * Base class of all {@code SwingWorker} classes within the plugin.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class NaturalisPluginWorker extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NaturalisPluginWorker.class);

  @Override
  protected Void doInBackground() {
    try {
      if (Ping.resume()) {
        if (performOperation()) {
          Ping.start();
        }
      }
    } catch (NonFatalException e) {
      guiLogger.error(e.getMessage());
    } catch (Throwable t) {
      guiLogger.fatal(t);
    }
    return null;
  }

  /**
   * To be implemented by subclasses: the actual number crunching. This method must return true if any documents were created or updated. This
   * will cause the {@link Ping} class to ping a test document until indexing is complete. If no document were created or updated, this method
   * should return false.
   * 
   * @return
   * @throws Exception
   */
  protected abstract boolean performOperation() throws Exception;

}
