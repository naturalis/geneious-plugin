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
import nl.naturalis.geneious.util.DocumentManager;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

class DocumentAnnotator {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentAnnotator.class);

  private final List<ImportedDocument> docs;

  DocumentAnnotator(List<ImportedDocument> docs) {
    this.docs = docs;
  }

  void annotateImportedDocuments() throws DatabaseServiceException {
    List<ImportedDocument> annotables = createDocumentNotes();
    guiLogger.debug(() -> "Collecting extract IDs from document notes");
    Set<String> ids = annotables.stream()
        .map(d -> d.getSequenceInfo().getNote().getExtractId())
        .collect(Collectors.toSet());
    DocumentManager dm = createDocumentManager(ids);
    for (ImportedDocument doc : annotables) {
      doc.annotate(dm);
    }
  }

  private List<ImportedDocument> createDocumentNotes() {
    guiLogger.debug(() -> "Creating document notes");
    List<ImportedDocument> annotatable = new ArrayList<>(docs.size());
    for (ImportedDocument doc : docs) {
      try {
        doc.getSequenceInfo().createNote();
        annotatable.add(doc);
      } catch (NotParsableException e) {
        String file = doc.getSequenceInfo().getSourceFile().getName();
        guiLogger.error("Error processing file %s: %s", file, e.getMessage());
      }
    }
    return annotatable;
  }

  private static DocumentManager createDocumentManager(Set<String> extractIDs) throws DatabaseServiceException {
    guiLogger.debugf(() -> format("Searching database \"%s\" for documents with the provided extract IDs", getTargetDatabaseName()));
    List<AnnotatedPluginDocument> docs = findByExtractID(extractIDs);
    guiLogger.debugf(() -> format("Found %s document(s)", docs.size()));
    return new DocumentManager(docs);
  }

}
