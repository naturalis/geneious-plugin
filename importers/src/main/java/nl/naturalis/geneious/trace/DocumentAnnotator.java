package nl.naturalis.geneious.trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.util.DocumentResultSetInspector;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

/**
 * Responsible for creating the Naturalis-specific annotations and adding them to Geneious documents.
 *
 * @author Ayco Holleman
 */
class DocumentAnnotator {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentAnnotator.class);

  private final List<ImportableDocument> docs;

  private int successCount;
  private int failureCount;

  DocumentAnnotator(List<ImportableDocument> docs) {
    this.docs = docs;
  }

  /**
   * Creates the annotations and adds them to the Geneious documents.
   * 
   * @throws DatabaseServiceException
   */
  void annotateImportedDocuments() throws DatabaseServiceException {
    guiLogger.info("Creating annotations");
    List<ImportableDocument> annotables = getAnnotatableDocuments();
    guiLogger.debug(() -> "Collecting extract IDs");
    Set<String> ids = annotables.stream()
        .map(d -> d.getSequenceInfo().getNaturalisNote().getExtractId())
        .collect(Collectors.toSet());
    guiLogger.debugf(() -> format("Searching database \"%s\" for older documents with the same extract IDs", getTargetDatabaseName()));
    List<AnnotatedPluginDocument> docs = findByExtractID(ids);
    guiLogger.debugf(() -> format("Found %s document(s)", docs.size()));
    DocumentResultSetInspector dm = new DocumentResultSetInspector(docs);
    for (ImportableDocument doc : annotables) {
      doc.annotate(dm);
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
   * Returns the number of documents which could not be annotated (most likely because the sequence name could not be parsed).
   * 
   * @return
   */
  int getFailureCount() {
    return failureCount;
  }

  private List<ImportableDocument> getAnnotatableDocuments() {
    successCount = failureCount = 0;
    List<ImportableDocument> annotatable = new ArrayList<>(docs.size());
    for (ImportableDocument doc : docs) {
      try {
        doc.getSequenceInfo().createNote();
        ++successCount;
        annotatable.add(doc);
      } catch (NotParsableException e) {
        ++failureCount;
        String file = doc.getSequenceInfo().getSourceFile().getName();
        guiLogger.error("Error processing %s: %s", file, e.getMessage());
      }
    }
    return annotatable;
  }

}
