package nl.naturalis.geneious;

import java.util.List;
import java.util.Set;
import javax.swing.SwingWorker;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import nl.naturalis.geneious.crs.CrsDocumentOperation;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.log.LogSession;
import nl.naturalis.geneious.util.Ping;
import nl.naturalis.geneious.util.PreconditionValidator;

/**
 * Abstract base class for all {@code SwingWorker} classes within the plugin. These are the classes that manage and coordinate the number
 * crunching for the various operations provided by the plugin (AB1/Fasta Import, Split Name, etc.). They are not called directly by
 * Geneious. Geneious calls a {@code DocumentOperation} class (e.g. {@link CrsDocumentOperation}), which then kicks off a concrete subclass
 * of {@code PluginSwingWorker} class (e.g. {@code CrsImporter}). Although in practice the number crunching finishes quickly, we follow
 * Geneious's advice to do the number crunching on another thread than the GUI's event dispatch thread. The {@code PluginSwingWorker} class
 * itself only establishes a global control flow for all operations (the actual number crunching is left to the subclasses):
 * <ol>
 * <li>Start a log session.
 * <li>Check that, in the operation preceding the one currently executing, the user has patiently waited for all documents to get indexed.
 * If not, the {@link Ping ping mechanism} is resumed and the currently executing operation will not proceed until all documents have been
 * indexed after all.
 * <li>Verify that all preconditions for the operation have been met.
 * <li>Delegate to subclasses to carry out the main task (see {@link #performOperation() performOperation}).
 * <li>Ensure that any documents created or updated are indexed and set their status to "unread".
 * <li>Finally, if any exception was thrown out of the {@link #performOperation() performOperation} method, the {@code PluginSwingWorker}
 * class will catch it (rather than let Geneious "crash") and display the error message in the GUI log window.
 * </ol>
 * 
 * @author Ayco Holleman
 *
 */
public abstract class PluginSwingWorker<T extends OperationConfig> extends SwingWorker<Void, Void> {

  private static final GuiLogger logger = GuiLogManager.getLogger(PluginSwingWorker.class);

  protected final T config;

  private boolean finished = false;

  public PluginSwingWorker(T config) {
    this.config = config;
  }

  public boolean isFinished() {
    return finished;
  }

  /**
   * Implements the mechanism described above.
   */
  @Override
  protected Void doInBackground() {
    try (LogSession session = GuiLogManager.startSession(this, getLogTitle())) {
      if (config.getTargetDatabase() == null) { // Should never happen, but let's deal with it anyhow.
        logger.error("Please select a database first");
      } else {
        if (Ping.resume(config.getTargetDatabase())) {
          PreconditionValidator validator = new PreconditionValidator(config, getPreconditions());
          validator.validate();
          List<AnnotatedPluginDocument> createdOrUpdated = performOperation();
          if (!createdOrUpdated.isEmpty()) {
            try {
              Ping.start(config.getTargetDatabase());
            } finally {
              createdOrUpdated.forEach(doc -> doc.setUnread(true));
            }
          }
        }
      }
    } catch (NonFatalException e) {
      logger.error(e.getMessage());
    } catch (Throwable t) {
      logger.fatal(t);
    }
    finished = true;
    return null;
  }

  /**
   * To be implemented by subclasses: the actual number crunching. Implementations must return a list of all documents that were created or
   * updated during the operation. If no documents were created or updated, an empty list must be returned.
   * 
   */
  protected abstract List<AnnotatedPluginDocument> performOperation() throws Exception;

  /**
   * To be implemented by subclasses: return the window title for the log window.
   * 
   * @return
   */
  protected abstract String getLogTitle();

  /**
   * To be implemented by subclasses: return all preconditions that must be met before the operation can continue.
   * 
   * @return
   */
  protected abstract Set<Precondition> getPreconditions();

}
