package nl.naturalis.geneious.bold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.common.base.BiFunctions;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.Messages.Debug;
import nl.naturalis.geneious.util.Messages.Warn;

import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;

/**
 * A lookup table for the documents selected by the user, initially keyed on the combination of CRS registration number and marker, and
 * finally on the CRS registration number. As soon as a document gets annotated using a row in the BOLD spreadsheet it is removed from the
 * lookup table. This ensure that documents don't get updated more than once. There is no danger of data corruption if that were to happen,
 * because the document would always get updated with the same data, but it is wasteful and makes for confusing log messages.
 * 
 * @author Ayco Holleman
 *
 */
class BoldLookupTable extends HashMap<BoldKey, ArrayList<StoredDocument>> {

  private static final GuiLogger logger = GuiLogManager.getLogger(BoldLookupTable.class);

  /**
   * Returns a document lookup table keyed on the combination of the CRS registration number and marker. In other words the returned lookup
   * table can be used to find a "strong" correspondence between a document and a row in a BOLD spreadsheet.
   * 
   * @param selectedDocuments
   * @param markerMap
   * @return
   */
  static BoldLookupTable newInstance(List<AnnotatedPluginDocument> selectedDocuments, MarkerMap markerMap) {
    BoldLookupTable tbl = new BoldLookupTable();
    selectedDocuments.stream().map(StoredDocument::new).forEach(sd -> {
      BoldKey key = getCompoundKey(sd, markerMap);
      if (key != null) {
        tbl.computeIfAbsent(key, (k) -> new ArrayList<>(8)).add(sd);
      }
    });
    return tbl;
  }

  private BoldLookupTable() {}

  /**
   * Returns a new document lookup table keyed on just the CRS registration number. The importer will first attempt to match rows to
   * documents using a compound key consisting of registration number and marker. Matching documents will be exhaustively updated from the
   * row and then removed from the lookup table. If, after all markers have been processed, there are still documents in the lookup table,
   * the importer will attempt to match on registration number only. Matching documents will only acquire the specimen-related annotations
   * extracted from the row. Marker-related annotations will be ignored.
   * 
   * @return
   */
  BoldLookupTable rebuildWithPartialKey() {
    BoldLookupTable tbl = new BoldLookupTable();
    entrySet().forEach(e -> tbl.merge(new BoldKey(e.getKey().getRegno()), e.getValue(), BiFunctions::concat));
    return tbl;
  }

  private static BoldKey getCompoundKey(StoredDocument sd, MarkerMap markerMap) {
    String regno = sd.getNaturalisNote().get(SMPL_REGISTRATION_NUMBER);
    if (regno == null) {
      Debug.ignoringSelectedDocument(logger, sd, "Missing CRS registration number");
      return null; // do not add to lookup table
    }
    if (sd.isDummy()) {
      return new BoldKey(regno); // Dummies always matched on registration number only
    }
    String naturalisMarker = sd.getNaturalisNote().get(SEQ_MARKER);
    if (naturalisMarker == null) {
      Warn.corruptDocument(logger, sd, "Has registration number but no marker");
      return null;
    }
    String boldMarker = markerMap.get(naturalisMarker);
    if (boldMarker == null) {
      String fmt = "Unknown marker \"%s\" in document %s (go to Tools -> Preferences to add a mapping for this marker)";
      logger.warn(fmt, sd.getName(), naturalisMarker, sd.getName());
      return new BoldKey(regno);
    }
    return new BoldKey(regno, boldMarker);
  }

}
