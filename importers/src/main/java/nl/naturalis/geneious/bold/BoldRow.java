package nl.naturalis.geneious.bold;

import static nl.naturalis.geneious.note.NaturalisField.*;
import org.apache.commons.lang3.StringUtils;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

class BoldRow {
  
  private static final String ERR_BASE = "Invalid record in sample sheet: %s. ";
  private static final String ERR_MISSING_VALUE = ERR_BASE + "Missing value for field %s";
  private static final String ERR_INVALID_VALUE = ERR_BASE + "Ivalid value for field %s: \"%s\"";

  private final int rowNum;
  private final String[] row;

  BoldRow(int rowNum, String[] row) {
    this.rowNum = rowNum;
    this.row = row;
  }

  boolean isEmpty() {
    for (String s : row) {
      if (!StringUtils.isBlank(s)) {
        return false;
      }
    }
    return true;
  }

  NaturalisNote extractNote() throws SampleSheetRowException {
    if (row.length < 7) {
      String fmt = ERR_BASE + "Too few fields (%s). Expected at least 7.";
      String msg = String.format(fmt, rowNum, row.length);
      throw new SampleSheetRowException(msg);
    }
    if(StringUtils.isBlank(row[0])) {
      throw missingValue(SMPL_EXTRACT_PLATE_ID);
    }
    if(StringUtils.isBlank(row[1])) {
      throw missingValue(SMPL_PLATE_POSITION);
    }
    if(StringUtils.isBlank(row[2])) {
      throw missingValue(SMPL_SAMPLE_PLATE_ID);
    }
    if(StringUtils.isBlank(row[3])) {
      throw missingValue(SMPL_EXTRACT_ID);
    }
    NaturalisNote note = new NaturalisNote();
//    note.setExtractPlateId(row[0]);
//    note.setPlatePosition(row[1]);
//    int i = row[2].indexOf('-');
//    if (i == -1) {
//       throw invalidValue(SMPL_SAMPLE_PLATE_ID, row[2]);
//    }
//    note.setSamplePlateId(row[2].substring(0, i));
//    note.setExtractId("e" + row[3]);
//    note.setRegistrationNumber(row[4]);
//    note.setScientificName(row[5]);
//    note.setExtractionMethod(row[6]);
    return note;
  }
  
  private SampleSheetRowException missingValue(NaturalisField field) {
    String msg = String.format(ERR_MISSING_VALUE, rowNum, field.getName());
    return new SampleSheetRowException(msg);
  }
  
  @SuppressWarnings("unused")
  private SampleSheetRowException invalidValue(NaturalisField field, Object val) {
    String msg = String.format(ERR_INVALID_VALUE, rowNum, field.getName(), val);
    return new SampleSheetRowException(msg);
  }

}
