package nl.naturalis.geneious.crs;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.gui.log.GuiLogger.plural;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.util.DebugUtil.toJson;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;

import java.util.Collections;
import java.util.List;

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
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;

/**
 * Does the actual work of importing a CRS file into Geneious.
 */
class CrsImporter extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(CrsImporter.class);

  private final CrsImportConfig cfg;

  CrsImporter(CrsImportConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws NonFatalException {
    int required = AT_LEAST_ONE_DOCUMENT_SELECTED | ALL_DOCUMENTS_IN_SAME_DATABASE;
    PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
    validator.validate();
    guiLogger.info("Loading CRS file " + cfg.getFile().getPath());
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    int numRows = rows.size() - cfg.getSkipLines();
    guiLogger.info("CRS file contains %s row%s", numRows, plural(numRows));
    StoredDocumentTable<String> selectedDocuments = new StoredDocumentTable<>(cfg.getSelectedDocuments(), this::getRegno);
    StoredDocumentList updated = new StoredDocumentList(selectedDocuments.size());
    int good = 0, bad = 0, unused = 0;
    NaturalisNote note;
    for (int i = cfg.getSkipLines(); i < rows.size(); ++i) {
      if ((note = createNote(rows, i)) == null) {
        ++bad;
        continue;
      }
      ++good;
      String regno = note.get(SMPL_REGISTRATION_NUMBER);
      guiLogger.debugf(() -> format("Scanning selected documents for reg.no. %s", regno));
      StoredDocumentList docs = selectedDocuments.get(regno);
      if (docs == null) {
        if (guiLogger.isDebugEnabled()) {
          guiLogger.debug("Not found. Row at line %s remains unused", i + 1);
        }
        ++unused;
      } else {
        guiLogger.debugf(() -> format("Found %1$s document%2$s. Updating document%2$s", docs.size(), plural(docs)));
        for (StoredDocument doc : docs) {
          if (doc.attach(note)) {
            updated.add(doc);
          } else {
            String fmt = "Document with reg.no. %s not updated (no new values in CRS file)";
            guiLogger.debugf(() -> format(fmt, regno));
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
    guiLogger.info("Number of valid rows in CRS file .......: %3d", good);
    guiLogger.info("Number of empty/bad rows in CRS file ...: %3d", bad);
    guiLogger.info("Number of unused rows in CRS file ......: %3d", unused);
    guiLogger.info("Number of selected documents ...........: %3d", selected);
    guiLogger.info("Number of updated documents ............: %3d", updated.size());
    guiLogger.info("Number of unchanged documents ..........: %3d", unchanged);
    guiLogger.info("UNUSED ROW (explanation): The row's registration number did not");
    guiLogger.info("          correspond to any of the selected documents, but may or");
    guiLogger.info("          may not correspond to other, unselected documents.");
    guiLogger.info("Operation completed successfully");
    return all;
  }

  private NaturalisNote createNote(List<String[]> rows, int rownum) {
    String[] values = rows.get(rownum);
    CrsRow row = new CrsRow(cfg.getColumnNumbers(), values);
    guiLogger.debugf(() -> format("Line %s: %s", rownum + 1, toJson(values)));
    CrsNoteFactory factory = new CrsNoteFactory(rownum + 1, row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note)));
      return note;
    } catch (InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private String getRegno(StoredDocument sd) {
    return sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
  }
}
