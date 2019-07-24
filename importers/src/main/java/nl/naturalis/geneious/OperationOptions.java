package nl.naturalis.geneious;

import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

/**
 * An object underpinning the user input dialog for a particular operation. This base class only serves to "freeze" the
 * folder and documents currently selected by the user. This is necessary because the operations run in another thread
 * than the GUI's event-dispatch thread, so the user could click on another folder while the operation is executing.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class OperationOptions<T extends OperationConfig> extends Options {

  private final List<AnnotatedPluginDocument> selectedDocuments;
  private final WritableDatabaseService targetFolder;

  /**
   * Creates a new {@code OperationOptions} object freezing the currently selected folder and documents.
   */
  public OperationOptions() {
    selectedDocuments = Collections.unmodifiableList(DocumentUtilities.getSelectedDocuments());
    targetFolder = ServiceUtilities.getResultsDestination();
  }

  /**
   * Returns the currently selected documents.
   * 
   * @return
   */
  protected List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  /**
   * Returns the currently selected folder.
   * 
   * @return
   */
  protected WritableDatabaseService getTargetFolder() {
    return targetFolder;
  }

  /**
   * Generates an implementation-specific configuration object to be passed on the {@link PluginSwingWorker} classes.
   * 
   * @return
   */
  protected abstract T configureOperation();

  /**
   * A helper method that can be used by subclasses when generating the configuration object. Subclasses <b>must</b> make
   * sure that all configuration settings captured by classes higher up in the class hierarchy are set as well on the
   * configuration object.
   * 
   * @param config
   * @return
   */
  protected T configureDefaults(T config) {
    config.setSelectedDocuments(selectedDocuments);
    config.setTargetFolder(targetFolder);
    return config;
  }

}
