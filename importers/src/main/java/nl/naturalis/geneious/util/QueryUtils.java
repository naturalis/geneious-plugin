package nl.naturalis.geneious.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.NaturalisDatabaseService;

import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createFieldQuery;
import static com.biomatters.geneious.publicapi.databaseservice.Query.Factory.createOrQuery;
import static com.biomatters.geneious.publicapi.documents.Condition.EQUAL;

import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;

public class QueryUtils {

  private static final DocumentField QF_EXTRACT_ID = SMPL_EXTRACT_ID.createQueryField();

  private QueryUtils() {}

  public static WritableDatabaseService getTargetDatabase() {
    return ServiceUtilities.getResultsDestination().getPrimaryDatabaseRoot();
  }

  public static String getTargetDatabaseName() {
    return getTargetDatabase().getFolderName();
  }

  public static List<AnnotatedPluginDocument> findByExtractID(Set<String> extractIds) throws DatabaseServiceException {
    if (extractIds.size() == 0) {
      return Collections.emptyList();
    }
    Query[] subqueries = extractIds.stream().map(id -> createFieldQuery(QF_EXTRACT_ID, EQUAL, id)).toArray(Query[]::new);
    Query query = createOrQuery(subqueries, Collections.emptyMap());
    return getTargetDatabase().retrieve(query, ProgressListener.EMPTY);
  }

  public static void deleteDocuments(Set<StoredDocument> dummies) throws DatabaseServiceException {
    //String tmpFolderName = getTmpFolderName();
    //WritableDatabaseService dummyFolder = getTargetDatabase().createChildFolder(tmpFolderName);
    for (StoredDocument d : dummies) {
      getTargetDatabase().removeDocument(d.getGeneiousDocument(), ProgressListener.EMPTY);
      //dummyFolder.moveDocument(d.getGeneiousDocument(), ProgressListener.EMPTY);
    }
    //getTargetDatabase().removeChildFolder(tmpFolderName);
  }

  private static NaturalisDatabaseService getNaturalisDbService() {
    GeneiousService svc = ServiceUtilities.getService(NaturalisDatabaseService.UNIQUE_ID);
    return (NaturalisDatabaseService) svc;
  }

  private static String getTmpFolderName() {
    return new StringBuilder(64)
        .append("dummies-")
        .append(System.currentTimeMillis())
        .append(new Random().nextInt(100))
        .toString();
  }

}
