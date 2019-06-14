package nl.naturalis.geneious.name;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.QueryUtils.deleteDocuments;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

/**
 * Responsible for creating annotations by parsing document names, and then adding the annotations to Geneious documents.
 *
 * @author Ayco Holleman
 */
public class Annotator {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(Annotator.class);

  private final List<StorableDocument> docs;

  private Set<StoredDocument> obsoleteDummies;

  /**
   * Creates a new {@code Annotator} for the provided list of {@code StorableDocument} instances.
   * 
   * @param docs
   */
  public Annotator(List<StorableDocument> docs) {
    this.docs = docs;
  }

  /**
   * Creates the annotations and adds them to the Geneious documents. Returns a new list of {@code StorableDocument} instances that were
   * successfully annotated. The new annotations have not yet been saved yet to the database yet, so you must still call
   * {@link StorableDocument#saveAnnotations(boolean) StorableDocument.saveAnnotations} afterwards.
   * 
   * @throws DatabaseServiceException
   */
  public List<StorableDocument> annotateDocuments() throws DatabaseServiceException {
    guiLogger.info("Creating annotations");
    List<StorableDocument> documents = getAnnotatableDocuments();
    guiLogger.debug(() -> "Collecting extract IDs");
    Set<String> ids = documents.stream().map(Annotator::getExtractId).collect(Collectors.toSet());
    guiLogger.debugf(() -> format("Collected %s unique extract ID%s", ids.size(), plural(ids)));
    guiLogger.debugf(() -> format("Searching database %s for matching documents", getTargetDatabaseName()));
    List<AnnotatedPluginDocument> queryResult = findByExtractID(ids);
    guiLogger.debugf(() -> format("Found %s matching document%s", queryResult.size(), plural(queryResult)));
    QueryCache queryCache = new QueryCache(queryResult);
    obsoleteDummies = new TreeSet<>(StoredDocument.URN_COMPARATOR); // Guarantees we won't attempt to delete the same dummy twice
    for (StorableDocument doc : documents) {
      NaturalisNote note = doc.getSequenceInfo().getNaturalisNote();
      String extractId = doc.getSequenceInfo().getNaturalisNote().getExtractId();
      queryCache.findDummy(extractId).ifPresent(dummy -> {
        guiLogger.debugf(
            () -> format("Found dummy document matching %s. Copying annotations to %s document", extractId, getType(doc)));
        dummy.getNaturalisNote().mergeInto(note);
        obsoleteDummies.add(dummy);
        guiLogger.debug(() -> "Dummy document queued for deletion");
      });
    }
    guiLogger.info("Setting document versions");
    VersionTracker versioner = new VersionTracker(queryCache.getLatestDocumentVersions());
    documents.forEach(versioner::setDocumentVersion);
    guiLogger.info("Attaching annotations");
    documents.forEach(StorableDocument::attachNaturalisNote);
    if (!obsoleteDummies.isEmpty()) {
      guiLogger.info("Deleting %s obsolete dummy document%s", obsoleteDummies.size(), plural(obsoleteDummies));
      deleteDocuments(obsoleteDummies);
    }
    return documents;
  }

  private List<StorableDocument> getAnnotatableDocuments() {
    List<StorableDocument> annotatables = new ArrayList<>(docs.size());
    for (StorableDocument doc : docs) {
      try {
        guiLogger.debugf(() -> format("Extracting annotations from name \"%s\"", doc.getSequenceInfo().getName()));
        doc.getSequenceInfo().createNote();
        annotatables.add(doc);
      } catch (NotParsableException e) {
        String file = doc.getSequenceInfo().getImportedFrom().getName();
        guiLogger.error("Error processing %s: %s", file, e.getMessage());
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
