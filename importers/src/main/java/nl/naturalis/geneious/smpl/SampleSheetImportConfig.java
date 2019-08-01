package nl.naturalis.geneious.smpl;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.CsvImportConfig;

/**
 * Contains the user input for sample sheet imports.
 *
 * @author Ayco Holleman
 */
public class SampleSheetImportConfig extends CsvImportConfig<SampleSheetColumn> {

  private boolean createDummies;

  SampleSheetImportConfig() {
    super(); // do not leave this out!
  }

  /**
   * Return the column-name-to-column-number mapping.
   */
  @Override
  public EnumToIntMap<SampleSheetColumn> getColumnNumbers() {
    // Column numbers correspond exactly to ordinal values
    return new EnumToIntMap<>(SampleSheetColumn.class, Enum::ordinal);
  }

  /**
   * Whether or not the user requested to create dummies for new extract IDs.
   * 
   * @return
   */
  boolean isCreateDummies() {
    return createDummies;
  }

  /**
   * Sets whether or not the user requested to create dummies for new extract IDs.
   * 
   * @param createDummies
   */
  void setCreateDummies(boolean createDummies) {
    this.createDummies = createDummies;
  }

}
