package nl.naturalis.geneious.split;

import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;

import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;

import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.StorableDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.name.Annotator;
import nl.naturalis.geneious.name.NameUtil;
import nl.naturalis.geneious.util.PreconditionValidator;

public class NameSplitter extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NameSplitter.class);

  private final NameSplitterConfig cfg;

  public NameSplitter(NameSplitterConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected boolean performOperation() throws DatabaseServiceException, NonFatalException {
    int required = ALL_DOCUMENTS_IN_SAME_DATABASE;
    PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
    validator.validate();
    DocumentFilter filter = new DocumentFilter(cfg);
    List<StorableDocument> docs = filter.filterAndConvert();
    Annotator annotator = new Annotator(docs);
    List<StorableDocument> annotated = annotator.annotateDocuments();
    annotated.forEach(doc -> {
      String name = doc.getSequenceInfo().getName() + NameUtil.getDefaultSuffix(doc);
      doc.getGeneiousDocument().setName(name);
      doc.saveAnnotationsAndMakeUnread();
      doc.save();
    });
    int selected = cfg.getSelectedDocuments().size();
    guiLogger.info("Number of selected documents ..........: %3d", selected);
    guiLogger.info("Number of documents passing filters ...: %3d", docs.size());
    guiLogger.info("Total number of documents annotated ...: %3d", annotated.size());
    guiLogger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
    guiLogger.info("Operation completed successfully");
    return annotated.size() != 0;
  }

}
