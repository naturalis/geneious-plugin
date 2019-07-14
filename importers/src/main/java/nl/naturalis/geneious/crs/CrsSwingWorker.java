package nl.naturalis.geneious.crs;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.stream.Collectors.toList;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;

import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.CsvImportStats;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.Messages.Info;
import nl.naturalis.geneious.util.PreconditionValidator;
import nl.naturalis.geneious.util.StoredDocumentTable;

/**
 * Manages and coordinates the import of CRS files into Geneious.
 * 
 * @author Ayco Holleman
 */
class CrsSwingWorker extends PluginSwingWorker {

  private static final GuiLogger logger = GuiLogManager.getLogger(CrsSwingWorker.class);

  private final CrsImportConfig cfg;

  CrsSwingWorker(CrsImportConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws NonFatalException {
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    List<AnnotatedPluginDocument> selectedDocuments = cfg.getSelectedDocuments();
    PreconditionValidator validator = new PreconditionValidator(selectedDocuments, required);
    validator.validate();
    logger.info("Loading CRS file " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    int numRows = rows.size() - cfg.getSkipLines();
    logger.info("CRS file contains %s row%s (excluding header rows)", numRows, plural(numRows));
    RuntimeInfo runtime = new RuntimeInfo(numRows);
    CrsImporter importer = new CrsImporter(cfg, runtime);
    StoredDocumentTable<String> lookups = new StoredDocumentTable<>(selectedDocuments, this::getRegno);
    importer.importRows(rows, lookups);
    List<AnnotatedPluginDocument> updated = null;
    if(runtime.countUpdatedDocuments() != 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      updated = runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).collect(toList());
      updated = addAndReturnGeneratedDocuments(updated, true, Collections.emptyList());
    }
    CsvImportStats stats = new CsvImportStats(selectedDocuments, runtime);
    stats.print(logger);
    logger.info("UNUSED ROW (explanation): The row's registration number did not");
    logger.info("          correspond to any of the selected documents, but may or");
    logger.info("          may not correspond to other, unselected documents.");
    Info.operationCompletedSuccessfully(logger, "CRS Import");
    return updated == null ? Collections.emptyList() : updated;
  }

  private String getRegno(StoredDocument sd) {
    return sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
  }

  @Override
  protected String getLogTitle() {
    return "CRS Import";
  }
}
