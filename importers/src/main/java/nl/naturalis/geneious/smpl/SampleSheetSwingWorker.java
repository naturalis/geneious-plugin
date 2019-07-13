package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.JsonUtil.toJson;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.ImportStats;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Info;
import nl.naturalis.geneious.util.PreconditionValidator;
import nl.naturalis.geneious.util.QueryUtils;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

/**
 * Manages and coordinates the import of sample sheets into Geneious.
 * 
 * @author Ayco Holleman
 */
class SampleSheetSwingWorker extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetSwingWorker.class);

  private final SampleSheetImportConfig cfg;

  SampleSheetSwingWorker(SampleSheetImportConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws DatabaseServiceException, NonFatalException {
    if(cfg.isCreateDummies()) {
      int required = ALL_DOCUMENTS_IN_SAME_DATABASE;
      PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
      validator.validate();
      return updateOrCreateDummies();
    }
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
    validator.validate();
    return updateOnly();
  }

  private List<AnnotatedPluginDocument> updateOrCreateDummies() throws DatabaseServiceException {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    guiLogger.info("Collecting extract IDs");
    Set<String> idsInSampleSheet = collectIdsInSampleSheet(rows);
    StoredDocumentTable<String> selectedDocuments = createLookupTableForSelectedDocuments();
    guiLogger.info("Searching database %s for matching extract IDs", getTargetDatabaseName());
    /*
     * First, get all extract IDs in the sample sheet that do not correspond to any of the selected documents. If an extract
     * ID is found in a selected document, we don't again need to search for it in the database, because, well, that's where
     * the document came from.
     */
    Set<String> extraIds = idsInSampleSheet.stream().filter(not(selectedDocuments::containsKey)).collect(toSet());
    List<AnnotatedPluginDocument> searchResult = QueryUtils.findByExtractID(extraIds);
    StoredDocumentTable<String> unselected = createLookupTableForUnselectedDocuments(searchResult);
    // The extract IDs that are both in the sample sheet and in the selected documents:
    int overlap = (int) idsInSampleSheet.stream().filter(selectedDocuments::containsKey).count();
    int numRows = rows.size() - cfg.getSkipLines();
    guiLogger.info("Sample sheet contains %s row%s (exluding header rows)", numRows, plural(numRows));
    guiLogger.info("Sample sheet contains %s extract ID%s matching selected documents", overlap, plural(overlap));
    guiLogger.info("Sample sheet contains %s extract ID%s matching unselected documents", searchResult.size(), plural(searchResult));
    /*
     * We must only create dummies for sample sheet rows that do not correspond to any document in the target database,
     * selected or not. So newIds is lilkely the number of dummies we are going to create, unless the sample sheet rows
     * containing them turn out to be invalid.
     */
    int newIds = idsInSampleSheet.size() - overlap - unselected.size();
    guiLogger.info("Sample sheet contains %s new extract ID%s", newIds, plural(newIds));
    int good = 0, bad = 0, updatedDummies = 0, unused = 0;
    StoredDocumentList updated = new StoredDocumentList(overlap);
    StoredDocumentList created = new StoredDocumentList(newIds); // (dummies)
    NaturalisNote note;
    for (int i = cfg.getSkipLines(); i < rows.size(); ++i) {
      if((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      Debug.scanningSelectedDocuments(guiLogger, "extract ID", id);
      StoredDocumentList docs0 = selectedDocuments.get(id);
      if(docs0 == null) {
        guiLogger.debugf(() -> format("Not found. Scanning query cache for unselected documents with extract ID %s", id));
        StoredDocumentList docs1 = unselected.get(id);
        if(docs1 == null) {
          guiLogger.debugf(() -> format("Not found. Creating dummy document for extract ID %s", id));
          created.add(new DummySequence(note).wrap());
        } else {
          ++unused;
          if(guiLogger.isDebugEnabled()) {
            logUnusedRow(docs1);
          }
        }
      } else {
        updatedDummies += annotateMatchingDocuments(docs0, note, updated, id);
      }
    }

    updated.forEach(StoredDocument::saveAnnotations);
    created.forEach(StoredDocument::saveAnnotations);

    List<AnnotatedPluginDocument> all = created.unwrap();
    all.addAll(updated.unwrap());
    if(!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }

    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated.size();
    guiLogger.info("Number of valid rows ................: %3d", good);
    guiLogger.info("Number of empty/bad rows ............: %3d", bad);
    guiLogger.info("Number of unused rows ...............: %3d", unused);
    guiLogger.info("Number of selected documents ........: %3d", selected);
    guiLogger.info("Number of updated documents .........: %3d", updated.size() - updatedDummies);
    guiLogger.info("Number of updated dummies ...........: %3d", updatedDummies);
    guiLogger.info("Number of unchanged documents .......: %3d", unchanged);
    guiLogger.info("Number of dummy documents created ...: %3d", created.size());
    guiLogger.info("UNUSED ROW (explanation): The row's extract ID was found in an");
    guiLogger.info("          existing document, but the  document was not selected");
    guiLogger.info("          and therefore not updated.");
    guiLogger.info("Import type: update existing documents or create dummies");
    Info.operationCompletedSuccessfully(guiLogger, "Sample Sheet Import");
    return all;
  }

  private List<AnnotatedPluginDocument> updateOnly() {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    int numRows = rows.size() - cfg.getSkipLines();
    guiLogger.info("Sample sheet contains %s row%s (excluding header rows)", numRows, plural(numRows));
    StoredDocumentTable<String> selectedDocuments = createLookupTableForSelectedDocuments();
    StoredDocumentList updated = new StoredDocumentList(selectedDocuments.size());
    int good = 0, bad = 0, unused = 0;
    NaturalisNote note;
    for (int i = cfg.getSkipLines(); i < rows.size(); ++i) {
      if((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      Debug.scanningSelectedDocuments(guiLogger, "extract ID", id);
      StoredDocumentList docs = selectedDocuments.get(id);
      if(docs == null) {
        Debug.noDocumentsMatchingKey(guiLogger, i + 1);
        ++unused;
      } else {
        annotateMatchingDocuments(docs, note, updated, id);
      }
    }

    updated.forEach(StoredDocument::saveAnnotations);
    List<AnnotatedPluginDocument> all = updated.unwrap();
    all.addAll(updated.unwrap());
    if(!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }

    new ImportStats()
        .rowStats(good, bad, unused)
        .docStats(cfg.getSelectedDocuments().size(), updated.size())
        .print(guiLogger);

    guiLogger.info("UNUSED ROW (explanation): The row's extract ID did not correspond");
    guiLogger.info("          to any of the selected documents, but may or may not");
    guiLogger.info("          correspond to other, unselected documents.");
    guiLogger.info("Import type: update existing documents; do not create dummies");
    Info.operationCompletedSuccessfully(guiLogger, "Sample Sheet Import");
    return all;
  }

  private Set<String> collectIdsInSampleSheet(List<String[]> rows) {
    int colno = cfg.getColumnNumbers().get(SampleSheetColumn.EXTRACT_ID);
    return rows.subList(cfg.getSkipLines(), rows.size())
        .stream()
        .filter(row -> colno < row.length)
        .filter(row -> StringUtils.isNotBlank(row[colno]))
        .map(row -> "e" + row[colno])
        .collect(toSet());
  }

  private StoredDocumentTable<String> createLookupTableForSelectedDocuments() {
    return new StoredDocumentTable<>(cfg.getSelectedDocuments(), sd -> sd.getNaturalisNote().getExtractId());
  }

  private static StoredDocumentTable<String> createLookupTableForUnselectedDocuments(List<AnnotatedPluginDocument> searchResult) {
    return new StoredDocumentTable<>(searchResult, sd -> sd.getNaturalisNote().getExtractId());
  }

  private NaturalisNote createNote(List<String[]> rows, int rownum) {
    String[] values = rows.get(rownum);
    SampleSheetRow row = new SampleSheetRow(cfg.getColumnNumbers(), values);
    // Convert rownum to user-friendly (one-based) line number
    guiLogger.debugf(() -> format("Line %s: %s", rownum + 1, toJson(values)));
    SmplNoteFactory factory = new SmplNoteFactory(rownum + 1, row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private static int annotateMatchingDocuments(StoredDocumentList docs, NaturalisNote note, StoredDocumentList updated, String extractId) {
    Debug.foundDocumensMatchingKey(guiLogger, "sample sheet", docs);
    int updatedDummies = 0;
    for (StoredDocument doc : docs) {
      if(doc.attach(note)) {
        updated.add(doc);
        if(doc.isDummy()) {
          ++updatedDummies;
        }
      } else {
        Debug.noNewValues(guiLogger, "sample sheet", "extract ID", extractId);
      }
    }
    return updatedDummies;
  }

  private static void logUnusedRow(StoredDocumentList docs) {
    // All documents will have the same extract ID b/c that's what they were keyed on. Pick any.
    String extractId = docs.get(0).getNaturalisNote().getExtractId();
    String fmt;
    if(docs.size() == 1) {
      fmt = "Found 1 %s document with extract ID %s, but the document was not selected and therefore not updated";
      guiLogger.debug(fmt, docs.get(0).getType(), extractId);
    } else {
      fmt = "Found %s documents with extract ID %s, but the documents were not selected and therefore not updated";
      guiLogger.debug(fmt, docs.size(), extractId);
    }
  }

  @Override
  protected String getLogTitle() {
    return "Sample Sheet Import";
  }

}
