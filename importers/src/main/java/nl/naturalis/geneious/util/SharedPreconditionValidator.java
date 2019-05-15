package nl.naturalis.geneious.util;

import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

import nl.naturalis.geneious.MessageProvider;
import nl.naturalis.geneious.MessageProvider.Message;

import static nl.naturalis.geneious.ErrorCode.BAD_CHARSET;
import static nl.naturalis.geneious.ErrorCode.BAD_DOCUMENT_DATABASE;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

public class SharedPreconditionValidator {

  private final List<AnnotatedPluginDocument> selected;

  public SharedPreconditionValidator(AnnotatedPluginDocument[] selected) {
    this.selected = Arrays.asList(selected);
  }

  public SharedPreconditionValidator(List<AnnotatedPluginDocument> selected) {
    this.selected = selected;
  }

  public Message validate() {
    Message message;
    if (null != (message = checkEncoding())) {
      return message;
    }
    if (null != (message = checkAllDocsWritableAndInSameDatabase())) {
      return message;
    }
    return MessageProvider.OK_MESSAGE;
  }

  private static Message checkEncoding() {
    String enc = System.getProperty("file.encoding", "").toLowerCase();
    if (!enc.equals("utf-8") && !enc.equals("utf8")) {
      return MessageProvider.messageFor(BAD_CHARSET, System.getProperty("file.encoding"));
    }
    return null;
  }

  private Message checkAllDocsWritableAndInSameDatabase() {
    WritableDatabaseService svc = ServiceUtilities.getResultsDestination();
    WritableDatabaseService root = svc.getPrimaryDatabaseRoot();
    for (AnnotatedPluginDocument doc : selected) {
      DatabaseService db = doc.getDatabase();
      if (db == null || !(db instanceof WritableDatabaseService)) {
        return MessageProvider.messageFor(BAD_DOCUMENT_DATABASE, getTargetDatabaseName());
      }
      WritableDatabaseService wdb = (WritableDatabaseService) db;
      if (!wdb.getPrimaryDatabaseRoot().equals(root)) {
        return MessageProvider.messageFor(BAD_DOCUMENT_DATABASE, getTargetDatabaseName());
      }
    }
    return null;
  }

}
