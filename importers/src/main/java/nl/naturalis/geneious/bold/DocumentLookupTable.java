package nl.naturalis.geneious.bold;

import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.common.base.BiFunctions;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;

/**
 * A cache of selected documents enabling fast lookups. As soon as a document gets annotated using a row in the BOLD
 * spreadsheet it is removed from the lookup table. This ensure that documents don't get updated more than once. There
 * is no danger of data corruption if that were to happen, because the document would alsways get updated with the same
 * data, but it is wasteful and makes for confusing log messages.
 * 
 * @author Ayco Holleman
 *
 */
class DocumentLookupTable extends HashMap<BoldKey, ArrayList<StoredDocument>> {

  private static final GuiLogger logger = GuiLogManager.getLogger(DocumentLookupTable.class);

  /**
   * Returns a document lookup table keyed on the combination of the CRS registration number and marker. In other words
   * the returned lookup table can be used to find a "strong" correspondence between a document and a row in a BOLD
   * spreadsheet.
   * 
   * @param selectedDocuments
   * @param markerMap
   * @return
   */
  static DocumentLookupTable newInstance(List<AnnotatedPluginDocument> selectedDocuments, MarkerMap markerMap) {
    DocumentLookupTable tbl = new DocumentLookupTable();
    selectedDocuments.stream().map(StoredDocument::new).forEach(sd -> {
      BoldKey key = getCompoundKey(sd, markerMap);
      if(key != null) {
        tbl.computeIfAbsent(key, (k) -> new ArrayList<>(8)).add(sd);
      }
    });
    return tbl;
  }

  private DocumentLookupTable() {}

  /**
   * Returns a new document lookup table keyed on just the CRS registration number.
   * 
   * @return
   */
  DocumentLookupTable rebuildWithPartialKey() {
    DocumentLookupTable tbl = new DocumentLookupTable();
    entrySet().forEach(e -> tbl.merge(new BoldKey(e.getKey().getRegno()), e.getValue(), BiFunctions::append));
    return tbl;
  }

  private static BoldKey getCompoundKey(StoredDocument sd, MarkerMap markerMap) {
    String regno = sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
    if(regno == null) {
      logger.info("Ignoring %s: missing CRS registration number", sd.getName());
      return null; // means: do not add to lookup table
    }
    if(sd.isDummy()) {
      // Dummies will always be matched on registration number only (having a dummy marker "Dum", which BOLD knows nothing
      // about). And they will also always be enriched using just the specimen-related columns within a BOLD row.
      return new BoldKey(regno);
    }
    String naturalisMarker = sd.getNaturalisNote().get(SEQ_MARKER);
    if(naturalisMarker == null) {
      logger.error("Ignoring %s: corrupt document (has registration number but no marker)", sd.getName());
      return null;
    }
    String boldMarker = markerMap.get(naturalisMarker);
    if(boldMarker == null) {
      String fmt = "Ignoring %s: unknown marker \"%s\" (go to Tools -> Preferences to add a mapping for this marker)";
      logger.error(fmt, sd.getName(), naturalisMarker);
      return null;
    }
    return new BoldKey(regno, boldMarker);
  }

}
