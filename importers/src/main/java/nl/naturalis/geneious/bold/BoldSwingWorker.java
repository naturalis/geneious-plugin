package nl.naturalis.geneious.bold;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.stream.Collectors.toList;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.util.JsonUtil.toJson;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;

import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.CsvImportStats;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.Log.Info;
import nl.naturalis.geneious.util.PreconditionValidator;

/**
 * Manages and coordinates the import of BOLD files into Geneious.
 * 
 * @author Ayco Holleman
 */
class BoldSwingWorker extends PluginSwingWorker<BoldImportConfig> {

  static final String FILE_DESCRIPTION = "BOLD file";
  static final String KEY_NAME = "registration number";

  private static final GuiLogger logger = GuiLogManager.getLogger(BoldSwingWorker.class);

  BoldSwingWorker(BoldImportConfig config) {
    super(config);
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws NonFatalException {
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    PreconditionValidator validator = new PreconditionValidator(config, required);
    validator.validate();
    Info.loadingFile(logger, FILE_DESCRIPTION, config);
    BoldNormalizer normalizer = new BoldNormalizer(config);
    RuntimeInfo runtime = new RuntimeInfo(normalizer.countRows());
    BoldImporter importer = new BoldImporter(config, runtime);
    List<String> markers = normalizer.getMarkers();
    MarkerMap markerMap = new MarkerMap(markers);
    BoldLookupTable lookups = BoldLookupTable.newInstance(selectedDocuments, markerMap);
    if(markers.isEmpty()) {
      logger.debug("No marker columns BOLD file");
      lookups = lookups.rebuildWithPartialKey();
      importer.importRows(normalizer.getRows(), lookups);
    } else {
      logger.debugf(() -> format("Will use these Naturalis-to-Bold marker mappings: %s", toJson(markerMap)));
      for(String marker : normalizer.getRowsPerMarker().keySet()) {
        List<String[]> rows = normalizer.getRowsPerMarker().get(marker);
        importer.importRows(rows, marker, lookups);
      }
      if(!lookups.isEmpty()) {
        lookups = lookups.rebuildWithPartialKey();
        importer.importRows(normalizer.getRows(), lookups);
      }
    }
    List<AnnotatedPluginDocument> updated = null;
    if(runtime.countUpdatedDocuments() != 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      updated = runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).collect(toList());
      updated = addAndReturnGeneratedDocuments(updated, true, Collections.emptyList());
    }
    CsvImportStats stats = new CsvImportStats(selectedDocuments, runtime);
    stats.print(logger);
    logger.info("UNUSED ROW (explanation): The row's registration number was not");
    logger.info("           found in any of the selected documents, but may still");
    logger.info("           be present in other, unselected documents");
    Info.operationCompletedSuccessfully(logger, BoldDocumentOperation.NAME);
    return updated == null ? Collections.emptyList() : updated;
  }

  @Override
  protected String getLogTitle() {
    return BoldDocumentOperation.NAME;
  }

}
