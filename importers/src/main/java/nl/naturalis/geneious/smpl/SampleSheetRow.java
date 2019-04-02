package nl.naturalis.geneious.smpl;

import nl.naturalis.common.collection.EnumToIntMap;
import nl.naturalis.geneious.csv.Row;

class SampleSheetRow extends Row<SampleSheetColumn> {

  SampleSheetRow(EnumToIntMap<SampleSheetColumn> columnNumbers, String[] columnValues) {
    super(SampleSheetColumn.class, columnNumbers, columnValues);
  }

}
