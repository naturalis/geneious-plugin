package nl.naturalis.geneious.crs;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.stream.Collectors.toList;
import static nl.naturalis.geneious.Precondition.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.Precondition.AT_LEAST_ONE_DOCUMENT_SELECTED;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;

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
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.DocumentLookupTable;
import nl.naturalis.geneious.util.Messages.Info;

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
    List<AnnotatedPluginDocument> selectedDocuments = config.getSelectedDocuments();
    Info.loadingFile(logger, FILE_DESCRIPTION, config);
    List<String[]> rows = new RowSupplier(config).getDataRows();
    Info.displayRowCount(logger, FILE_DESCRIPTION, rows.size());
    RuntimeInfo runtime = new RuntimeInfo(rows.size());
    CrsImporter importer = new CrsImporter(config, runtime);
    DocumentLookupTable<String> lookups = new DocumentLookupTable<>(selectedDocuments, this::getKey);
    importer.importRows(rows, lookups);
    List<AnnotatedPluginDocument> updated = null;
    if(runtime.countUpdatedDocuments() > 0) {
      runtime.getUpdatedDocuments().forEach(StoredDocument::saveAnnotations);
      updated = runtime.getUpdatedDocuments().stream().map(StoredDocument::getGeneiousDocument).collect(toList());
      updated = addAndReturnGeneratedDocuments(updated, true, Collections.emptyList());
    }
    CsvImportStats stats = new CsvImportStats(selectedDocuments, runtime);
    stats.print(logger);
    Info.explainUnusedRowForCrsAndBold(logger);
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

  @Override
  protected Set<Precondition> getPreconditions() {
    return EnumSet.of(AT_LEAST_ONE_DOCUMENT_SELECTED, ALL_DOCUMENTS_IN_SAME_DATABASE);
  }
}
