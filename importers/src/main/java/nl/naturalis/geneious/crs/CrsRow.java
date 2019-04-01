package nl.naturalis.geneious.crs;

import java.util.EnumMap;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.collection.EnumToIntMap;

/**
 * Contains the values for a single row in a CRS file.
 *
 * @author Ayco Holleman
 */
class CrsRow extends EnumMap<CrsColumn, String> {

  CrsRow(EnumToIntMap<CrsColumn> columnNumbers, String[] columnValues) {
    super(CrsColumn.class);
    for (CrsColumn col : columnNumbers.keySet()) {
      int colnum = columnNumbers.get(col);
      if (colnum < columnValues.length) {
        put(col, StringUtils.trimToNull(columnValues[colnum]));
      }
    }
  }

  boolean isEmptyRow() {
    return values().stream().allMatch(s -> s != null);
  }

  boolean hasValueFor(CrsColumn column) {
    return get(column) != null;
  }

}
