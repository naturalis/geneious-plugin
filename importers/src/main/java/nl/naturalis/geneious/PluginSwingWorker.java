package nl.naturalis.geneious;

import java.util.List;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.Ping;

/**
 * Base class of all {@code SwingWorker} classes within the plugin. These classes manage and coordinate the
 * actual number crunching for the various operations provided by the plugin (e.g. AB1/Fasta Import, Split
 * Name, etc.). Although in practice they finish very quickly, we follow Geneious's advice to do the number
 * crunching on another thread than the GUI's event dispatch thread. The {@code PluginSwingWorker} class
 * itself establishes a global control flow for all operations:
 * <ol>
 * <li>Check that, in the operation preceding the one currently executing, the user waited for all documents
 * to be indexed. If not, the {@link Ping ping mechanism} is resumed and the currently executing operation
 * will not proceed until all documents are indexed after all.
 * <li>Delegate the operation-specific number crunching to the subclasses by calling the abstract
 * {@link #performOperation() performOperation} method.
 * <li>If the number crunching resulting in any documents being created or updated, the
 * {@code PluginSwingWorker} class takes over again, and (again) kicks off the ping mechanism to ensure that
 * the newly created/updated documents are indexed.
 * <li>Also, if any documents were created or updated, their status will be set to "unread".
 * <li>Finally, if any exception was thrown out of the {@code performOperation} method, the
 * {@code PluginSwingWorker} class will catch it (rather than let Geneious "crash") and display the error
 * message in the GUI log window.
 * </ol>
 * 
 * @author Ayco Holleman
 *
 */
public abstract class PluginSwingWorker extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(PluginSwingWorker.class);

  /**
   * Implements the mechanism described above.
   */
  @Override
  protected Void doInBackground() {
    try {
      if (Ping.resume()) {
        List<AnnotatedPluginDocument> createdOrUpdated = performOperation();
        if (!createdOrUpdated.isEmpty()) {
          try {
            Ping.start();
          } finally {
            createdOrUpdated.forEach(doc -> doc.setUnread(true));
          }
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
   * To be implemented by subclasses: the actual number crunching. Implementations must return a list of all
   * documents that were created or updated during the operation. If no document were created or updated, an
   * empty list must be returned.
   * 
   */
  protected abstract List<AnnotatedPluginDocument> performOperation() throws Exception;

}
