package nl.naturalis.geneious;

import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

/**
 * Abstract base class for {@code Options} objects underpinning the user input dialogs for the various operations
 * provided by the plugin. Mainly servers to "freeze" the selected target folder and the selected documents. This is
 * necessary because the operations run in another thread than the GUI's event-dispatch thread, so the user could click
 * on another folder while the operation is running.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class OperationOptions<T extends OperationConfig> extends Options {

  private final List<AnnotatedPluginDocument> selectedDocuments;
  private final WritableDatabaseService targetFolder;

  public OperationOptions() {
    selectedDocuments = Collections.unmodifiableList(DocumentUtilities.getSelectedDocuments());
    targetFolder = ServiceUtilities.getResultsDestination();
  }

  public List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  public WritableDatabaseService getTargetFolder() {
    return targetFolder;
  }

  public abstract T configureOperation();

  protected T configureDefaults(T config) {
    config.setSelectedDocuments(selectedDocuments);
    config.setTargetFolder(targetFolder);
    return config;
  }

}
