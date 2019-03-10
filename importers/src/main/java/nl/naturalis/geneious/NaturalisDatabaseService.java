package nl.naturalis.geneious;

import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.PartiallyWritableDatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.databaseservice.RetrieveCallback;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.plugin.Icons;

import nl.naturalis.geneious.util.QueryUtils;

public class NaturalisDatabaseService extends PartiallyWritableDatabaseService {

  public static final String UNIQUE_ID = "__naturalis_db_service__";

  @Override
  public boolean showInServiceTree() {
    return false;
  }

  @Override
  public String getName() { // We won't be visible, but OK
    return "Naturalis Database Service";
  }

  @Override
  public String getUniqueID() {
    return UNIQUE_ID;
  }

  @Override
  public String getDescription() {
    return "A database service allowing for bulk removal of documents";
  }

  @Override
  public String getHelp() {
    return "";
  }

  @Override
  public Icons getIcons() {
    return null;
  }

  @Override
  public QueryField[] getSearchFields() {
    return QueryUtils.getTargetDatabase().getSearchFields();
  }

  @Override
  public void retrieve(Query query, RetrieveCallback callback, URN[] urns) throws DatabaseServiceException {
    QueryUtils.getTargetDatabase().retrieve(query, callback, urns);
  }

  @Override
  public boolean canDeleteDocuments(List<AnnotatedPluginDocument> documents) {
    return true;
  }

}
