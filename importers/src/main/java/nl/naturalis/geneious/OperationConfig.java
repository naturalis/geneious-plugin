package nl.naturalis.geneious;

import java.util.List;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * Base class for all objects that capture user input for one of the plugin's operations. For each operation there is a separate subclass of
 * {@code OperationConfig}. {@code OperationConfig} objects are generated from {@link OperationOptions} objects and passed on (as
 * constructor arguments) to {@link PluginSwingWorker} objects. The purpose of this base class is to capture and "freeze" the currently
 * selected documents and the currently selected folder. Because the {@code PluginSwingWorker} objects execute in a separate thread, the
 * user could click around, and thereby change the target folder, while the operation is in progress.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class OperationConfig {

  private List<AnnotatedPluginDocument> selectedDocuments;
  private WritableDatabaseService targetFolder;

  public OperationConfig() {}

  /**
   * Returns the folder selected by the user just before the start of the operation.
   * 
   * @return
   */
  public WritableDatabaseService getTargetFolder() {
    return targetFolder;
  }

  /**
   * Sets the folder selected by the user.
   * 
   * @param targetFolder
   */
  public void setTargetFolder(WritableDatabaseService targetFolder) {
    this.targetFolder = targetFolder;
  }

  /**
   * Returns the database containing the target folder.
   * 
   * @return
   */
  public WritableDatabaseService getTargetDatabase() {
    return targetFolder == null ? null : targetFolder.getPrimaryDatabaseRoot();
  }

  /**
   * Returns name of the database that contains the folder that is currently selected by the user, or "&lt;no database selected&gt;" if no
   * folder has been selected yet.
   * 
   * @return
   */
  public String getTargetDatabaseName() {
    if (getTargetDatabase() == null) {
      return "<no database selected>";
    }
    return getTargetDatabase().getFolderName();
  }

  /**
   * Returns the documents selected by the user just before the start of the operation.
   * 
   * @return
   */
  public List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  /**
   * Sets the documents selected by the user.
   * 
   * @param selectedDocuments
   */
  public void setSelectedDocuments(List<AnnotatedPluginDocument> selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
  }

  /**
   * Returns the name of the operation configured by this configuration object. This is useful for code that only knows about the base class
   * (this class) but still wants to log an operation-specific message.
   * 
   * @return
   */
  public abstract String getOperationName();

}
