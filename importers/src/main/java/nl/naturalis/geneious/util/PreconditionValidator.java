package nl.naturalis.geneious.util;

import java.util.List;
import java.util.Objects;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

import nl.naturalis.geneious.NonFatalException;

/**
 * Checks whether the preconditions for an operation (e&#34;g&#34; a BOLD import) are satisfied. There are a few
 * preconditions that need to be checked no matter the operation. Individual operations can request additional checks.
 * 
 * @author Ayco Holleman
 *
 */
public class PreconditionValidator {

  /**
   * Only do basic checks.
   */
  public static final int BASIC = 0;
  /**
   * Make sure all selected documents are in the same database.
   */
  public static final int ALL_DOCUMENTS_IN_SAME_DATABASE = 1;
  /**
   * Make sure the user has selected at least one document.
   */
  public static final int AT_LEAST_ONE_DOCUMENT_SELECTED = 2;
  /**
   * Make sure the user is allowed to use the selected folder as a destination.
   */
  public static final int VALID_TARGET_FOLDER = 4;

  private static final String encoding = System.getProperty("file.encoding", "").toUpperCase().replace("-", "");

  private final List<AnnotatedPluginDocument> selected;
  private final int preconditions;

  /**
   * Create a {@code PreconditionValidator} that checks the provided preconditions. The preconditions are specified using
   * a bitwise OR. E.g. {@link #ALL_DOCUMENTS_IN_SAME_DATABASE} | {@link PreconditionValidator#VALID_TARGET_FOLDER}. If
   * you only want to check operation-independent preconditions, specify {@link #BASIC}.
   * 
   * @param preconditions
   */
  public PreconditionValidator(int preconditions) {
    this(null, preconditions);
  }

  /**
   * Create a {@code PreconditionValidator} for the provided document selection and preconditions.
   * 
   * @param selected
   * @param preconditions
   */
  public PreconditionValidator(List<AnnotatedPluginDocument> selected, int preconditions) {
    this.selected = selected;
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
    if (requires(AT_LEAST_ONE_DOCUMENT_SELECTED) && selectedNotNull() && selected.isEmpty()) {
      smash("No documents selected");
    }
    if (requires(ALL_DOCUMENTS_IN_SAME_DATABASE)) {
      checkAllDocsInSameDatabase();
    }
    if (requires(VALID_TARGET_FOLDER)) {
      checkValidTargetFolder();
    }
  }

  private static void checkEncoding() throws NonFatalException {
    if (!encoding.equals("UTF8")) {
      smash("Unsupported character encoding: " + encoding);
    }
  }

  private static void checkTargetDatabase() throws NonFatalException {
    if (QueryUtils.getTargetDatabase() == null) {
      smash("No database (folder) selected");
    }
  }

  private static void checkValidTargetFolder() throws NonFatalException {
    WritableDatabaseService svc = ServiceUtilities.getResultsDestination();
    if (svc == null) { // Geneious will prevent this, but let's handle it anyhow.
      smash("Please select a target folder");
    }
    do {
      if (svc.getFolderName().equals(PingSequence.PING_FOLER)) {
        smash("Illegal target folder: " + svc.getName());
      }
      if (svc.getParentService() instanceof WritableDatabaseService) {
        svc = (WritableDatabaseService) svc.getParentService();
      } else {
        break;
      }
    } while (svc != null);
  }

  private void checkAllDocsInSameDatabase() throws NonFatalException {
    for (AnnotatedPluginDocument doc : selected) {
      DatabaseService db = doc.getDatabase();
      if (db == null /* huh? */ || !(db instanceof WritableDatabaseService)) {
        smash(String.format("Document %s: database unknown or read-only"));
      }
      WritableDatabaseService wdb = ((WritableDatabaseService) db).getPrimaryDatabaseRoot();
      if (!wdb.equals(QueryUtils.getTargetDatabase())) {
        smash("All selected documents must be in the selected database");
      }
    }
  }

  private boolean requires(int precondition) {
    return (preconditions & precondition) == precondition;
  }

  private boolean selectedNotNull() { // Prevents programming errors rather than anything else
    Objects.requireNonNull(selected, "Can't check documents if you don't provide them");
    return true;
  }

  private static void smash(String message) throws NonFatalException {
    throw new NonFatalException(message);
  }

}
