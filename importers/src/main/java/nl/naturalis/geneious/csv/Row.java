package nl.naturalis.geneious.csv;

import java.util.EnumMap;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.common.collection.EnumToIntMap;

/**
 * Contains the data for a single row within a CSV-like file. The {@code Row} class is structured as a map
 * with symbolic column names (like {@code EXTRACT_ID}) mapping to actual column numbers.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 */
public class Row<T extends Enum<T>> extends EnumMap<T, String> {

  public Row(Class<T> keyType, EnumToIntMap<T> columnNumbers, String[] columnValues) {
    super(keyType);
    for (T col : columnNumbers.keySet()) {
      int colnum = columnNumbers.get(col);
      if (colnum < columnValues.length) {
        put(col, StringUtils.trimToNull(columnValues[colnum]));
      }
    }
  }

  /**
   * Whether or not the rows contains at least one non-null value.
   */
  public boolean isEmpty() {
    return values().stream().allMatch(Objects::isNull);
  }

  /**
   * Whether or not the row has values for <i>all of</i> the provided columns.
   * 
   * @param columns
   * @return
   */
  @SuppressWarnings("unchecked")
  public boolean hasValueFor(T... columns) {
    for (T column : columns) {
      if (get(column) == null) {
        return false;
      }
    }
    return true;
  }
}
