package nl.naturalis.geneious.bold;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static nl.naturalis.geneious.bold.BoldColumn.MARKER;
import static nl.naturalis.geneious.bold.BoldColumn.SAMPLE_ID;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.util.JsonUtil.toJson;
import static nl.naturalis.geneious.util.PreconditionValidator.ALL_DOCUMENTS_IN_SAME_DATABASE;
import static nl.naturalis.geneious.util.PreconditionValidator.AT_LEAST_ONE_DOCUMENT_SELECTED;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.CommonStatistics;
import nl.naturalis.geneious.util.Messages;
import nl.naturalis.geneious.util.PreconditionValidator;
import nl.naturalis.geneious.util.StoredDocumentList;
import nl.naturalis.geneious.util.StoredDocumentTable;
import static java.util.stream.Collectors.*;
import static nl.naturalis.geneious.note.NaturalisField.*;

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
    PreconditionValidator validator = new PreconditionValidator(cfg.getSelectedDocuments(), required);
    validator.validate();
    guiLogger.info("Loading BOLD file " + cfg.getFile().getPath());
    BoldNormalizer normalizer = new BoldNormalizer(cfg);
    Map<String, List<String[]>> allRows = normalizer.normalizeRows();
    MarkerMap markerMap = new MarkerMap(normalizer.getMarkers());
    guiLogger.debugf(() -> format("Will use these BOLD-to-Naturalis marker mappings: %s", toJson(markerMap)));
    StoredDocumentTable<BoldKey> selectedDocuments = createLookupTableForSelectedDocuments1();
    StoredDocumentList updated = new StoredDocumentList(selectedDocuments.size());
    int good = 0, bad = 0, unused = 0;
    NaturalisNote note;
    for(String marker : allRows.keySet()) {
      guiLogger.info("Processing marker \"%s\"", marker);
      List<String[]> rows = allRows.get(marker);
      for(int i = 0; i < rows.size(); ++i) {
        /*
         * During normalization header lines were stripped off, so we need to add them again to make the user understand what
         * line we are talking about.
         */
        guiLogger.debug("Line %s: %s", i + cfg.getSkipLines() + 1, toJson(rows.get(i)));
        BoldRow row = new BoldRow(cfg.getColumnNumbers(), rows.get(i));
        if((note = createNote(row, i)) == null) {
          ++bad;
          continue;
        }
        ++good;
        String regno = row.get(SAMPLE_ID);
        String boldMarker = row.get(MARKER);
        List<BoldKey> keys;
        if(boldMarker == null) {
          keys = Arrays.asList(new BoldKey(regno, BoldKey.NO_MARKER));
        } else {
          keys = Arrays.stream(markerMap.get(boldMarker)).map(m -> new BoldKey(regno, m)).collect(toList());
        }
        /*
         * BOLD marker may map to multiple Naturalis markers. Only if none of the Naturalis markers is found within the selected
         * documents will the row in the BOLD file remain unused.
         */
        boolean used = false;
        for(BoldKey key : keys) {
          Messages.scanningSelectedDocuments(guiLogger, "key", key);
          StoredDocumentList docs = selectedDocuments.get(key);
          if(docs != null) {
            Messages.foundDocumensMatchingKey(guiLogger, "BOLD file", docs);
            used = true;
            for(StoredDocument doc : docs) {
              NaturalisNote finalNote;
              if(doc.isDummy() && key.getMarker() != BoldKey.NO_MARKER) {
                finalNote = new NaturalisNote(note);
                finalNote.remove(BOLD_NUCLEOTIDE_LENGTH, BOLD_NUM_TRACES, BOLD_GEN_BANK_ID, BOLD_GEN_BANK_URI);
              } else {
                finalNote = note;
              }
              if(doc.attach(finalNote)) {
                updated.add(doc);
              } else {
                Messages.noNewValues(guiLogger, "BOLD file", "key", key);
              }
            }
          }
        }
        if(!used) {
          Messages.noDocumentsMatchingKey(guiLogger, i + cfg.getSkipLines() + 1);
          ++unused;
        }
      }
    }

    updated.forEach(StoredDocument::saveAnnotations);
    List<AnnotatedPluginDocument> all = updated.unwrap();
    all.addAll(updated.unwrap());
    if(!all.isEmpty()) {
      all = addAndReturnGeneratedDocuments(all, true, Collections.emptyList());
    }

    new CommonStatistics()
        .rowStats(good, bad, unused)
        .docStats(cfg.getSelectedDocuments().size(), updated.size())
        .print(guiLogger);

    guiLogger.info("UNUSED ROW (explanation): The row's registration number and marker");
    guiLogger.info("          did not correspond to any of the selected documents, but");
    guiLogger.info("          they may or may not correspond to other, unselected documents.");
    Messages.operationCompletedSuccessfully(guiLogger, "BOLD Import");
    return all;
  }

  @Override
  protected String getLogTitle() {
    return "BOLD Import";
  }

  private NaturalisNote createNote(BoldRow row, int rownum) {
    int line = rownum + cfg.getSkipLines() + 1;
    if(row.get(SAMPLE_ID) == null) {
      guiLogger.error(InvalidRowException.MSG_MISSING_VALUE, line, SAMPLE_ID);
      return null;
    }
    BoldNoteFactory factory = new BoldNoteFactory(rownum + cfg.getSkipLines() + 1, row);
    try {
      NaturalisNote note = factory.createNote();
      guiLogger.debugf(() -> format("Note created: %s", toJson(note)));
      return note;
    } catch(InvalidRowException e) {
      guiLogger.error(e.getMessage());
      return null;
    }
  }

  private StoredDocumentTable<BoldKey> createLookupTableForSelectedDocuments1() {
    return new StoredDocumentTable<>(cfg.getSelectedDocuments(), this::getCompoundKey);
  }

  private BoldKey getCompoundKey(StoredDocument sd) {
    String regno = sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
    if(regno == null) {
      guiLogger.info("Ignoring selected document %s: missing registration number");
      return null; // i.e. do not add to lookup table
    }
    String marker = sd.getNaturalisNote().get(SEQ_MARKER);
    if(marker == null) {
      guiLogger.error("Corrupt %s document: %s. Document has registration number but no marker",
          sd.getType(),
          sd.getGeneiousDocument().getName());
      return null;
    }
    return new BoldKey(regno, marker);
  }

}
