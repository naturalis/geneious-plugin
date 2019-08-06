package nl.naturalis.geneious.name;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.OperationConfig;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Error;
import nl.naturalis.geneious.util.Messages.Info;

import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.util.QueryUtils.deleteDocuments;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractId;

/**
 * Manages the actual annotation process. It uses a {@link SequenceNameParser} to split the document names, queries the database for dummies
 * to get extra annotations from and uses a {@link VersionTracker} to assign document versions.
 *
 * @author Ayco Holleman
 */
public class Annotator {

  private static final GuiLogger logger = GuiLogManager.getLogger(Annotator.class);

  private final OperationConfig config;

  /**
   * Creates a new {@code Annotator} for the provided list of {@code StorableDocument} instances.
   * 
   * @param config
   */
  public Annotator(OperationConfig config) {
    this.config = config;
  }

  /**
   * Creates the annotations and adds them to the Geneious documents. Returns a list of {@code StorableDocument} instances that were
   * successfully annotated. The new annotations have not yet been saved yet to the database yet, so you must still call
   * {@link StorableDocument#saveAnnotations(boolean) StorableDocument.saveAnnotations} afterwards.
   * 
   * @param documents
   * @throws DatabaseServiceException
   * @throws NonFatalException
   */
  public List<StorableDocument> annotateDocuments(List<StorableDocument> documents) throws DatabaseServiceException, NonFatalException {
    Info.annotatingDocuments(logger);
    ArrayList<StorableDocument> docs = splitNames(documents);
    if (docs.isEmpty()) {
      return Collections.emptyList();
    }
    Debug.collectingExtractIds(logger);
    Set<String> ids = docs.stream().map(NameUtil::getExtractId).collect(Collectors.toSet());
    Debug.collectedExtractIds(logger, ids);
    Debug.searchingForDocuments(logger, config.getTargetDatabaseName());
    List<AnnotatedPluginDocument> result = findByExtractId(config.getTargetDatabase(), ids);
    Debug.foundDocuments(logger, result);
    QueryCache queryCache = new QueryCache(result);
    copyAnnotationsFromDummies(docs, queryCache);
    Info.versioningDocuments(logger, docs);
    VersionTracker versioner = new VersionTracker(queryCache.getLatestDocumentVersions());
    docs.forEach(sd -> {
      versioner.setDocumentVersion(sd);
      sd.attachNaturalisNote();
    });
    return docs;
  }

  private static ArrayList<StorableDocument> splitNames(List<StorableDocument> documents) {
    ArrayList<StorableDocument> docs = new ArrayList<>(documents.size());
    for (StorableDocument doc : documents) {
      try {
        Debug.splittingName(logger, doc.getSequenceInfo().getName());
        doc.getSequenceInfo().createNote();
        docs.add(doc);
      } catch (NotParsableException e) {
        Error.nameParsingFailed(logger, doc.getSequenceInfo().getName(), e);
      }
    }
    return docs;
  }

  private void copyAnnotationsFromDummies(List<StorableDocument> docs, QueryCache queryCache) throws DatabaseServiceException {
    Set<StoredDocument> obsoleteDummies = new TreeSet<>(StoredDocument.URN_COMPARATOR);
    int updated = 0;
    for (StorableDocument doc : docs) {
      String id = NameUtil.getExtractId(doc);
      List<StoredDocument> dummies = queryCache.findDummy(id);
      if (dummies != null) {
        if (dummies.size() > 1) {
          Error.duplicateDummies(logger, doc.getSequenceInfo().getName(), id, dummies);
        } else {
          Debug.foundDummyForExtractId(logger, id, getType(doc));
          NaturalisNote myNote = doc.getSequenceInfo().getNaturalisNote();
          NaturalisNote dummyNote = dummies.get(0).getNaturalisNote();
          if (dummyNote.mergeInto(myNote, DOCUMENT_VERSION)) {
            ++updated;
            if (obsoleteDummies.add(dummies.get(0))) {
              Debug.dummyQueuedForDeletion(logger, id);
            }
          } else {
            Debug.noNewValues(logger, doc.getSequenceInfo().getName(), "dummy document");
          }
        }
      }
    }
    if (updated != 0) {
      Info.documentsUpdatedFromDummies(logger, docs, obsoleteDummies);
      Info.deletingObsoleteDummies(logger, obsoleteDummies);
      deleteDocuments(config.getTargetDatabase(), obsoleteDummies);
    }
  }

  private static DocumentType getType(StorableDocument doc) {
    return doc.getSequenceInfo().getDocumentType();
  }
}
