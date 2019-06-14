package nl.naturalis.geneious.split;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.Annotator;
import nl.naturalis.geneious.name.StorableDocument;
import nl.naturalis.geneious.util.Messages;
import nl.naturalis.geneious.util.PreconditionValidator;

/**
 * Manages the Split Name operation.
 * 
 * @author Ayco Holleman
 *
 */
class NameSplitter extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NameSplitter.class);

  private final NameSplitterConfig cfg;

  public NameSplitter(NameSplitterConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws DatabaseServiceException, NonFatalException {
    int required = ALL_DOCUMENTS_IN_SAME_DATABASE;
    PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
    validator.validate();
    DocumentFilter filter = new DocumentFilter(cfg);
    List<StorableDocument> docs = filter.filterAndConvert();
    Annotator annotator = new Annotator(docs);
    List<StorableDocument> annotated = annotator.annotateDocuments();
    List<AnnotatedPluginDocument> all = new ArrayList<>(annotated.size());
    for (StorableDocument doc : annotated) {
      String name = doc.getSequenceInfo().getName();
      doc.getGeneiousDocument().setName(name);
      doc.saveAnnotations();
      all.add(doc.getGeneiousDocument());
    }
    if (!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }
    int selected = cfg.getSelectedDocuments().size();
    guiLogger.info("Number of selected documents ..........: %3d", selected);
    guiLogger.info("Number of documents passing filters ...: %3d", docs.size());
    guiLogger.info("Total number of documents annotated ...: %3d", annotated.size());
    guiLogger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
    Messages.operationCompletedSuccessfully(guiLogger, "Split Name");
    return all;
  }

}
