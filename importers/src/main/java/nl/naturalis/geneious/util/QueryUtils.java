package nl.naturalis.geneious.util;

import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createFieldQuery;
import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.*;
import static com.biomatters.geneious.publicapi.documents.Condition.EQUAL;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.*;

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
import nl.naturalis.geneious.OperationConfig;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.smpl.DummySequence;

/**
 * Methods for querying the Geneious database.
 *
 * @author Ayco Holleman
 */
public class QueryUtils {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(QueryUtils.class);

  /**
   * The {@code DocumentField} to use for queries on the <i>Extract ID (Seq)</i> field
   */
  public static final DocumentField QF_SEQ_EXTRACT_ID = SEQ_EXTRACT_ID.createQueryField();
  /**
   * The {@code DocumentField} to use for queries on the <i>Extract ID (Samples)</i> field
   */
  public static final DocumentField QF_SMPL_EXTRACT_ID = SMPL_EXTRACT_ID.createQueryField();
  /**
   * The {@code DocumentField} to use for queries on the <i>Marker (Seq)</i> field
   */
  public static final DocumentField QF_SEQ_MARKER = SEQ_MARKER.createQueryField();

  private QueryUtils() {}

  /**
   * Returns the currently selected database (i&#46;e&#46; the root folder of the currently selected folder). May return null. <i><b>Do not
   * call this method when inside a {@link PluginSwingWorker}!</b></i> Since code inside a {@code PluginSwingWorker} runs in another thread
   * than the GUI's event-dispatch thread, the user may click on another folder while the operation is running. Use
   * {@link OperationConfig#getTargetDatabase()} when inside a {@code PluginSwingWorker}. That method will always return the database that
   * was selected at the time the operation began.
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
   * Returns the name of the currently selected database or "&lt;no database selected&gt;" if no database has been selected yet. <i><b>Do
   * not call this method when inside a {@code DocumentOperation}!</b></i>
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
   * Return all documents with the specified extract IDs.
   * 
   * @param database
   * @param extractIds
   * @return
   * @throws DatabaseServiceException
   */
  public static List<AnnotatedPluginDocument> findByExtractID(WritableDatabaseService database, Collection<String> extractIds)
      throws DatabaseServiceException {
    if (extractIds.size() == 0) {
      return Collections.emptyList();
    }
    Query[] constraints = new Query[extractIds.size() * 2];
    int i = 0;
    for (String id : extractIds) {
      constraints[i++] = createFieldQuery(QF_SEQ_EXTRACT_ID, EQUAL, id);
      constraints[i++] = createFieldQuery(QF_SMPL_EXTRACT_ID, EQUAL, id);
    }
    Query query = createOrQuery(constraints, Collections.emptyMap());
    guiLogger.debugf(() -> format("Executing query: %s", query));
    return database.retrieve(query, ProgressListener.EMPTY);
  }

  /**
   * Return all documents with the specified extract IDs.
   * 
   * @param database
   * @param extractIds
   * @return
   * @throws DatabaseServiceException
   */
  public static List<AnnotatedPluginDocument> findDummies(WritableDatabaseService database, Collection<String> extractIds)
      throws DatabaseServiceException {
    if (extractIds.size() == 0) {
      return Collections.emptyList();
    }
    Query[] constraints = new Query[extractIds.size() * 2];
    int i = 0;
    for (String id : extractIds) {
      constraints[i++] = createFieldQuery(QF_SEQ_EXTRACT_ID, EQUAL, id);
      constraints[i++] = createFieldQuery(QF_SMPL_EXTRACT_ID, EQUAL, id);
    }
    Query subquery = createOrQuery(constraints, Collections.emptyMap());
    Query dummiesOnly = createFieldQuery(QF_SEQ_MARKER, EQUAL, DummySequence.DUMMY_MARKER);
    Query query = createAndQuery(new Query[] {dummiesOnly, subquery}, Collections.emptyMap());
    guiLogger.debugf(() -> format("Executing query: %s", query));
    return database.retrieve(query, ProgressListener.EMPTY);
  }

  /**
   * Return all documents with the specified extract ID. Since this method exists to facility the {@link Ping} mechanism, it does not log
   * anything.
   * 
   * @param extractId
   * @return
   * @throws DatabaseServiceException
   */
  public static List<AnnotatedPluginDocument> findByExtractID(WritableDatabaseService database, String extractId)
      throws DatabaseServiceException {
    Query[] constraints = new Query[2];
    constraints[0] = createFieldQuery(QF_SEQ_EXTRACT_ID, EQUAL, extractId);
    constraints[1] = createFieldQuery(QF_SMPL_EXTRACT_ID, EQUAL, extractId);
    Query query = createOrQuery(constraints, Collections.emptyMap());
    return database.retrieve(query, ProgressListener.EMPTY);
  }

  /**
   * Deletes the specified documents. The documents may reside in multiple databases.
   * 
   * @param database
   * @param documents
   * @throws DatabaseServiceException
   */
  public static void deleteDocuments(WritableDatabaseService database, Set<StoredDocument> documents) throws DatabaseServiceException {
    for (StoredDocument d : documents) {
      database.removeDocument(d.getGeneiousDocument(), ProgressListener.EMPTY);
    }
  }

}
