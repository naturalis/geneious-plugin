package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;

import nl.naturalis.geneious.ErrorCode;
import nl.naturalis.geneious.MessageProvider;
import nl.naturalis.geneious.NaturalisPluginWorker;
import nl.naturalis.geneious.StorableDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.name.Annotator;

public class NameSplitter extends NaturalisPluginWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NameSplitter.class);

  private final NameSplitterConfig cfg;

  public NameSplitter(NameSplitterConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected boolean performOperation() throws DatabaseServiceException {
    DocumentFilter filter = new DocumentFilter(cfg);
    List<StorableDocument> docs = filter.filterAndConvert();
    Annotator annotator = new Annotator(docs);
    List<StorableDocument> annotated = annotator.annotateDocuments();
    annotated.forEach(doc -> doc.saveAnnotations());
    int selected = cfg.getSelectedDocuments().size();
    guiLogger.info("Number of selected documents ..........: %3d", selected);
    guiLogger.info("Number of documents passing filters ...: %3d", docs.size());
    guiLogger.info("Total number of documents annotated ...: %3d", annotated.size());
    guiLogger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
    guiLogger.info(MessageProvider.get(ErrorCode.OPERATION_SUCCESS));
    return annotated.size() != 0;
  }

}
