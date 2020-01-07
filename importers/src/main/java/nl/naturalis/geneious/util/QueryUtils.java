package nl.naturalis.geneious.util;

import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createFieldQuery;
import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createOrQuery;
import static com.biomatters.geneious.publicapi.documents.Condition.EQUAL;
import static nl.naturalis.common.CollectionMethods.sublist;
import static nl.naturalis.geneious.Settings.settings;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import jebl.util.ProgressListener;
import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.Messages.Error;

/**
 * Methods for querying the Geneious database.
 *
 * @author Ayco Holleman
 */
public class QueryUtils {

  private static final GuiLogger logger = GuiLogManager.getLogger(QueryUtils.class);

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
   * Return all documents with the specified extract IDs.
   * 
   * @param database
   * @param extractIds
   * @return
   * @throws DatabaseServiceException
   * @throws NonFatalException
   */
  public static List<AnnotatedPluginDocument> findByExtractId(WritableDatabaseService database, Collection<String> extractIds)
      throws NonFatalException {
    if (extractIds.size() == 0) {
      return Collections.emptyList();
    }
    ArrayList<String> ids;
    if (extractIds instanceof ArrayList) {
      ids = (ArrayList<String>) extractIds;
    } else {
      ids = new ArrayList<>(extractIds);
    }
    List<AnnotatedPluginDocument> result = new ArrayList<>(ids.size());
    // Divide by 2 b/c we're pumping 2 constraints per extract ID into the batch
    int sz = settings().getQuerySize() / 2;
    List<String> chunk;
    for (int x = 0; !(chunk = sublist(ids, x, sz)).isEmpty(); x += sz) {
      List<Query> constraints = new ArrayList<>(sz);
      for (String id : chunk) {
        constraints.add(createFieldQuery(QF_SEQ_EXTRACT_ID, EQUAL, id));
        constraints.add(createFieldQuery(QF_SMPL_EXTRACT_ID, EQUAL, id));
      }
      Query[] orClauses = constraints.toArray(new Query[constraints.size()]);
      Query orQuery = createOrQuery(orClauses, Collections.emptyMap());
      logger.debugf(() -> format("Executing query: %s", orQuery));
      try {
        result.addAll(database.retrieve(orQuery, ProgressListener.EMPTY));
      } catch (Exception e) {
        Error.queryError(logger, e);
        throw new NonFatalException("Operation aborted");
      }
    }
    return result;
  }

  /**
   * Return all documents with the specified extract ID. Since this method exists to facility the {@link Ping} mechanism, it does not log
   * anything.
   * 
   * @param extractId
   * @return
   * @throws DatabaseServiceException
   */
  public static List<AnnotatedPluginDocument> findByExtractId(WritableDatabaseService database, String extractId)
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
