package nl.naturalis.geneious.util;

import java.util.List;
import java.util.Objects;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;

public class PreconditionValidator {

  public static final int BASIC = 0;
  public static final int ALL_DOCUMENTS_IN_SAME_DATABASE = 1;
  public static final int AT_LEAST_ONE_DOCUMENT_SELECTED = 2;

  private static final String encoding = System.getProperty("file.encoding", "").toUpperCase().replace("-", "");

  private final List<AnnotatedPluginDocument> selected;
  private final int preconditions;

  public PreconditionValidator(int preconditions) {
    this(null, preconditions);
  }

  public PreconditionValidator(List<AnnotatedPluginDocument> selected, int preconditions) {
    this.selected = selected;
    this.preconditions = preconditions;
  }

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
