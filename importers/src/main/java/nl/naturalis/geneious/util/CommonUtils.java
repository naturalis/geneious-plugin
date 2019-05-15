package nl.naturalis.geneious.util;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import java.util.Optional;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;
import com.google.common.base.Preconditions;

import nl.naturalis.common.base.NOptionals;
import nl.naturalis.geneious.gui.ShowDialog;

/**
 * Generic plugin utilities.
 */
public class CommonUtils {

  public static boolean checkTargetFolderNotNull() {
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
    Preconditions.checkArgument(isNotEmpty(docs), "At least one document required");
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
    Preconditions.checkArgument(isNotEmpty(docs), "At least one document required");
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
   * Returns the folder to be used for document queries, updates and inserts. If the user has selected any documents, the folder of the first
   * document is returned, else the folder selected by the user in the GUI. If no folder was selected either, an empty Optional is returned.
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
   * Returns the database (root folder) to be used for document queries, updates and inserts. If the user has selected any documents, the root
   * folder of the first document is returned, else the root folder of the folder selected by the user in the GUI. If no folder was selected
   * either, an empty Optional is returned.
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

  private CommonUtils() {}
}
