package nl.naturalis.geneious.crs;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.RowSupplierConfig;

import static nl.naturalis.geneious.crs.CrsColumn.AGENT;
import static nl.naturalis.geneious.crs.CrsColumn.ALTITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.COLLECTING_START_DATE;
import static nl.naturalis.geneious.crs.CrsColumn.COUNTRY;
import static nl.naturalis.geneious.crs.CrsColumn.FULL_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.crs.CrsColumn.GENUS_OR_MONOMIAL;
import static nl.naturalis.geneious.crs.CrsColumn.IDENTIFIED_BY;
import static nl.naturalis.geneious.crs.CrsColumn.LATTITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.LOCALITY;
import static nl.naturalis.geneious.crs.CrsColumn.LONGITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.HIGHER_NAMES;
import static nl.naturalis.geneious.crs.CrsColumn.PHASE_OR_STAGE;
import static nl.naturalis.geneious.crs.CrsColumn.HIGHER_RANKS;
import static nl.naturalis.geneious.crs.CrsColumn.REGISTRATION_NUMBER;
import static nl.naturalis.geneious.crs.CrsColumn.SEX;
import static nl.naturalis.geneious.crs.CrsColumn.STATE_OR_PROVINCE;

/**
 * Stores the input provided provided by the user via the options dialog.
 *
 * @author Ayco Holleman
 */
class CrsImportConfig extends RowSupplierConfig {

  CrsImportConfig(List<AnnotatedPluginDocument> selectedDocuments) {
    super(selectedDocuments);
  }

  EnumToIntMap<CrsColumn> getColumnNumbers() {
    return new EnumToIntMap<>(CrsColumn.class)
        .set(REGISTRATION_NUMBER, 0)
        .set(HIGHER_RANKS, 1)
        .set(HIGHER_NAMES, 2)
        .set(GENUS_OR_MONOMIAL, 3)
        .set(FULL_SCIENTIFIC_NAME, 4)
        .set(IDENTIFIED_BY, 5)
        .set(SEX, 6)
        .set(PHASE_OR_STAGE, 7)
        .set(AGENT, 8)
        .set(COLLECTING_START_DATE, 9)
        .set(COUNTRY, 10)
        .set(STATE_OR_PROVINCE, 11)
        .set(LOCALITY, 12)
        .set(LATTITUDE, 13)
        .set(LONGITUDE, 14)
        .set(ALTITUDE, 15);
  }

}
