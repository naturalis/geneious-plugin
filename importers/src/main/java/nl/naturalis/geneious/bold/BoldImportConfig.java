package nl.naturalis.geneious.bold;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.CsvImportConfig;

/**
 * Contains the user input for BOLD imports.
 *
 * @author Ayco Holleman
 */
class BoldImportConfig extends CsvImportConfig<BoldColumn> {

  /**
   * Return the column-name-to-column-number mapping.
   */
  @Override
  public EnumToIntMap<BoldColumn> getColumnNumbers() {
    // Column numbers correspond exactly to ordinal values (after normalization!)
    return new EnumToIntMap<>(BoldColumn.class, Enum::ordinal);
  }

}
