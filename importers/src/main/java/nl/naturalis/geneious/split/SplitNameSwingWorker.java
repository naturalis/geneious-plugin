package nl.naturalis.geneious.split;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
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
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Error;
import nl.naturalis.geneious.util.Messages.Info;

import static java.util.stream.Collectors.toList;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;

import static nl.naturalis.geneious.Precondition.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.util.QueryUtils.deleteDocuments;
import static nl.naturalis.geneious.util.QueryUtils.findDummies;

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
    List<StoredDocument> updated = splitName(docs);
    List<AnnotatedPluginDocument> all = null;
    if (!updated.isEmpty()) {
      updated.forEach(StoredDocument::saveAnnotations);
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

  private List<StoredDocument> splitName(List<StoredDocument> documents) throws DatabaseServiceException {
    Iterator<StoredDocument> iterator = documents.iterator();
    Set<String> ids = new HashSet<String>(documents.size(), 1F);
    while (iterator.hasNext()) {
      StoredDocument doc = iterator.next();
      String name = NameUtil.removeKnownSuffixes(doc.getName());
      doc.getGeneiousDocument().setName(name);
      SequenceNameParser parser = new SequenceNameParser(name);
      NaturalisNote note;
      try {
        note = parser.parseName();
      } catch (NotParsableException e) {
        Error.nameParsingFailed(logger, doc.getName(), e);
        continue;
      }
      if (note.copyTo(doc.getNaturalisNote())) {
        ids.add(note.getExtractId());
      } else {
        Debug.noNewValues(logger, doc.getName(), "name parts");
        iterator.remove();
      }
    }
    List<AnnotatedPluginDocument> result = findDummies(config.getTargetDatabase(), ids);
    QueryCache queryCache = new QueryCache(result);
    Set<StoredDocument> obsoleteDummies = new TreeSet<>(StoredDocument.URN_COMPARATOR);
    for (StoredDocument doc : documents) {
      String id = doc.getNaturalisNote().getExtractId();
      List<StoredDocument> dummies = queryCache.findDummy(id);
      if (dummies != null) {
        if (dummies.size() > 1) {
          Error.duplicateDummies(logger, doc.getName(), id, dummies);
        } else {
          Debug.foundDummyForExtractId(logger, id, doc.getType());
          dummies.get(0).getNaturalisNote().mergeInto(doc.getNaturalisNote(), DOCUMENT_VERSION);
          if (obsoleteDummies.add(dummies.get(0))) {
            Debug.dummyQueuedForDeletion(logger, id);
          }
        }
      }
    }
    if (!obsoleteDummies.isEmpty()) {
      Info.deletingObsoleteDummies(logger, obsoleteDummies);
      deleteDocuments(config.getTargetDatabase(), obsoleteDummies);
    }
    return documents;
  }

}
