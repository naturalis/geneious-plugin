package nl.naturalis.geneious;

import java.util.List;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.Ping;

/**
 * Base class of all {@code SwingWorker} classes within the plugin.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class PluginSwingWorker extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(PluginSwingWorker.class);

  @Override
  protected Void doInBackground() {
    try {
      if (Ping.resume()) {
        List<AnnotatedPluginDocument> createdOrUpdated=performOperation();
        if (!createdOrUpdated.isEmpty()) {
          Ping.start(createdOrUpdated);
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
  protected abstract List<AnnotatedPluginDocument> performOperation() throws Exception;

}
