package nl.naturalis.geneious.split;

import java.util.List;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;

import nl.naturalis.geneious.StorableDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.name.Annotator;
import nl.naturalis.geneious.util.Ping;

public class NameSplitter extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NameSplitter.class);

  private final NameSplitterConfig cfg;

  public NameSplitter(NameSplitterConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected Void doInBackground() {
    try {
      if (Ping.resume()) {
        if (splitNames()) {
          Ping.start();
        }
      }
    } catch (Throwable t) {
      guiLogger.fatal(t.getMessage(), t);
    }
    return null;
  }

  public boolean splitNames() throws DatabaseServiceException {
    DocumentFilter filter = new DocumentFilter(cfg);
    List<StorableDocument> docs = filter.filterAndConvert();
    Annotator annotator = new Annotator(docs);
    List<StorableDocument> annotated = annotator.annotateDocuments();
    annotated.forEach(doc -> doc.saveAnnotations(true));
    int selected = cfg.getSelectedDocuments().size();
    guiLogger.info("Number of selected documents ..........: %3d", selected);
    guiLogger.info("Number of documents passing filters ...: %3d", docs.size());
    guiLogger.info("Total number of documents annotated ...: %3d", annotated.size());
    guiLogger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
    return annotated.size() != 0;
  }

}
