package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static java.util.function.Predicate.not;
import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.gui.log.GuiLogger.plural;
import static nl.naturalis.geneious.util.DebugUtil.toJson;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.PreconditionValidator;
import nl.naturalis.geneious.util.QueryUtils;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class SampleSheetImporter extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetImporter.class);

  private final SampleSheetImportConfig cfg;

  SampleSheetImporter(SampleSheetImportConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws DatabaseServiceException, NonFatalException {
    if (cfg.isCreateDummies()) {
      int required = ALL_DOCUMENTS_IN_SAME_DATABASE;
      PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
      validator.validate();
      return updateOrCreateDummies();
    }
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
    validator.validate();
    return updateSelectedDocuments();
  }

  private List<AnnotatedPluginDocument> updateOrCreateDummies() throws DatabaseServiceException {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    guiLogger.info("Collecting extract IDs");
    Set<String> extractIds = collectExtractIds(rows);
    StoredDocumentTable<String> selectedDocuments = createLookupTableForSelectedDocuments();
    guiLogger.info("Searching database %s for matching extract IDs", getTargetDatabaseName());
    // Get all extract IDs in sample sheet that do not correspond to any of the selected documents
    Set<String> unselectedExtractIds = extractIds.stream()
        .filter(not(selectedDocuments::containsKey))
        .collect(Collectors.toSet());
    List<AnnotatedPluginDocument> searchResult = QueryUtils.findByExtractID(unselectedExtractIds);
    StoredDocumentTable<String> unselected = createLookupTableForUnselectedDocuments(searchResult);
    int overlap = (int) extractIds.stream().filter(selectedDocuments::containsKey).count();
    int numRows = rows.size() - cfg.getSkipLines();
    guiLogger.info("Sample sheet contains %s row%s", numRows, plural(numRows));
    guiLogger.info("Sample sheet contains %s extract ID%s matching selected documents", overlap, plural(overlap));
    guiLogger.info("Sample sheet contains %s extract ID%s matching unselected documents", searchResult.size(), plural(searchResult));
    // Estimate only! If some of the rows containing new exract IDs are invalid, it will be less:
    int dummyCount = extractIds.size() - overlap - unselected.keySet().size();
    guiLogger.info("Sample sheet contains %s new extract ID%s", dummyCount, plural(dummyCount));
    int good = 0, bad = 0, updatedDummies = 0, unused = 0;
    StoredDocumentList updated = new StoredDocumentList(overlap);
    StoredDocumentList created = new StoredDocumentList(dummyCount);
    NaturalisNote note;
    for (int i = cfg.getSkipLines(); i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      guiLogger.debugf(() -> format("Searching selected documents for extract ID %s", id));
      StoredDocumentList docs0 = selectedDocuments.get(id);
      if (docs0 == null) {
        guiLogger.debugf(() -> format("Not found. Searching query cache for unselected documents with extract ID %s", id));
        StoredDocumentList docs1 = unselected.get(id);
        if (docs1 == null) {
          guiLogger.debugf(() -> format("Not found. Creating dummy document for extract ID %s", id));
          created.add(new DummySequence(note).wrap());
        } else {
          ++unused;
          if (guiLogger.isDebugEnabled()) {
            logUnusedRow(docs1);
          }
        }
      } else {
        if (guiLogger.isDebugEnabled()) {
          String fmt = "Found %1$s document%2$s. Comparing values in sample sheet with values in document%2$s (updating document%2$s if necessary)";
          guiLogger.debug(fmt, docs0.size(), plural(docs0));
        }
        for (StoredDocument doc : docs0) {
          if (doc.attach(note)) {
            updated.add(doc);
            if (doc.isDummy()) {
              ++updatedDummies;
            }
          } else if (guiLogger.isDebugEnabled()) {
            guiLogger.debug("Document with extract ID %s not updated (no new values in sample sheet)", id);
          }
        }
      }
    }

    updated.forEach(StoredDocument::saveAnnotations);
    created.forEach(StoredDocument::saveAnnotations);

    List<AnnotatedPluginDocument> all = created.unwrap();
    all.addAll(updated.unwrap());
    if (!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }

    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated.size();
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", selected);
    guiLogger.info("Number of unchanged documents ..............: %3d", unchanged);
    guiLogger.info("Number of updated documents ................: %3d", updated.size() - updatedDummies);
    guiLogger.info("Number of updated dummies ..................: %3d", updatedDummies);
    guiLogger.info("Number of dummy documents created ..........: %3d", all.size());
    guiLogger.info("UNUSED ROW (explanation): The row's extract ID was found in an");
    guiLogger.info("          existing document, but the  document was not selected");
    guiLogger.info("          and therefore not updated.");
    guiLogger.info("Import type: update existing documents or create dummies");
    guiLogger.info("Operation completed successfully");
    return all;
  }

  private List<AnnotatedPluginDocument> updateSelectedDocuments() {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    int numRows = rows.size() - cfg.getSkipLines();
    guiLogger.info("Sample sheet contains %s row%s", numRows, plural(numRows));
    StoredDocumentTable<String> selectedDocuments = createLookupTableForSelectedDocuments();
    StoredDocumentList updated = new StoredDocumentList(selectedDocuments.size());
    int good = 0, bad = 0, unused = 0;
    NaturalisNote note;
    for (int i = cfg.getSkipLines(); i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      guiLogger.debugf(() -> format("Searching selected documents for extract ID %s", id));
      StoredDocumentList docs = selectedDocuments.get(id);
      if (docs == null) {
        if (guiLogger.isDebugEnabled()) {
          guiLogger.debug("Not found. Row at line %s remains unused", i + 1);
        }
        ++unused;
      } else {
        if (guiLogger.isDebugEnabled()) {
          String fmt = "Found %1$s document%2$s. Comparing values in sample sheet with values in document%2$s (updating document%2$s if necessary)";
          guiLogger.debug(fmt, docs.size(), plural(docs));
        }
        for (StoredDocument doc : docs) {
          if (doc.attach(note)) {
            updated.add(doc);
          } else if (guiLogger.isDebugEnabled()) {
            guiLogger.debug("Document with extract ID %s not updated (no new values in sample sheet)", id);
          }
        }
      }
    }
    
    updated.forEach(StoredDocument::saveAnnotations);
    List<AnnotatedPluginDocument> all = updated.unwrap();
    all.addAll(updated.unwrap());
    if (!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }

    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated.size();
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", selected);
    guiLogger.info("Number of updated documents ................: %3d", updated.size());
    guiLogger.info("Number of unchanged documents ..............: %3d", unchanged);
    guiLogger.info("UNUSED ROW (explanation): The row's extract ID did not correspond");
    guiLogger.info("          to any of the selected documents, but may or may not");
    guiLogger.info("          correspond to other, unselected documents.");
    guiLogger.info("Import type: update existing documents; do not create dummies");
    guiLogger.info("Operation completed successfully");
    return all;
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

  private Set<String> collectExtractIds(List<String[]> rows) {
    int colno = cfg.getColumnNumbers().get(SampleSheetColumn.EXTRACT_ID);
    return rows.subList(cfg.getSkipLines(), rows.size())
        .stream()
        .filter(row -> colno < row.length)
        .filter(row -> StringUtils.isNotBlank(row[colno]))
        .map(row -> "e" + row[colno])
        .collect(Collectors.toSet());
  }

  private static void logUnusedRow(StoredDocumentList docs) {
    String extractId = docs.get(0).getNaturalisNote().getExtractId();
    String fmt;
    if (docs.size() == 1) {
      fmt = "Found %s %s document with extract ID %s, but the document was not selected and therefore not updated";
      guiLogger.debug(fmt, docs.size(), docs.get(0).getType(), extractId);
    } else {
      fmt = "Found %s documents with extract ID %s, but the documents were not selected and therefore not updated";
      guiLogger.debug(fmt, docs.size(), extractId);
    }
  }

}
