package nl.naturalis.geneious.csv;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.log.GuiLogger;

public class CsvImportStats {

  private final List<AnnotatedPluginDocument> docs;
  private final RuntimeInfo runtime;

  public CsvImportStats(List<AnnotatedPluginDocument> selectedDocuments, RuntimeInfo runtime) {
    this.docs = selectedDocuments;
    this.runtime = runtime;
  }

  public void print(GuiLogger logger) {
    logger.info("Number of valid rows ............: %3d", runtime.countGoodRows());
    logger.info("Number of empty/bad rows ........: %3d", runtime.countBadRows());
    logger.info("Number of unused rows ...........: %3d", runtime.countUnusedRows());
    int x = docs.size();
    int y = runtime.countUpdatedDocuments();
    logger.info("Number of selected documents ....: %3d", x);
    logger.info("Number of updated documents .....: %3d", y);
    logger.info("Number of unchanged documents ...: %3d", x - y);
  }

}
