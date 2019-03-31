package nl.naturalis.geneious.crs;

import java.util.EnumMap;

import nl.naturalis.common.collection.EnumToIntMap;

class CrsRow extends EnumMap<CrsColumn, String> {

  CrsRow(EnumToIntMap<CrsColumn> columnNumbers, String[] columnValues) {
    super(CrsColumn.class);
    for (CrsColumn col : columnNumbers.keys()) {
      int colnum = columnNumbers.get(col);
      if (colnum < columnValues.length) {
        put(col, columnValues[colnum]);
      }
    }
  }

}
