package nl.naturalis.geneious.smpl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.APDList;
import nl.naturalis.geneious.util.StoredDocument;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

import static java.util.function.Predicate.not;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.util.DebugUtil.toJson;
import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;
import static nl.naturalis.geneious.util.QueryUtils.getTargetDatabaseName;

/**
 * Does the actual work of importing a sample sheet into Geneious.
 */
class SampleSheetImporter extends SwingWorker<APDList, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetImporter.class);

  private final SampleSheetImportConfig cfg;

  SampleSheetImporter(SampleSheetImportConfig cfg) {
    this.cfg = cfg;
  }

  /**
   * Enriches the documents selected within the GUI with data from the sample sheet. Documents and sample sheet records
   * are linked using their extract ID. In addition, if requested, this routine will create dummy documents from sample
   * sheet records if their extract ID does not exist yet.
   */
  @Override
  protected APDList doInBackground() throws DatabaseServiceException {
    return importSampleSheet();
  }

  private APDList importSampleSheet() throws DatabaseServiceException {
    if (cfg.isCreateDummies()) {
      return updateOrCreateDummies();
    }
    return updateOnly();
  }

  private APDList updateOrCreateDummies() throws DatabaseServiceException {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    guiLogger.info("Collecting extract IDs");
    Set<String> extractIds = collectExtractIds(rows);
    StoredDocumentTable selectedDocuments = new StoredDocumentTable(cfg.getSelectedDocuments());
    guiLogger.info("Searching database \"%s\" for matching documents", getTargetDatabaseName());
    Set<String> searchFor = extractIds.stream()
        .filter(not(selectedDocuments::containsKey))
        .collect(Collectors.toSet());
    StoredDocumentTable unselected = new StoredDocumentTable(findByExtractID(searchFor));
    int numNewExtractIds = extractIds.size() - selectedDocuments.keySet().size() - unselected.keySet().size();
    guiLogger.info("Sample sheet contains %s new extract ID%s", numNewExtractIds, plural(numNewExtractIds));
    APDList dummies = new APDList();
    int good = 0, bad = 0, updated = 0, updatedDummies = 0, unused = 0;
    NaturalisNote note;
    for (int i = 0; i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      guiLogger.debugf(() -> format("Scanning selected documents for extract ID %s", id));
      StoredDocumentList docs0 = selectedDocuments.get(id);
      if (docs0 == null) {
        guiLogger.debugf(() -> format("Not found. Scanning query cache for unselected documents with extract ID %s", id));
        StoredDocumentList docs1 = unselected.get(id);
        if (docs1 == null) {
          guiLogger.debugf(() -> format("Not found. Creating dummy document for extract ID %s", id));
          dummies.add(createDummy(note));
        } else {
          handleUnselectedDocument(docs1, i);
          ++unused;
        }
      } else {
        String fmt = "Found %1$s document%2$s. Updating document%2$s";
        guiLogger.debugf(() -> format(fmt, docs0.size(), plural(docs0)));
        for (StoredDocument doc : docs0) {
          if (note.saveTo(doc)) {
            if (doc.isDummy()) {
              ++updatedDummies;
            } else {
              ++updated;
            }
          } else {
            String fmt1 = "Document with extract ID %s not updated (no new values in sample sheet)";
            guiLogger.debugf(() -> format(fmt1, id));
          }
        }
      }
    }
    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated - updatedDummies;
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", selected);
    guiLogger.info("Number of unchanged documents ..............: %3d", unchanged);
    guiLogger.info("Number of updated documents ................: %3d", updated);
    guiLogger.info("Number of updated dummies ..................: %3d", updatedDummies);
    guiLogger.info("Number of dummy documents created ..........: %3d", dummies.size());
    guiLogger.info("UNUSED ROW (explanation): The row's extract ID was found in an");
    guiLogger.info("          existing document, but the  document was not selected");
    guiLogger.info("          and therefore not updated.");
    guiLogger.info("Import type: update existing documents or create dummies");
    return dummies.isEmpty() ? null : dummies;
  }

  private APDList updateOnly() {
    guiLogger.info("Loading sample sheet " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    StoredDocumentTable selectedDocuments = new StoredDocumentTable(cfg.getSelectedDocuments());
    int good = 0, bad = 0, updated = 0, unused = 0;
    NaturalisNote note;
    for (int i = 1; i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String id = note.getExtractId();
      guiLogger.debugf(() -> format("Scanning selected documents for extract ID %s", id));
      StoredDocumentList docs = selectedDocuments.get(id);
      if (docs == null) {
        int rownum = userfriendly(i);
        guiLogger.debugf(() -> format("Not found. Row at line %s remains unused", rownum));
        ++unused;
      } else {
        guiLogger.debugf(() -> format("Found %1$s document%2$s. Updating document%2$s", docs.size(), plural(docs)));
        for (StoredDocument doc : docs) {
          if (note.saveTo(doc)) {
            ++updated;
          } else {
            String fmt = "Document with extract ID %s not updated (no new values in sample sheet)";
            guiLogger.debugf(() -> format(fmt, id));
          }
        }
      }
    }
    int selected = cfg.getSelectedDocuments().size();
    int unchanged = selected - updated;
    guiLogger.info("Number of valid rows in sample sheet .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in sample sheet ...: %3d", bad);
    guiLogger.info("Number of unused rows in sample sheet ......: %3d", unused);
    guiLogger.info("Number of selected documents ...............: %3d", selected);
    guiLogger.info("Number of updated documents ................: %3d", updated);
    guiLogger.info("Number of unchanged documents ..............: %3d", unchanged);
    guiLogger.info("UNUSED ROW (explanation): The row's extract ID did not correspond");
    guiLogger.info("          to any of the selected documents, but may or may not");
    guiLogger.info("          correspond to other, unselected documents.");
    guiLogger.info("Import type: update existing documents; do not create dummies");
    return null; // Tells Geneious that we didn't create any new documents.
  }

  private NaturalisNote createNote(List<String[]> rows, int rownum) {
    String[] values = rows.get(rownum);
    int x = userfriendly(rownum);
    SampleSheetRow row = new SampleSheetRow(cfg.getColumnNumbers(), values);
    if (row.isEmptyRow()) {
      guiLogger.debugf(() -> format("Ignoring empty row at line %s", x));
      return null;
    }
    guiLogger.debugf(() -> format("Line %s: %s", x, toJson(values, false)));
    SmplNoteFactory factory = new SmplNoteFactory(x, row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note, false)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private Set<String> collectExtractIds(List<String[]> rows) {
    int colno = cfg.getColumnNumbers().get(SampleSheetColumn.EXTRACT_ID);
    return rows.stream()
        .filter(row -> colno < row.length)
        .filter(row -> StringUtils.isNotBlank(row[colno]))
        .map(row -> "e" + row[colno])
        .collect(Collectors.toSet());
  }

  private static AnnotatedPluginDocument createDummy(NaturalisNote note) {
    AnnotatedPluginDocument apd = new DummySequenceDocument(note).wrap();
    apd.save();
    return apd;
  }

  private static void handleUnselectedDocument(StoredDocumentList docs, int row) {
    String extractId = docs.get(0).getNaturalisNote().getExtractId();
    if (docs.size() == 1) {
      String fmt = "Row at line %s (%s) corresponds to an existing document, but the document was not selected and therefore not updated";
      guiLogger.debugf(() -> format(fmt, userfriendly(row), extractId));
    } else {
      String fmt = "Row at line %s (%s) corresponds to %s existing documents, but they were not selected and therefore not updated";
      guiLogger.debugf(() -> format(fmt, userfriendly(row), extractId, docs.size()));
    }
  }

  private static int userfriendly(int zerobased) {
    return zerobased + 1;
  }

  private static String plural(Collection<?> c) {
    return plural(c.size());
  }

  private static String plural(int i) {
    return i == 1 ? "" : "s";
  }

}
