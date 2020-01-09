package nl.naturalis.geneious;

import static nl.naturalis.geneious.util.PluginUtils.allDocumentsInSameFolder;
import java.util.Collections;
import java.util.List;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.Options;
import nl.naturalis.geneious.util.Ping;
import nl.naturalis.geneious.util.PluginUtils;

/**
 * An object underpinning the user input dialog for a particular operation. This base class only serves to "freeze" the folder and documents
 * currently selected by the user. This is necessary because the operations run in another thread than the GUI's event-dispatch thread, so
 * the user could click on another folder while the operation is executing.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class OperationOptions<T extends OperationConfig> extends Options {

  private final List<AnnotatedPluginDocument> selectedDocuments;

  private WritableDatabaseService targetFolder;
  private WritableDatabaseService targetDatabase;

  /**
   * Creates a new {@code OperationOptions} object freezing the currently selected folder and documents.
   */
  public OperationOptions() {
    selectedDocuments = Collections.unmodifiableList(DocumentUtilities.getSelectedDocuments());
    targetFolder = PluginUtils.getSelectedFolder().orElseGet(() -> {
      return allDocumentsInSameFolder(selectedDocuments) ? (WritableDatabaseService) getSelectedDocuments().get(0).getDatabase() : null;
    });
    if (targetFolder != null) {
      targetDatabase = targetFolder.getPrimaryDatabaseRoot();
    }
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
   * Returns the currently selected folder or, if no folder was selected all selected documents (if any) are in the same folder, that
   * folder. For operations that do not create documents, it is not required that the user has selected a folder in the left panel of the
   * GUI, or that a target folder can be inferred from the selected documents. However, the {@link #getTargetDatabase() target database}
   * must always be known because the ping mechanism must know to which database to send the ping document.
   * 
   * @return
   */
  protected WritableDatabaseService getTargetFolder() {
    return targetFolder;
  }

  /**
   * Sets the target folder.
   * 
   * @param targetFolder
   */
  public void setTargetFolder(WritableDatabaseService targetFolder) {
    this.targetFolder = targetFolder;
  }

  /**
   * Returns the database that must be used for lookup queries and for the {@link Ping ping mechanism}. The target folder, if applicable,
   * <i>must</i> always reside within the target database.
   * 
   * @return
   */
  public WritableDatabaseService getTargetDatabase() {
    return targetDatabase;
  }

  /**
   * Sets the target database.
   * 
   * @param targetDatabase
   */
  public void setTargetDatabase(WritableDatabaseService targetDatabase) {
    this.targetDatabase = targetDatabase;
  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if (msg != null) {
      return msg;
    }
    if (targetDatabase == null) {
      return "Please select a database first";
    }
    if (targetFolder != null && !targetFolder.getPrimaryDatabaseRoot().equals(targetDatabase)) {
      return "Target folder must be in the selected database";
    }
    return null;
  }

  /**
   * Generates an implementation-specific configuration object to be passed on the {@link PluginSwingWorker} classes.
   * 
   * @return
   */
  protected abstract T configureOperation();

  /**
   * A helper method that can be used by subclasses when generating the configuration object. Subclasses <b>must</b> make sure that all
   * configuration settings captured by classes higher up in the class hierarchy are set as well on the configuration object.
   * 
   * @param config
   * @return
   */
  protected T configureDefaults(T config) {
    config.setSelectedDocuments(selectedDocuments);
    config.setTargetFolder(targetFolder);
    config.setTargetDatabase(targetDatabase);
    return config;
  }

}
