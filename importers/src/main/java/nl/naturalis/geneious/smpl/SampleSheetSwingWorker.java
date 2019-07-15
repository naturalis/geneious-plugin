package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.stream.Collectors.toList;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
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
 * Manages and coordinates the import of sample sheets into Geneious.
 * 
 * @author Ayco Holleman
 */
class SampleSheetSwingWorker extends PluginSwingWorker {

  private static final GuiLogger logger = GuiLogManager.getLogger(SampleSheetSwingWorker.class);
  private static final String FILE_DESCRIPTION = "sample sheet";

  private final SampleSheetImportConfig config;

  SampleSheetSwingWorker(SampleSheetImportConfig config) {
    this.config = config;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws DatabaseServiceException, NonFatalException {
    if(config.isCreateDummies()) {
      return updateOrCreateDummies();
    }
    return updateOnly();
  }

  private List<AnnotatedPluginDocument> updateOrCreateDummies() throws DatabaseServiceException, NonFatalException {
    int required = ALL_DOCUMENTS_IN_SAME_DATABASE;
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    PreconditionValidator validator = new PreconditionValidator(selectedDocuments, required);
    validator.validate();
    Info.loadingFile(logger, FILE_DESCRIPTION, config);
    List<String[]> rows = new RowSupplier(config).getAllRows();
    int numRows = rows.size() - config.getSkipLines();
    Info.displayRowCount(logger, FILE_DESCRIPTION, numRows);
    RuntimeInfo runtime = new RuntimeInfo(numRows);
    SampleSheetImporter2 importer = new SampleSheetImporter2(config, runtime);
    StoredDocumentTable<String> lookups = new StoredDocumentTable<>(selectedDocuments, this::getKey);
    importer.importRows(rows, lookups);
    List<AnnotatedPluginDocument> updated = null;
    if(runtime.countUpdatedDocuments() > 0 || importer.getNewDummies().size() > 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      importer.getNewDummies().forEach(StoredDocument::saveAnnotations);
      List<AnnotatedPluginDocument> all = new ArrayList<>(runtime.countUpdatedDocuments() + importer.getNewDummies().size());
      runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).forEach(all::add);
      importer.getNewDummies().stream().map(StoredDocument::getGeneiousDocument).forEach(all::add);
    }
    int unchanged = selectedDocuments.size() - runtime.countUpdatedDocuments() - importer.getUpdatedDummies().size();
    logger.info("Number of valid rows ................: %3d", runtime.countGoodRows());
    logger.info("Number of empty/bad rows ............: %3d", runtime.countBadRows());
    logger.info("Number of unused rows ...............: %3d", runtime.countUnusedRows());
    logger.info("Number of selected documents ........: %3d", selectedDocuments.size());
    logger.info("Number of updated documents .........: %3d", runtime.countUpdatedDocuments() - importer.getUpdatedDummies().size());
    logger.info("Number of updated dummies ...........: %3d", importer.getUpdatedDummies().size());
    logger.info("Number of unchanged documents .......: %3d", unchanged);
    logger.info("Number of dummy documents created ...: %3d", importer.getNewDummies().size());
    logger.info("UNUSED ROW (explanation): The row's extract ID was found in an");
    logger.info("          existing document, but the  document was not selected");
    logger.info("          and therefore not updated.");
    logger.info("Import type: update existing documents or create dummies");
    Info.operationCompletedSuccessfully(logger, getLogTitle());
    return updated == null ? Collections.emptyList() : updated;
  }

  private List<AnnotatedPluginDocument> updateOnly() throws NonFatalException {
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    PreconditionValidator validator = new PreconditionValidator(selectedDocuments, required);
    validator.validate();
    Info.loadingFile(logger, FILE_DESCRIPTION, config);
    List<String[]> rows = new RowSupplier(config).getAllRows();
    int numRows = rows.size() - config.getSkipLines();
    Info.displayRowCount(logger, FILE_DESCRIPTION, numRows);
    RuntimeInfo runtime = new RuntimeInfo(numRows);
    SampleSheetImporter1 importer = new SampleSheetImporter1(config, runtime);
    StoredDocumentTable<String> lookups = new StoredDocumentTable<>(selectedDocuments, this::getKey);
    importer.importRows(rows, lookups);
    List<AnnotatedPluginDocument> updated = null;
    if(runtime.countUpdatedDocuments() != 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      updated = runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).collect(toList());
      updated = addAndReturnGeneratedDocuments(updated, true, Collections.emptyList());
    }
    CsvImportStats stats = new CsvImportStats(selectedDocuments, runtime);
    stats.print(logger);
    logger.info("UNUSED ROW (explanation): The row's extract ID did not correspond");
    logger.info("          to any of the selected documents, but may or may not");
    logger.info("          correspond to other, unselected documents.");
    logger.info("Import type: update existing documents; do not create dummies");
    Info.operationCompletedSuccessfully(logger, getLogTitle());
    return updated == null ? Collections.emptyList() : updated;
  }

  private String getKey(StoredDocument sd) {
    return sd.getNaturalisNote().get(SMPL_EXTRACT_ID);
  }

  @Override
  protected String getLogTitle() {
    return "Sample Sheet Import";
  }

}
