package nl.naturalis.geneious.crs;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.stream.Collectors.toList;
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
class CrsSwingWorker extends PluginSwingWorker<CrsImportConfig> {

  static final String FILE_DESCRIPTION = "CRS file";
  static final String KEY_NAME = "registration number";

  private static final GuiLogger logger = GuiLogManager.getLogger(CrsSwingWorker.class);

  CrsSwingWorker(CrsImportConfig config) {
    super(config);
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws NonFatalException {
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    PreconditionValidator validator = new PreconditionValidator(selectedDocuments, required);
    validator.validate();
    Info.loadingFile(logger, FILE_DESCRIPTION, config);
    List<String[]> rows = new RowSupplier(config).getDataRows();
    Info.displayRowCount(logger, FILE_DESCRIPTION, rows.size());
    RuntimeInfo runtime = new RuntimeInfo(rows.size());
    CrsImporter importer = new CrsImporter(config, runtime);
    StoredDocumentTable<String> lookups = new StoredDocumentTable<>(selectedDocuments, this::getKey);
    importer.importRows(rows, lookups);
    List<AnnotatedPluginDocument> updated = null;
    if(runtime.countUpdatedDocuments() > 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      updated = runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).collect(toList());
      updated = addAndReturnGeneratedDocuments(updated, true, Collections.emptyList());
    }
    CsvImportStats stats = new CsvImportStats(selectedDocuments, runtime);
    stats.print(logger);
    logger.info("UNUSED ROW (explanation): The row's registration number was not");
    logger.info("           found in any of the selected documents, but may still");
    logger.info("           be present in other, unselected documents");
    Info.operationCompletedSuccessfully(logger, CrsDocumentOperation.NAME);
    return updated == null ? Collections.emptyList() : updated;
  }

  private String getKey(StoredDocument sd) {
    return sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
  }

  @Override
  protected String getLogTitle() {
    return CrsDocumentOperation.NAME;
  }
}
