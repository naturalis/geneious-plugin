package nl.naturalis.geneious.samplesheet;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

import static org.apache.commons.lang3.StringUtils.isBlank;

import static nl.naturalis.geneious.note.NaturalisField.EXTRACTION_METHOD;
import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.PLATE_POSITION;
import static nl.naturalis.geneious.note.NaturalisField.REGISTRATION_NUMBER;
import static nl.naturalis.geneious.note.NaturalisField.SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SCIENTIFIC_NAME;

/**
 * Represents a single row within a sample sheet and functions as a producer of {@link NaturalisNote} instances.
 */
class SampleSheetRow {

  /**
   * Column containing the extract plate ID (0).
   */
  static final int COL_EXTRACT_PLATE_ID = 0;
  /**
   * Column containing the plate position (1).
   */
  static final int COL_PLATE_POSITION = 1;
  /**
   * Column containing the sample plate ID (2).
   */
  static final int COL_SAMPLE_PLATE_ID = 2;
  /**
   * Column containing the extract ID (3).
   */
  static final int COL_EXTRACT_ID = 3;
  /**
   * Column containing the registration number (4).
   */
  static final int COL_REG_NO = 4;
  /**
   * Column containing the scientific name (5).
   */
  static final int COL_SCI_NAME = 5;
  /**
   * Column containing the extraction method (6).
   */
  static final int COL_EXTRACTION_METHOD = 6;

  /**
   * Returns the column number in which the specied field can be found.
   * 
   * @param nf
   * @return
   */
  static int getColumnNumber(NaturalisField nf) {
    Preconditions.checkArgument(cols.containsKey(nf), "Not a sample sheet field: %s", nf);
    return cols.get(nf).intValue();
  }

  private static final EnumMap<NaturalisField, Integer> cols = new EnumMap<>(NaturalisField.class);

  static {
    cols.put(EXTRACT_PLATE_ID, COL_EXTRACT_PLATE_ID);
    cols.put(PLATE_POSITION, COL_PLATE_POSITION);
    cols.put(SAMPLE_PLATE_ID, COL_SAMPLE_PLATE_ID);
    cols.put(EXTRACT_ID, COL_EXTRACT_ID);
    cols.put(REGISTRATION_NUMBER, COL_REG_NO);
    cols.put(SCIENTIFIC_NAME, COL_SCI_NAME);
    cols.put(EXTRACTION_METHOD, COL_EXTRACTION_METHOD);
  }

  private static final EnumSet<NaturalisField> required = EnumSet.of(
      EXTRACT_PLATE_ID,
      PLATE_POSITION,
      SAMPLE_PLATE_ID,
      EXTRACT_ID);

  private static final String ERR_BASE = "Invalid record in sample sheet: %s. ";
  private static final String ERR_CELL_COUNT = ERR_BASE + "Too few fields (%s). Row must have at least %s fields.";
  private static final String ERR_MISSING_VALUE = ERR_BASE + "Missing value for field %s";
  private static final String ERR_INVALID_VALUE = ERR_BASE + "Invalid value for field %s: \"%s\"";

  private final int rowNum;
  private final String[] cells;

  /**
   * Creates a SampleSheetRow for the specied row number in the sample sheet containing the specified cell values.
   * 
   * @param rowNum
   * @param cells
   */
  SampleSheetRow(int rowNum, String[] cells) {
    this.rowNum = rowNum;
    this.cells = cells;
  }

  /**
   * Whether or not this is an empty row (all of its cells contain whitespace only).
   * 
   * @param rowNum
   * @param cells
   */
  boolean isEmpty() {
    return !Arrays.stream(cells).filter(StringUtils::isNotBlank).findFirst().isPresent();
  }

  /**
   * Converts the sample sheet row into a NaturalisNote instance.
   * 
   * @return
   * @throws InvalidRowException
   */
  NaturalisNote extractNote() throws InvalidRowException {
    String[] cells;
    if ((cells = this.cells).length < 7) {
      throw new InvalidRowException(String.format(ERR_CELL_COUNT, rowNum, cells.length, 7));
    }
    for (NaturalisField nf : required) {
      if (isBlank(get(nf))) {
        throw missingValue(nf);
      }
    }
    NaturalisNote note = new NaturalisNote();
    note.setExtractPlateId(get(EXTRACT_PLATE_ID));
    note.setPlatePosition(get(PLATE_POSITION));
    int i = get(SAMPLE_PLATE_ID).indexOf('-');
    if (i == -1) {
      throw invalidValue(SAMPLE_PLATE_ID, cells[COL_SAMPLE_PLATE_ID]);
    }
    note.setSamplePlateId(get(SAMPLE_PLATE_ID).substring(0, i));
    note.setExtractId("e" + get(EXTRACT_ID));
    note.setRegistrationNumber(get(REGISTRATION_NUMBER));
    note.setScientificName(get(SCIENTIFIC_NAME));
    note.setExtractionMethod(get(EXTRACTION_METHOD));
    return note;
  }

  private String get(NaturalisField nf) {
    return cells[cols.get(nf)];
  }

  private InvalidRowException missingValue(NaturalisField field) {
    String msg = String.format(ERR_MISSING_VALUE, rowNum, field.getName());
    return new InvalidRowException(msg);
  }

  private InvalidRowException invalidValue(NaturalisField field, Object val) {
    String msg = String.format(ERR_INVALID_VALUE, rowNum, field.getName(), val);
    return new InvalidRowException(msg);
  }

}
