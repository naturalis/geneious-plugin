package nl.naturalis.geneious.bold;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.Row;

/**
 * Contains the values for a single row in a normalized BOLD file.
 * 
 * @see BoldNormalizer
 *
 * @author Ayco Holleman
 */
class BoldRow extends Row<BoldColumn> {

  BoldRow(EnumToIntMap<BoldColumn> columnNumbers, String[] columnValues) {
    super(BoldColumn.class, columnNumbers, columnValues);
  }

}
