package nl.naturalis.geneious.trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.util.DocumentResultSetInspector;
import nl.naturalis.geneious.util.ImportedDocument;

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

  private Set<ImportedDocument> dummies;

  DocumentAnnotator(List<ImportableDocument> docs) {
    this.docs = docs;
  }

  /**
   * Creates the annotations and adds them to the Geneious documents.
   * 
   * @throws DatabaseServiceException
   */
  void annotateImportedDocuments() throws DatabaseServiceException {
    guiLogger.info("Annotating documents");
    List<ImportableDocument> annotables = getAnnotatableDocuments();
    guiLogger.debug(() -> "Collecting extract IDs");
    Set<String> ids = annotables.stream()
        .map(d -> d.getSequenceInfo().getNaturalisNote().getExtractId())
        .collect(Collectors.toSet());
    guiLogger.debugf(() -> format("Searching database \"%s\" for older documents with the same extract IDs", getTargetDatabaseName()));
    List<AnnotatedPluginDocument> docs = findByExtractID(ids);
    guiLogger.debugf(() -> format("Found %s document(s)", docs.size()));
    DocumentResultSetInspector inspector = new DocumentResultSetInspector(docs);
    Set<ImportedDocument> dummies = new TreeSet<>(ImportedDocument.URN_COMPARATOR);
    annotables.forEach(doc -> {
      ImportedDocument previousVersion = doc.annotate(inspector);
      if (previousVersion != null && previousVersion.isDummy()) {
        dummies.add(previousVersion);
      }
    });
    this.dummies = dummies;
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

  /**
   * Returns all dummy documents that were found and used for their annotations. They have served their purpose and should now be deleted.
   * 
   * @return
   */
  Set<ImportedDocument> getObsoleteDummyDocuments() {
    return dummies;
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

}
