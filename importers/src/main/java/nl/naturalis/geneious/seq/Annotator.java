package nl.naturalis.geneious.seq;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.util.StoredDocument;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.gui.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.QueryUtils.deleteDocuments;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

/**
 * Responsible for creating the Naturalis-specific annotations and adding them to Geneious documents.
 *
 * @author Ayco Holleman
 */
class Annotator {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(Annotator.class);

  private final List<ImportableDocument> docs;

  private int successCount;
  private int failureCount;

  private Set<StoredDocument> obsoleteDummies;

  Annotator(List<ImportableDocument> docs) {
    this.docs = docs;
  }

  /**
   * Creates the annotations and adds them to the Geneious documents.
   * 
   * @throws DatabaseServiceException
   */
  void annotateImportedDocuments() throws DatabaseServiceException {
    guiLogger.info("Parsing AB1 files names / fasta sequence headers");
    List<ImportableDocument> documents = getAnnotatableDocuments();
    guiLogger.debug(() -> "Collecting extract IDs");
    Set<String> ids = documents.stream().map(Annotator::getExtractId).collect(Collectors.toSet());
    guiLogger.debugf(() -> format("Collected %s unique extract ID%s", ids.size(), plural(ids)));
    guiLogger.debugf(() -> format("Searching database %s for matching documents", getTargetDatabaseName()));
    List<AnnotatedPluginDocument> queryResult = findByExtractID(ids);
    guiLogger.debugf(() -> format("Found %s matching document%s", queryResult.size(), plural(queryResult)));
    QueryCache queryCache = new QueryCache(queryResult);
    obsoleteDummies = new TreeSet<>(StoredDocument.URN_COMPARATOR); // Guarantees we won't attempt to delete the same dummy twice
    guiLogger.info("Annotating documents");
    for (ImportableDocument doc : documents) {
      NaturalisNote note = doc.getSequenceInfo().getNaturalisNote();
      String extractId = doc.getSequenceInfo().getNaturalisNote().getExtractId();
      guiLogger.debugf(() -> format("Scanning query cache for dummy document with extract ID %s", extractId));
      queryCache.findDummy(extractId).ifPresent(dummy -> {
        guiLogger.debugf(() -> format("Found. Copying annotations to %s document", getType(doc)));
        dummy.getNaturalisNote().copyTo(note);
        obsoleteDummies.add(dummy);
        guiLogger.debug(() -> "Dummy document queued for deletion");
      });
    }
    guiLogger.info("Versioning documents");
    VersionTracker versioner = new VersionTracker(queryCache);
    documents.forEach(versioner::setDocumentVersion);
    guiLogger.info("Saving annotations to database");
    documents.forEach(ImportableDocument::saveAnnotations);
    if (!obsoleteDummies.isEmpty()) {
      guiLogger.info("Deleting %s obsolete dummy document%s", obsoleteDummies.size(), plural(obsoleteDummies));
      deleteDocuments(obsoleteDummies);
    }
  }

  /**
   * Returns the number of successfully annotated documents.
   * 
   * @return
   */
  int getSuccessCount() {
    return successCount;
  }

  /**
   * Returns the number of documents which could not be annotated (most likely because the sequence name could not be
   * parsed).
   * 
   * @return
   */
  int getFailureCount() {
    return failureCount;
  }

  private List<ImportableDocument> getAnnotatableDocuments() {
    successCount = failureCount = 0;
    List<ImportableDocument> annotatables = new ArrayList<>(docs.size());
    for (ImportableDocument doc : docs) {
      try {
        doc.getSequenceInfo().createNote();
        ++successCount;
        annotatables.add(doc);
      } catch (NotParsableException e) {
        ++failureCount;
        String file = doc.getSequenceInfo().getSourceFile().getName();
        guiLogger.error("Error processing %s: %s", file, e.getMessage());
      }
    }
    return annotatables;
  }

  private static String getExtractId(ImportableDocument doc) {
    return doc.getSequenceInfo().getNaturalisNote().getExtractId();
  }


  private static DocumentType getType(ImportableDocument doc) {
    return doc.getSequenceInfo().getDocumentType();
  }
}
