package nl.naturalis.geneious.crs;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.CsvImportConfig;

import static nl.naturalis.geneious.crs.CrsColumn.COL_AGENT;
import static nl.naturalis.geneious.crs.CrsColumn.COL_ALTITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.COL_COLLECTING_START_DATE;
import static nl.naturalis.geneious.crs.CrsColumn.COL_COUNTRY;
import static nl.naturalis.geneious.crs.CrsColumn.COL_FULL_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.crs.CrsColumn.COL_GENUS_OR_MONOMIAL;
import static nl.naturalis.geneious.crs.CrsColumn.COL_HIGHER_NAMES;
import static nl.naturalis.geneious.crs.CrsColumn.COL_HIGHER_RANKS;
import static nl.naturalis.geneious.crs.CrsColumn.COL_IDENTIFIED_BY;
import static nl.naturalis.geneious.crs.CrsColumn.COL_LATTITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.COL_LOCALITY;
import static nl.naturalis.geneious.crs.CrsColumn.COL_LONGITUDE;
import static nl.naturalis.geneious.crs.CrsColumn.COL_PHASE_OR_STAGE;
import static nl.naturalis.geneious.crs.CrsColumn.COL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.crs.CrsColumn.COL_SEX;
import static nl.naturalis.geneious.crs.CrsColumn.COL_STATE_OR_PROVINCE;

/**
 * Contains the user input for CRS imports.
 *
 * @author Ayco Holleman
 */
class CrsImportConfig extends CsvImportConfig<CrsColumn> {

  CrsImportConfig() {
    super(); // do not leave this out!
  }

  /**
   * Returns the column-name-to-column-number mapping.
   */
  @Override
  public EnumToIntMap<CrsColumn> getColumnNumbers() {
    return new EnumToIntMap<>(CrsColumn.class)
        .set(COL_REGISTRATION_NUMBER, 0)
        .set(COL_HIGHER_RANKS, 1)
        .set(COL_HIGHER_NAMES, 2)
        .set(COL_GENUS_OR_MONOMIAL, 3)
        .set(COL_FULL_SCIENTIFIC_NAME, 4)
        .set(COL_IDENTIFIED_BY, 5)
        .set(COL_SEX, 6)
        .set(COL_PHASE_OR_STAGE, 7)
        .set(COL_AGENT, 8)
        .set(COL_COLLECTING_START_DATE, 9)
        .set(COL_COUNTRY, 10)
        .set(COL_STATE_OR_PROVINCE, 11)
        .set(COL_LOCALITY, 12)
        .set(COL_LATTITUDE, 13)
        .set(COL_LONGITUDE, 14)
        .set(COL_ALTITUDE, 15);
  }

  @Override
  public String getOperationName() {
    return CrsDocumentOperation.NAME;
  }

}
