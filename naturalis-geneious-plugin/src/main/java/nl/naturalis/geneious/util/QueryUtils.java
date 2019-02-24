package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.Condition;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

import jebl.util.ProgressListener;

import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_ID;

public class QueryUtils {

  public QueryUtils() {}

  public static List<AnnotatedPluginDocument> findByExtractID(Set<String> extractIDs) throws DatabaseServiceException {
    WritableDatabaseService svc = ServiceUtilities.getResultsDestination().getPrimaryDatabaseRoot();
    List<Query> queries = new ArrayList<>(extractIDs.size());
    DocumentField extractIdField = EXTRACT_ID.createQueryField();
    for (String id : extractIDs) {
      Query query = Query.Factory.createFieldQuery(extractIdField, Condition.EQUAL, id);
      queries.add(query);
    }
    Query[] queryArray = queries.toArray(new Query[queries.size()]);
    Query query = Query.Factory.createOrQuery(queryArray, Collections.emptyMap());
    List<AnnotatedPluginDocument> docs = svc.retrieve(query, ProgressListener.EMPTY);
    return docs;
  }

}
