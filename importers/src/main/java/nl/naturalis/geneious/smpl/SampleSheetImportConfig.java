package nl.naturalis.geneious.smpl;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.CsvImportConfig;

/**
 * Contains configuration settings for sample sheet imports.
 *
 * @author Ayco Holleman
 */
public class SampleSheetImportConfig extends CsvImportConfig<SampleSheetColumn> {

  private boolean createDummies;

  @Override
  public EnumToIntMap<SampleSheetColumn> getColumnNumbers() {
    // Column numbers correspond exactly to ordinal values
    return new EnumToIntMap<>(SampleSheetColumn.class, Enum::ordinal);
  }

  boolean isCreateDummies() {
    return createDummies;
  }

  void setCreateDummies(boolean createDummies) {
    this.createDummies = createDummies;
  }

}
