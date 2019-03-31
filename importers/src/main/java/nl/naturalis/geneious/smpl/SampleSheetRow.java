package nl.naturalis.geneious.smpl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.note.NaturalisField;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACTION_METHOD;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_PLATE_POSITION;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SCIENTIFIC_NAME;

/**
 * Represents a single row within a sample sheet and functions as a producer of {@link NaturalisNote} instances.
 */
class SampleSheetRow {

  static final int COLNO_EXTRACT_PLATE_ID = 0;
  static final int COLNO_PLATE_POSITION = 1;
  static final int COLNO_SAMPLE_PLATE_ID = 2;
  static final int COLNO_EXTRACT_ID = 3;
  static final int COLNO_REGISTRATION_NUMBER = 4;
  static final int COLNO_SCIENTIFIC_NAME = 5;
  static final int COLNO_EXTRACTION_METHOD = 6;

  private static final int MIN_CELL_COUNT = 6;

  private static final String ERR_BASE = "Invalid row at line %s. ";
  private static final String ERR_CELL_COUNT = ERR_BASE + "Invalid number of columns: %s";
  private static final String ERR_MISSING_VALUE = ERR_BASE + "Missing value for %s";
  private static final String ERR_INVALID_VALUE = ERR_BASE + "Invalid value for %s: \"%s\". Reason: %s";

  private final int rowNum;
  private final String[] cells;

  /**
   * Creates a SampleSheetRow for the specied row number and cell values.
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
    if (cells.length < MIN_CELL_COUNT) {
      throw invalidColumnCount(rowNum, cells);
    }
    NaturalisNote note = new NaturalisNote();
    String extractId = processExtractId();
    note.parseAndSet(SMPL_EXTRACT_ID, extractId);
    note.parseAndSet(SMPL_SAMPLE_PLATE_ID, processSamplePlateId());
    note.parseAndSet(SMPL_EXTRACT_PLATE_ID, processExtractPlateId());
    note.parseAndSet(SMPL_PLATE_POSITION, processPlatePosition());
    setIfPresent(note, SMPL_SCIENTIFIC_NAME, COLNO_SCIENTIFIC_NAME);
    setIfPresent(note, SMPL_REGISTRATION_NUMBER, COLNO_REGISTRATION_NUMBER);
    setIfPresent(note, SMPL_EXTRACTION_METHOD, COLNO_EXTRACTION_METHOD);
    return note;
  }

  private String processExtractId() throws InvalidRowException {
    String val = StringUtils.trimToNull(cells[COLNO_EXTRACT_ID]);
    if (val == null) {
      throw missingValue(SMPL_EXTRACT_ID);
    }
    return "e" + val;
  }

  private String processSamplePlateId() throws InvalidRowException {
    String val = StringUtils.trimToNull(cells[COLNO_EXTRACT_PLATE_ID]);
    if (val == null) {
      throw missingValue(SMPL_SAMPLE_PLATE_ID);
    }
    int i = val.indexOf('-');
    if (i == -1) {
      throw invalidValue(SMPL_SAMPLE_PLATE_ID, cells[COLNO_SAMPLE_PLATE_ID], "missing hyphen ('-')");
    }
    return val.substring(0, i);
  }

  private String processExtractPlateId() throws InvalidRowException {
    String val = StringUtils.trimToNull(cells[COLNO_EXTRACT_PLATE_ID]);
    if (val == null) {
      throw missingValue(SMPL_EXTRACT_PLATE_ID);
    }
    return val;
  }

  private String processPlatePosition() throws InvalidRowException {
    String val = StringUtils.trimToNull(cells[COLNO_PLATE_POSITION]);
    if (val == null) {
      throw missingValue(SMPL_PLATE_POSITION);
    }
    return val;
  }

  private void setIfPresent(NaturalisNote note, NaturalisField field, int column) {
    String val = StringUtils.trimToNull(cells[column]);
    if (val != null) {
      note.parseAndSet(field, val);
    }
  }

  private static InvalidRowException invalidColumnCount(int rowNum, String[] cells) {
    return new InvalidRowException(String.format(ERR_CELL_COUNT, rowNum, cells.length));
  }

  private InvalidRowException missingValue(NaturalisField field) {
    String msg = String.format(ERR_MISSING_VALUE, rowNum, field.getName());
    return new InvalidRowException(msg);
  }

  private InvalidRowException invalidValue(NaturalisField field, Object val, String reason) {
    String msg = String.format(ERR_INVALID_VALUE, rowNum, field.getName(), val, reason);
    return new InvalidRowException(msg);
  }

}
