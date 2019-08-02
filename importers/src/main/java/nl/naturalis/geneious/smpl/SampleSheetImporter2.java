package nl.naturalis.geneious.smpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.RuntimeInfo;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.DocumentLookupTable;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Info;
import nl.naturalis.geneious.util.Messages.Warn;
import nl.naturalis.geneious.util.QueryUtils;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_EXTRACT_ID;
import static nl.naturalis.geneious.smpl.SampleSheetSwingWorker.FILE_DESCRIPTION;
import static nl.naturalis.geneious.smpl.SampleSheetSwingWorker.KEY_NAME;

/**
 * The sample sheet importer that runs when the user has opted to create place-holder documents (a#&46;k&#46;a#&46; dummies) for rows whose
 * extract ID does not correspond to any document, selected or not.
 * 
 * @author Ayco Holleman
 *
 */
class SampleSheetImporter2 {

  private static final GuiLogger logger = GuiLogManager.getLogger(SampleSheetImporter1.class);

  private final SampleSheetImportConfig config;
  private final RuntimeInfo runtime;

  private List<StoredDocument> newDummies;
  private List<StoredDocument> updatedDummies;

  /**
   * Creates a sample sheet importer configured using the provided configuration object and updating the provided runtime object as it
   * proceeds.
   * 
   * @param config
   * @param runtime
   */
  SampleSheetImporter2(SampleSheetImportConfig config, RuntimeInfo runtime) {
    this.config = config;
    this.runtime = runtime;
  }

  /**
   * Processes the provided rows, using them to enrich the provided documents. The documents are cached into a lookup table so they can be
   * quickly scanned for each and every row. The lookup table is keyed on the document's extract ID.
   * 
   * @param rows
   * @param lookups
   * @throws NonFatalException 
   */
  void importRows(List<String[]> rows, DocumentLookupTable<String> lookups) throws NonFatalException {
    /*
     * Collect all extract IDs in the sample sheet that do not correspond to any of the selected documents. For those IDs we need to check
     * whether or not they exist at all in the Geneious database.
     */
    logger.info("Collecting extract IDs in sample sheet");
    Set<String> idsInSampleSheet = collectIdsInSampleSheet(rows);
    Set<String> extraIds = idsInSampleSheet
        .stream()
        .filter(not(lookups::containsKey))
        .map(id -> "e" + id)
        .collect(toSet());
    List<AnnotatedPluginDocument> searchResult = QueryUtils.findByExtractID(config.getTargetDatabase(), extraIds);
    // All documents that correspond to a sample sheet row, but that were not selected by the user
    DocumentLookupTable<String> unselected = new DocumentLookupTable<>(searchResult, this::getKey);
    // The extract IDs that are both in the sample sheet and in the selected documents:
    int overlap = (int) idsInSampleSheet.stream().filter(lookups::containsKey).count();
    logger.info("Sample sheet contains %s extract ID%s matching selected documents", overlap, plural(overlap));
    logger.info("Sample sheet contains %s extract ID%s matching unselected documents", searchResult.size(), plural(searchResult));
    /*
     * We must only create dummies for sample sheet rows that do not correspond to any document in the target database, selected or not. So
     * newIds is likely the number of dummies we are going to create, unless the sample sheet rows containing them turn out to be invalid.
     */
    int newIds = idsInSampleSheet.size() - overlap - unselected.size();
    logger.info("Sample sheet contains %s new extract ID%s", newIds, plural(newIds));
    newDummies = new ArrayList<>(newIds);
    updatedDummies = new ArrayList<>();
    for (int i = 0; i < rows.size(); ++i) {
      int line = i + config.getSkipLines() + 1;
      Debug.showRow(logger, line, rows.get(i));
      SampleSheetRow row = new SampleSheetRow(config.getColumnNumbers(), rows.get(i));
      String key = row.get(COL_EXTRACT_ID);
      if (key == null) {
        Warn.missingKey(logger, KEY_NAME, line);
        runtime.markBad(i);
        continue;
      }
      Integer prevLine = runtime.checkAndAddKey(key, line);
      if (prevLine != null) {
        Warn.duplicateKey(logger, key, line, prevLine);
        continue;
      }
      NaturalisNote note = createNote(row, line);
      if (note == null) {
        runtime.markBad(i);
        continue;
      }
      Debug.scanningSelectedDocuments(logger, KEY_NAME, key);
      List<StoredDocument> docs = lookups.get(key);
      if (docs == null) {
        logger.debugf(() -> format("None found. Scanning query cache for unselected documents with extract ID %s", key));
        List<StoredDocument> docs1 = unselected.get(key);
        if (docs1 == null) {
          logger.debugf(() -> format("None found. Creating dummy document for extract ID %s", key));
          newDummies.add(new DummySequence(note).wrap());
          runtime.markUsed(i);
        } else {
          Info.foundUnselectedDocuments(logger, docs1);
        }
      } else {
        annotateDocuments(docs, note);
        runtime.markUsed(i);
      }
      lookups.remove(key);
    }
  }

  /**
   * Returns the dummy documents created by the importer.
   * 
   * @return
   */
  List<StoredDocument> getNewDummies() {
    return newDummies;
  }

  /**
   * Returns the dummy documents that were updated by the importer.
   * 
   * @return
   */
  List<StoredDocument> getUpdatedDummies() {
    return updatedDummies;
  }

  private Set<String> collectIdsInSampleSheet(List<String[]> rows) {
    int colno = config.getColumnNumbers().get(COL_EXTRACT_ID);
    return rows.stream()
        .filter(row -> colno < row.length)
        .filter(row -> StringUtils.isNotBlank(row[colno]))
        .map(row -> row[colno])
        .collect(toSet());
  }

  private void annotateDocuments(List<StoredDocument> docs, NaturalisNote note) {
    // Chop off the 'e' that we added ourselves (it's not in the sample sheet)
    String id = note.getExtractId().substring(1);
    Debug.foundDocumensMatchingKey(logger, docs, KEY_NAME, id);
    int updated = 0;
    for (StoredDocument doc : docs) {
      if (doc.attach(note)) {
        runtime.updated(doc);
        if (doc.isDummy()) {
          updatedDummies.add(doc);
        }
        ++updated;
      } else {
        Debug.noNewValues(logger, doc.getName(), FILE_DESCRIPTION);
      }
    }
    Debug.updatedDocuments(logger, docs, updated, KEY_NAME, id);
  }

  private String getKey(StoredDocument sd) {
    String id = sd.getNaturalisNote().getExtractId();
    return id == null ? null : id.substring(1); // Chop off the 'e'
  }

  private static NaturalisNote createNote(SampleSheetRow row, int line) {
    SmplNoteFactory factory = new SmplNoteFactory(row, line);
    try {
      NaturalisNote note = factory.createNote();
      Debug.showNote(logger, note);
      return note;
    } catch (InvalidRowException e) {
      logger.error(e.getMessage());
      return null;
    }
  }

}
