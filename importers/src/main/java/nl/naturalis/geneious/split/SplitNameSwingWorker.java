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
import nl.naturalis.geneious.util.Messages.Info;
import nl.naturalis.geneious.util.PreconditionValidator;

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
    int required = ALL_DOCUMENTS_IN_SAME_DATABASE;
    PreconditionValidator validator = new PreconditionValidator(config.getSelectedDocuments(), required);
    validator.validate();
    DocumentFilter filter = new DocumentFilter(config);
    List<StorableDocument> docs = filter.filterAndConvert();
    Annotator annotator = new Annotator(config, docs);
    List<StorableDocument> annotated = annotator.annotateDocuments();
    List<AnnotatedPluginDocument> all = new ArrayList<>(annotated.size());
    for(StorableDocument doc : annotated) {
      String name = doc.getSequenceInfo().getName();
      doc.getGeneiousDocument().setName(name);
      doc.saveAnnotations();
      all.add(doc.getGeneiousDocument());
    }
    if(!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }
    int selected = config.getSelectedDocuments().size();
    logger.info("Number of selected documents ..........: %3d", selected);
    logger.info("Number of documents passing filters ...: %3d", docs.size());
    logger.info("Total number of documents annotated ...: %3d", annotated.size());
    logger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
    Info.operationCompletedSuccessfully(logger, SplitNameDocumentOperation.NAME);
    return all;
  }

  @Override
  protected String getLogTitle() {
    return SplitNameDocumentOperation.NAME;
  }

}
