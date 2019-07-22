package nl.naturalis.geneious.util;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.OperationConfig;
import nl.naturalis.geneious.Precondition;

/**
 * Checks whether all preconditions for executing an operation are met and, if not, throws an {@link NonFatalException}.
 * Note that the precondition checked here partly overlap with the validations done in the input dialog for an
 * operation. For example, see {@code SampleSheetImportOptions.verifyOptionsAreValid()}. This is to make the code less
 * dependent on what happens in the GUI.
 * 
 * @author Ayco Holleman
 *
 */
public class PreconditionValidator {

  private static final String encoding = System.getProperty("file.encoding", "").toUpperCase().replace("-", "");

  private final OperationConfig config;
  private final Set<Precondition> preconditions;

  /**
   * Creates a {@code PreconditionValidator} that checks the provided preconditions.
   * 
   * @param preconditions
   */
  public PreconditionValidator(OperationConfig config, Set<Precondition> preconditions) {
    this.config = config;
    this.preconditions = preconditions;
  }

  /**
   * Executes the validations.
   * 
   * @throws NonFatalException
   */
  public void validate() throws NonFatalException {
    // Basic precondition checks:
    checkEncoding();
    checkTargetDatabase();
    for(Precondition p : preconditions) {
      switch (p) {
        case VALID_TARGET_FOLDER:
          checkValidTargetFolder();
          break;
        case AT_LEAST_ONE_DOCUMENT_SELECTED:
          if(CollectionUtils.isEmpty(config.getSelectedDocuments())) {
            smash("No documents selected");
          }
          break;
        case ALL_DOCUMENTS_IN_SAME_DATABASE:
          checkAllDocsInSameDatabase();
          break;
      }
    }
  }

  private static void checkEncoding() throws NonFatalException {
    if(!encoding.equals("UTF8")) {
      smash("Unsupported character encoding: " + encoding);
    }
  }

  private void checkTargetDatabase() throws NonFatalException {
    if(config.getTargetDatabase() == null) {
      smash("No database (folder) selected");
    }
  }

  private void checkValidTargetFolder() throws NonFatalException {
    WritableDatabaseService svc = config.getTargetFolder();
    if(svc == null) { // Geneious will prevent this, but let's handle it anyhow.
      smash("Please select a target folder");
    }
    do {
      if(svc.getFolderName().equals(PingSequence.PING_FOLER)) {
        smash("Illegal target folder: " + svc.getName());
      }
      if(svc.getParentService() instanceof WritableDatabaseService) {
        svc = (WritableDatabaseService) svc.getParentService();
      } else {
        break;
      }
    } while(svc != null);
  }

  private void checkAllDocsInSameDatabase() throws NonFatalException {
    for(AnnotatedPluginDocument doc : config.getSelectedDocuments()) {
      DatabaseService db = doc.getDatabase();
      if(db == null /* huh? */ || !(db instanceof WritableDatabaseService)) {
        smash(String.format("Document %s: database unknown or read-only"));
      }
      WritableDatabaseService wdb = ((WritableDatabaseService) db).getPrimaryDatabaseRoot();
      if(!wdb.equals(config.getTargetDatabase())) {
        smash("All selected documents must be in the selected database");
      }
    }
  }

  private static void smash(String message) throws NonFatalException {
    throw new NonFatalException(message);
  }

}
