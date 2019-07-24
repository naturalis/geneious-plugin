package nl.naturalis.geneious.name;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.util.QueryUtils.deleteDocuments;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.OperationConfig;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.Messages.Error;

/**
 * Manages the actual annotation process. It uses a {@link SequenceNameParser} to split the document names, queries the
 * database for dummies to get extra annotations from and uses a {@link VersionTracker} to assign document versions.
 *
 * @author Ayco Holleman
 */
public class Annotator {

  private static final GuiLogger logger = GuiLogManager.getLogger(Annotator.class);

  private final OperationConfig config;
  private final List<StorableDocument> docs;

  private Set<StoredDocument> obsoleteDummies;

  /**
   * Creates a new {@code Annotator} for the provided list of {@code StorableDocument} instances.
   * 
   * @param docs
   */
  public Annotator(OperationConfig config, List<StorableDocument> docs) {
    this.config = config;
    this.docs = docs;
  }

  /**
   * Creates the annotations and adds them to the Geneious documents. Returns a list of {@code StorableDocument} instances
   * that were successfully annotated. The new annotations have not yet been saved yet to the database yet, so you must
   * still call {@link StorableDocument#saveAnnotations(boolean) StorableDocument.saveAnnotations} afterwards.
   * 
   * @throws DatabaseServiceException
   */
  public List<StorableDocument> annotateDocuments() throws DatabaseServiceException {
    logger.info("Annotating documents");
    List<StorableDocument> documents = getAnnotatableDocuments();
    logger.debug(() -> "Collecting extract IDs");
    Set<String> ids = documents.stream().map(Annotator::getExtractId).collect(Collectors.toSet());
    logger.debugf(() -> format("Collected %s unique extract ID%s", ids.size(), plural(ids)));
    logger.debugf(() -> format("Searching database %s for matching documents", config.getTargetDatabaseName()));
    List<AnnotatedPluginDocument> result = findByExtractID(config.getTargetDatabase(), ids);
    logger.debugf(() -> format("Found %s matching document%s", result.size(), plural(result)));
    QueryCache queryCache = new QueryCache(result);
    obsoleteDummies = new TreeSet<>(StoredDocument.URN_COMPARATOR); // Guarantees we won't attempt to delete the same dummy twice
    for(StorableDocument doc : documents) {
      NaturalisNote note = doc.getSequenceInfo().getNaturalisNote();
      String extractId = getExtractId(doc);
      List<StoredDocument> dummies = queryCache.findDummy(extractId);
      if(dummies != null) {
        if(dummies.size() > 1) {
          Error.duplicateDummies(logger, doc, dummies);
        } else {
          logger.debugf(() -> format("Found dummy document matching %s. Copying annotations to %s document", extractId, getType(doc)));
          dummies.get(0).getNaturalisNote().mergeInto(note, DOCUMENT_VERSION);
          obsoleteDummies.add(dummies.get(0));
          logger.debug(() -> "Dummy document queued for deletion");
        }
      }
    }
    logger.info("Setting document versions");
    VersionTracker versioner = new VersionTracker(queryCache.getLatestDocumentVersions());
    documents.forEach(versioner::setDocumentVersion);
    logger.info("Attaching annotations");
    documents.forEach(StorableDocument::attachNaturalisNote);
    if(!obsoleteDummies.isEmpty()) {
      logger.info("Deleting %s obsolete dummy document%s", obsoleteDummies.size(), plural(obsoleteDummies));
      deleteDocuments(config.getTargetDatabase(), obsoleteDummies);
    }
    return documents;
  }

  private List<StorableDocument> getAnnotatableDocuments() {
    List<StorableDocument> annotatables = new ArrayList<>(docs.size());
    for(StorableDocument doc : docs) {
      try {
        logger.debugf(() -> format("Parsing \"%s\"", doc.getSequenceInfo().getName()));
        doc.getSequenceInfo().createNote();
        annotatables.add(doc);
      } catch(NotParsableException e) {
        String name;
        if(doc.getSequenceInfo().getImportedFrom() == null) { // A higher-level document, created from other documents
          name = doc.getSequenceInfo().getName();
        } else {
          name = doc.getSequenceInfo().getImportedFrom().getName();
        }
        logger.error("Error processing %s: %s", name, e.getMessage());
      }
    }
    return annotatables;
  }

  private static String getExtractId(StorableDocument doc) {
    return doc.getSequenceInfo().getNaturalisNote().getExtractId();
  }

  private static DocumentType getType(StorableDocument doc) {
    return doc.getSequenceInfo().getDocumentType();
  }
}
