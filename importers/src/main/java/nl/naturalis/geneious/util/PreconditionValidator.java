package nl.naturalis.geneious.util;

import static nl.naturalis.geneious.util.PluginUtils.allDocumentsInSameDatabase;
import static nl.naturalis.geneious.util.PluginUtils.getPath;
import static nl.naturalis.geneious.util.PluginUtils.isPingFolder;
import java.util.Set;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.plugin.Geneious;
import com.biomatters.geneious.publicapi.plugin.Geneious.MajorVersion;
import nl.naturalis.geneious.OperationConfig;
import nl.naturalis.geneious.OperationOptions;
import nl.naturalis.geneious.Precondition;
import nl.naturalis.geneious.PreconditionException;

/**
 * Checks whether all preconditions for executing an operation are met and, if not, throws an {@link PreconditionException}. Note that the
 * preconditions checked here partly overlap with the validations done in the input dialog for an operation (see {@link OperationOptions}).
 * This is to make the code less dependent on what happens in the GUI. Another difference is that the {@code PreconditionValidator} is part
 * of the operation itself (it runs after the ping phase), so it allows for some last minute decisions on whether or not to abort.
 * 
 * @author Ayco Holleman
 *
 */
public class PreconditionValidator {

  private static final String jvmEncoding = System.getProperty("file.encoding", "").toUpperCase().replace("-", "");

  private final OperationConfig config;
  private final Set<Precondition> preconditions;

  /**
   * Creates a {@code PreconditionValidator} that checks the provided preconditions.
   * 
   * @param config The configuration used by the the operation
   * @param preconditions The preconditions to check
   */
  public PreconditionValidator(OperationConfig config, Set<Precondition> preconditions) {
    this.config = config;
    this.preconditions = preconditions;
  }

  /**
   * Executes the validations.
   * 
   * @throws PreconditionException
   */
  public void validate() throws PreconditionException {
    // Basic precondition checks:
    checkGeneiousVersion();
    checkJvmEncoding();
    checkTargetDatabase();
    for (Precondition p : preconditions) {
      switch (p) {
        case VALID_TARGET_FOLDER:
          checkValidTargetFolder();
          break;
        case AT_LEAST_ONE_DOCUMENT_SELECTED:
          if (config.getSelectedDocuments().isEmpty()) {
            smash("No documents selected");
          }
          break;
        case ALL_DOCUMENTS_IN_SAME_DATABASE:
          if (!config.getSelectedDocuments().isEmpty() && !allDocumentsInSameDatabase(config.getSelectedDocuments())) {
            smash("All selected documents must be in the same database");
          }
          break;
      }
    }
  }

  private static void checkGeneiousVersion() throws PreconditionException {
    MajorVersion version = Geneious.getMajorVersion();
    if (version.ordinal() < 15) {
      smash("You are running a Geneious version that is not supported by the Naturalis plugin: " + version
          + ". Minimum supported version is " + MajorVersion.Version2019_1);
    }
  }

  private static void checkJvmEncoding() throws PreconditionException {
    if (!jvmEncoding.equals("UTF8")) {
      smash("Unsupported character encoding: " + jvmEncoding);
    }
  }

  private void checkTargetDatabase() throws PreconditionException {
    if (config.getTargetDatabase() == null) {
      smash("Please select a database first");
    }
  }

  private void checkValidTargetFolder() throws PreconditionException {
    WritableDatabaseService folder = config.getTargetFolder();
    if (folder == null) {
      smash("Please select a target folder");
    } else if (isPingFolder(folder)) {
      smash("Illegal target folder: " + getPath(folder));
    }
  }

  private static void smash(String message, Object... msgArgs) throws PreconditionException {
    if (msgArgs.length == 0) {
      throw new PreconditionException(message);
    }
    throw new PreconditionException(String.format(message, msgArgs));
  }

}
