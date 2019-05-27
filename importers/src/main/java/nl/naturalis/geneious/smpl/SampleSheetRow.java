package nl.naturalis.geneious.smpl;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.Row;

/**
 * Represents one row in a sample sheet.
 * 
 * @author Ayco Holleman
 *
 */
class SampleSheetRow extends Row<SampleSheetColumn> {

  SampleSheetRow(EnumToIntMap<SampleSheetColumn> columnNumbers, String[] columnValues) {
    super(SampleSheetColumn.class, columnNumbers, columnValues);
  }

}
