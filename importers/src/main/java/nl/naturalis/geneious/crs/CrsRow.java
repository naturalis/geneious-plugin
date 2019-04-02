package nl.naturalis.geneious.crs;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.Row;

/**
 * Contains the values for a single row in a CRS file.
 *
 * @author Ayco Holleman
 */
class CrsRow extends Row<CrsColumn> {

  CrsRow(EnumToIntMap<CrsColumn> columnNumbers, String[] columnValues) {
    super(CrsColumn.class, columnNumbers, columnValues);
  }

}
