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
import nl.naturalis.geneious.util.PreconditionValidator;

/**
 * Manages and coordinates the import of BOLD files into Geneious.
 * 
 * @author Ayco Holleman
 */
class BoldSwingWorker extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldSwingWorker.class);

  private final BoldImportConfig cfg;

  BoldSwingWorker(BoldImportConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws NonFatalException {
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    List<AnnotatedPluginDocument> selectedDocuments = cfg.getSelectedDocuments();
    PreconditionValidator validator = new PreconditionValidator(selectedDocuments, required);
    validator.validate();
    guiLogger.info("Loading BOLD file " + cfg.getFile().getPath());
    BoldNormalizer normalizer = new BoldNormalizer(cfg);
    RuntimeInfo runtime = new RuntimeInfo(normalizer.countRows());
    BoldImporter importer = new BoldImporter(cfg, runtime);
    List<String> markers = normalizer.getMarkers();
    MarkerMap markerMap = new MarkerMap(markers);
    DocumentLookupTable lookups = DocumentLookupTable.newInstance(selectedDocuments, markerMap);
    if(markers.isEmpty()) {
      guiLogger.debug("No marker-related data in BOLD file. Will only create specimen-related annotations");
      lookups = lookups.rebuildWithPartialKey();
      importer.importRows(normalizer.getRows(), lookups);
    } else {
      guiLogger.debugf(() -> format("Will use these BOLD-to-Naturalis marker mappings: %s", toJson(markerMap)));
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
    stats.print(guiLogger);
    return updated;
  }

  @Override
  protected String getLogTitle() {
    return "BOLD Import";
  }

}
