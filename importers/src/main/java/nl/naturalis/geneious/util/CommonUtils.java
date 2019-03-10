package nl.naturalis.geneious.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.databaseservice.RetrieveCallback;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;
import com.biomatters.geneious.publicapi.plugin.Icons;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;
import com.google.common.base.Preconditions;

import nl.naturalis.common.base.NOptionals;
import nl.naturalis.geneious.gui.ShowDialog;

/**
 * Generic plugin utilities.
 */
public class CommonUtils {

  /*
   * Dummy database service assigned to AnnotatedPluginDocument instances whose getDatabase() method returns null.
   */
  private static final DatabaseService NULL_DB = new DatabaseService() {
    @Override
    public String getUniqueID() {
      return "null_db";
    }

    @Override
    public String getName() {
      return "null_db";
    }

    @Override
    public Icons getIcons() {
      return null;
    }

    @Override
    public String getHelp() {
      return null;
    }

    @Override
    public String getDescription() {
      return null;
    }

    @Override
    public void retrieve(Query arg0, RetrieveCallback arg1, URN[] arg2) throws DatabaseServiceException {}

    @Override
    public QueryField[] getSearchFields() {
      return null;
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj;
    }

    @Override
    public int hashCode() {
      return System.identityHashCode(this);
    }
  };

  public static boolean checkTargetFolder() {
    if (ServiceUtilities.getResultsDestination() == null) {
      ShowDialog.pleaseSelectTargetFolder();
      return false;
    }
    return true;
  }

  /**
   * Whether or not all of the selected documents are in exactly the same folder.
   * 
   * @param docs
   * @return
   */
  public static boolean allDocumentsWritableAndInSameFolder(AnnotatedPluginDocument[] docs) {
    Preconditions.checkArgument(notEmpty(docs), "At least one document required");
    String id = null;
    for (AnnotatedPluginDocument doc : docs) {
      DatabaseService db = doc.getDatabase();
      if (db == null || !(doc.getDatabase() instanceof WritableDatabaseService)) {
        ShowDialog.documentNotEditable();
        return false;
      } else if (id == null) {
        id = db.getUniqueID();
      } else if (!id.equals(db.getUniqueID())) {
        ShowDialog.documentsMustBeInSameFolder();
        return false;
      }
    }
    return true;
  }

  /**
   * Whether or not all of the selected documents share the same root folder (a&#46;k&#46;a&#46; database).
   * 
   * @param docs
   * @return
   */
  public static boolean allDocumentsWritableAndInSameDatabase(AnnotatedPluginDocument[] docs) {
    Preconditions.checkArgument(notEmpty(docs), "At least one document required");
    String id = null;
    for (AnnotatedPluginDocument doc : docs) {
      DatabaseService db = doc.getDatabase();
      if (db == null || !(db instanceof WritableDatabaseService)) {
        ShowDialog.documentNotEditable();
        return false;
      }
      WritableDatabaseService root = ((WritableDatabaseService) db).getPrimaryDatabaseRoot();
      if (id == null) {
        id = root.getUniqueID();
      } else if (!id.equals(root.getUniqueID())) {
        ShowDialog.documentsMustBeInSameDatabase();
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the folder to be used for document queries, updates and inserts. If the user has selected any documents, the folder of the
   * first document is returned, else the folder selected by the user in the GUI. If no folder was selected either, an empty Optional is
   * returned.
   * 
   * @param docs
   * @return
   */
  public static Optional<WritableDatabaseService> getFolder(AnnotatedPluginDocument[] docs) {
    if (isEmpty(docs)) {
      GeneiousService svc = ServiceUtilities.getSelectedService();
      if (svc == null || !(svc instanceof WritableDatabaseService)) {
        return Optional.empty();
      }
      WritableDatabaseService wdb = ((WritableDatabaseService) svc).getPrimaryDatabaseRoot();
      return Optional.of(wdb);
    }
    DatabaseService db = docs[0].getDatabase();
    if (db == null || !(db instanceof WritableDatabaseService)) {
      return Optional.empty();
    }
    return NOptionals.cast(db);
  }

  /**
   * Returns the database (root folder) to be used for document queries, updates and inserts. If the user has selected any documents, the
   * root folder of the first document is returned, else the root folder of the folder selected by the user in the GUI. If no folder was
   * selected either, an empty Optional is returned.
   * 
   * @param docs
   * @return
   */
  public static Optional<WritableDatabaseService> getDatabase(AnnotatedPluginDocument[] docs) {
    if (isEmpty(docs)) {
      GeneiousService svc = ServiceUtilities.getSelectedService();
      if (svc == null || !(svc instanceof WritableDatabaseService)) {
        return Optional.empty();
      }
      return Optional.of((WritableDatabaseService) svc);
    }
    DatabaseService db = docs[0].getDatabase();
    if (db == null || !(db instanceof WritableDatabaseService)) {
      return Optional.empty();
    }
    WritableDatabaseService wdb = ((WritableDatabaseService) db).getPrimaryDatabaseRoot();
    return Optional.of(wdb);
  }

  /**
   * Partitions documents according to the folder they are in.
   * 
   * @param docs
   * @return
   */
  public static Map<DatabaseService, List<AnnotatedPluginDocument>> partitionByFolder(AnnotatedPluginDocument[] docs) {
    Map<DatabaseService, List<AnnotatedPluginDocument>> map = new HashMap<>(4);
    for (AnnotatedPluginDocument doc : docs) {
      DatabaseService db = doc.getDatabase() == null ? NULL_DB : doc.getDatabase();
      List<AnnotatedPluginDocument> partition = map.getOrDefault(db, new ArrayList<>());
      partition.add(doc);
      map.putIfAbsent(db, partition);
    }
    return map;
  }

  /**
   * Partitions documents according to the database (root folder) they are in.
   * 
   * @param docs
   * @return
   */
  public static Map<DatabaseService, List<AnnotatedPluginDocument>> partitionByDatabase(AnnotatedPluginDocument[] docs) {
    Map<DatabaseService, List<AnnotatedPluginDocument>> map = new HashMap<>(4);
    for (AnnotatedPluginDocument doc : docs) {
      DatabaseService db = doc.getDatabase();
      if (db == null || !(db instanceof WritableDatabaseService)) {
        db = NULL_DB;
      } else {
        db = ((WritableDatabaseService) db).getPrimaryDatabaseRoot();
      }
      List<AnnotatedPluginDocument> partition = map.getOrDefault(db, new ArrayList<>());
      partition.add(doc);
      map.putIfAbsent(db, partition);
    }
    return map;
  }

  /**
   * Whether or not the specified array is null or has zero elements.
   * 
   * @param array
   * @return
   */
  public static boolean isEmpty(Object[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Whether or not the specified array is not null and contains at least one element.
   * 
   * @param array
   * @return
   */
  public static boolean notEmpty(Object[] array) {
    return array != null && array.length != 0;
  }

  private CommonUtils() {}
}
