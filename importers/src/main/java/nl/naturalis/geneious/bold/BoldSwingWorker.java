package nl.naturalis.geneious.bold;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.Precondition;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.CsvImportStats;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.Messages.Info;

import static java.util.stream.Collectors.toList;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;

import static nl.naturalis.geneious.Precondition.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.Precondition.AT_LEAST_ONE_DOCUMENT_SELECTED;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.util.JsonUtil.*;

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
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    Info.loadingFile(logger, config);
    BoldNormalizer normalizer = new BoldNormalizer(config);
    RuntimeInfo runtime = new RuntimeInfo(normalizer.countRows());
    BoldImporter importer = new BoldImporter(config, runtime);
    List<String> markers = normalizer.getMarkers();
    MarkerMap markerMap = new MarkerMap(markers);
    BoldLookupTable lookups = BoldLookupTable.newInstance(selectedDocuments, markerMap);
    if(markers.isEmpty()) {
      lookups = lookups.rebuildWithPartialKey();
      importer.importRows(normalizer.getRows(), lookups);
    } else {
      logger.debugf(() -> format("Will use these Naturalis-to-BOLD marker mappings: %s", toPrettyJson(markerMap)));
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
    Info.explainUnusedRowForCrsAndBold(logger);
    Info.operationCompletedSuccessfully(logger, BoldDocumentOperation.NAME);
    return updated == null ? Collections.emptyList() : updated;
  }

  @Override
  protected String getLogTitle() {
    return BoldDocumentOperation.NAME;
  }

  @Override
  protected Set<Precondition> getPreconditions() {
    return EnumSet.of(AT_LEAST_ONE_DOCUMENT_SELECTED, ALL_DOCUMENTS_IN_SAME_DATABASE);
  }

}
