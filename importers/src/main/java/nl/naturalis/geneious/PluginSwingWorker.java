package nl.naturalis.geneious;

import java.util.List;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.crs.CrsDocumentOperation;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.log.LogSession;
import nl.naturalis.geneious.util.Ping;

/**
 * Abstract base class for all {@code SwingWorker} classes within the plugin. These are the classes that manage and
 * coordinate the number crunching for the various operations provided by the plugin (AB1/Fasta Import, Split Name,
 * etc.). They are not called directly by Geneious. Geneious calls a {@code DocumentOperation} class (e.g.
 * {@link CrsDocumentOperation}), which then kicks off a concrete subclass of {@code PluginSwingWorker} class (e.g.
 * {@code CrsImporter}). Although in practice the number crunching finishes quickly, we follow Geneious's advice to do
 * the number crunching on another thread than the GUI's event dispatch thread. The {@code PluginSwingWorker} class
 * itself only establishes a global control flow for all operations (the actual number crunching is left to the
 * subclasses):
 * <ol>
 * <li>Start a log session.
 * <li>Check that, in the operation preceding the one currently executing, the user waited for all documents to be
 * indexed. If not, the {@link Ping ping mechanism} is resumed and the currently executing operation will not proceed
 * until all documents are indexed after all.
 * <li>Delegate the operation-specific number crunching to subclasses by calling the abstract {@link #performOperation()
 * performOperation} method.
 * <li>If the number crunching resulting in any documents being created or updated, the {@code PluginSwingWorker} class
 * takes over again, and (again) kicks off the ping mechanism to ensure that the newly created/updated documents are
 * indexed.
 * <li>Also, if any documents were created or updated, their status will be set to "unread".
 * <li>Finally, if any exception was thrown out of the {@code performOperation} method, the {@code PluginSwingWorker}
 * class will catch it (rather than let Geneious "crash") and display the error message in the GUI log window.
 * </ol>
 * 
 * @author Ayco Holleman
 *
 */
public abstract class PluginSwingWorker<T extends OperationConfig> extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(PluginSwingWorker.class);

  protected final T config;

  public PluginSwingWorker(T config) {
    this.config = config;
  }

  /**
   * Implements the mechanism described above.
   */
  @Override
  protected Void doInBackground() {
    try(LogSession session = GuiLogManager.startSession(getLogTitle())) {
      if(Ping.resume(config.getTargetDatabase())) {
        List<AnnotatedPluginDocument> createdOrUpdated = performOperation();
        if(!createdOrUpdated.isEmpty()) {
          try {
            Ping.start(config.getTargetDatabase());
          } finally {
            createdOrUpdated.forEach(doc -> doc.setUnread(true));
          }
        }
      }
    } catch(NonFatalException e) {
      guiLogger.error(e.getMessage());
    } catch(Throwable t) {
      guiLogger.fatal(t);
    }
    return null;
  }

  /**
   * To be implemented by subclasses: the actual number crunching required for the operation they manage and coordinate.
   * Implementations must return a list of all documents that were created or updated during the operation. If no
   * documents were created or updated, an empty list must be returned.
   * 
   */
  protected abstract List<AnnotatedPluginDocument> performOperation() throws Exception;

  protected abstract String getLogTitle();

}
