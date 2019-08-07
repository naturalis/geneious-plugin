package nl.naturalis.geneious.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.Precondition;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.NameUtil;
import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.name.QueryCache;
import nl.naturalis.geneious.name.SequenceNameParser;
import nl.naturalis.geneious.name.VersionTracker;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Error;
import nl.naturalis.geneious.util.Messages.Info;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;

import static nl.naturalis.geneious.Precondition.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.util.QueryUtils.deleteDocuments;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractId;

/**
 * Manages and coordinates the Split Name operation.
 * 
 * @author Ayco Holleman
 */
class SplitNameSwingWorker extends PluginSwingWorker<SplitNameConfig> {

  private static final GuiLogger logger = GuiLogManager.getLogger(SplitNameSwingWorker.class);

  public SplitNameSwingWorker(SplitNameConfig config) {
    super(config);
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws DatabaseServiceException, NonFatalException {
    DocumentFilter filter = new DocumentFilter(config);
    List<StoredDocument> docs = filter.applyFilters();
    List<StoredDocument> updated = annotateDocuments(docs);
    List<AnnotatedPluginDocument> all = null;
    if (!updated.isEmpty()) {
      all = updated.stream().map(StoredDocument::getGeneiousDocument).collect(toList());
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }
    int selected = config.getSelectedDocuments().size();
    logger.info("Number of selected documents ..........: %3d", selected);
    logger.info("Number of documents passing filters ...: %3d", docs.size());
    logger.info("Total number of documents annotated ...: %3d", updated.size());
    logger.info("Total number of annotation failures ...: %3d", docs.size() - updated.size());
    Info.operationCompletedSuccessfully(logger, SplitNameDocumentOperation.NAME);
    return all == null ? Collections.emptyList() : all;
  }

  @Override
  protected String getLogTitle() {
    return SplitNameDocumentOperation.NAME;
  }

  @Override
  protected Set<Precondition> getPreconditions() {
    return EnumSet.of(ALL_DOCUMENTS_IN_SAME_DATABASE);
  }

  private List<StoredDocument> annotateDocuments(List<StoredDocument> documents) throws DatabaseServiceException, NonFatalException {
    Info.annotatingDocuments(logger);
    ArrayList<StoredDocument> docs = splitNames(documents);
    if (docs.isEmpty()) {
      return Collections.emptyList();
    }
    Debug.collectingExtractIds(logger);
    Set<String> ids = docs.stream().map(NameUtil::getExtractId).collect(toSet());
    Debug.collectedExtractIds(logger, ids);
    Debug.searchingForDocuments(logger, config.getTargetDatabaseName());
    List<AnnotatedPluginDocument> result = findByExtractId(config.getTargetDatabase(), ids);
    Debug.foundDocuments(logger, result);
    QueryCache queryCache = new QueryCache(result);
    copyAnnotationsFromDummies(docs, queryCache);
    Info.versioningDocuments(logger, docs);
    VersionTracker versioner = new VersionTracker(queryCache.getLatestDocumentVersions());
    docs.forEach(sd -> {
      if (sd.getNaturalisNote().getDocumentVersion() == null) {
        versioner.setDocumentVersion(sd);
      }
      sd.saveAnnotations();
    });
    return docs;
  }

  private static ArrayList<StoredDocument> splitNames(List<StoredDocument> documents) {
    ArrayList<StoredDocument> docs = new ArrayList<>(documents.size());
    for (StoredDocument doc : documents) {
      String name = NameUtil.removeKnownSuffixes(doc.getName());
      SequenceNameParser parser = new SequenceNameParser(name);
      NaturalisNote note;
      try {
        Debug.splittingName(logger, name);
        note = parser.parseName();
      } catch (NotParsableException e) {
        Error.nameParsingFailed(logger, doc.getName(), e);
        continue;
      }
      if (note.copyTo(doc.getNaturalisNote())) { // Possibly false if user decided to not ignoreDocsWithNaturalisNote
        docs.add(doc);
        if (!name.equals(doc.getName())) {
          // Only update document name if we also added/updated some annotations
          doc.getGeneiousDocument().setName(name);
        }
      } else {
        Debug.noNewValues(logger, doc.getName(), "name");
      }
    }
    return docs;
  }

  private void copyAnnotationsFromDummies(List<StoredDocument> docs, QueryCache queryCache) throws DatabaseServiceException {
    Set<StoredDocument> obsoleteDummies = new TreeSet<>(StoredDocument.URN_COMPARATOR);
    int updated = 0;
    for (StoredDocument doc : docs) {
      String id = NameUtil.getExtractId(doc);
      List<StoredDocument> dummies = queryCache.findDummy(id);
      if (dummies != null) {
        if (dummies.size() > 1) {
          Error.duplicateDummies(logger, doc.getName(), id, dummies);
        } else {
          Debug.foundDummyForExtractId(logger, id, doc.getType());
          NaturalisNote myNote = doc.getNaturalisNote();
          NaturalisNote dummyNote = dummies.get(0).getNaturalisNote();
          if (dummyNote.mergeInto(myNote, DOCUMENT_VERSION)) {
            ++updated;
            if (obsoleteDummies.add(dummies.get(0))) {
              Debug.dummyQueuedForDeletion(logger, id);
            }
          } else {
            Debug.noNewValues(logger, doc.getName(), "dummy document");
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

}
