package nl.naturalis.geneious.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createFieldQuery;
import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createOrQuery;
import static com.biomatters.geneious.publicapi.documents.Condition.EQUAL;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;

/**
 * Methods for accessing the Geneious database.
 *
 * @author Ayco Holleman
 */
public class QueryUtils {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(QueryUtils.class);

  private static final DocumentField QF_EXTRACT_ID = SEQ_EXTRACT_ID.createQueryField();

  private QueryUtils() {}

  /**
   * Returns the database containing the user-selected folder.
   * 
   * @return
   */
  public static WritableDatabaseService getTargetDatabase() {
    if (ServiceUtilities.getResultsDestination() == null) {
      return null;
    }
    return ServiceUtilities.getResultsDestination().getPrimaryDatabaseRoot();
  }

  /**
   * Returns name of the database containing the user-selected folder.
   * 
   * @return
   */
  public static String getTargetDatabaseName() {
    if (getTargetDatabase() == null) {
      return "<no database selected>";
    }
    return getTargetDatabase().getFolderName();
  }

  /**
   * Return the documents containing the specified extract IDs.
   * 
   * @param extractIds
   * @return
   * @throws DatabaseServiceException
   */
  public static List<AnnotatedPluginDocument> findByExtractID(Collection<String> extractIds) throws DatabaseServiceException {
    if (extractIds.size() == 0) {
      return APDList.emptyList();
    }
    Query[] subqueries = extractIds.stream().map(id -> createFieldQuery(QF_EXTRACT_ID, EQUAL, id)).toArray(Query[]::new);
    Query query = createOrQuery(subqueries, Collections.emptyMap());
    guiLogger.debugf(() -> format("Executing query: %s", query));
    return getTargetDatabase().retrieve(query, ProgressListener.EMPTY);
  }

  /**
   * Deletes the specified documents. The documents may reside in multiple databases.
   * 
   * @param documents
   * @throws DatabaseServiceException
   */
  public static void deleteDocuments(Set<StoredDocument> documents) throws DatabaseServiceException {
    for (StoredDocument d : documents) {
      getTargetDatabase().removeDocument(d.getGeneiousDocument(), ProgressListener.EMPTY);
    }
  }

}
