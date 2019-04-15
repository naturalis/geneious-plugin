package nl.naturalis.geneious.smpl;

import java.util.regex.Pattern;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.note.NaturalisField.SMPL_AMPLIFICATION_STAFF;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACTION_METHOD;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_PLATE_POSITION;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.EXTRACTION_METHOD;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.EXTRACT_ID;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.PLATE_POSITION;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.REGISTRATION_NUMBER;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.SCIENTIFIC_NAME;

/**
 * Produces a {@link NaturalisNote} from the data in a {@link SampleSheetRow}.
 */
class SmplNoteFactory extends NoteFactory<SampleSheetColumn> {

  private static final Pattern PT_EXTRACT_ID = Pattern.compile("^\\d{4,16}$");

  private static final String CONSTANT_VALUE_AMPL_STAFF = "Naturalis Biodiversity Center Laboratories";

  SmplNoteFactory(int rownum, SampleSheetRow row) {
    super(rownum, row);
  }

  @Override
  protected void populate(NaturalisNote note) throws InvalidRowException {
    setRequiredValue(note, SMPL_EXTRACT_PLATE_ID, EXTRACT_PLATE_ID);
    setRequiredValue(note, SMPL_PLATE_POSITION, PLATE_POSITION);
    setRequiredValue(note, SMPL_EXTRACT_ID, EXTRACT_ID, this::getExtractId);
    setRequiredValue(note, SMPL_SAMPLE_PLATE_ID, SAMPLE_PLATE_ID, this::getExtractPlateId);
    setValue(note, SMPL_REGISTRATION_NUMBER, REGISTRATION_NUMBER);
    setValue(note, SMPL_SCIENTIFIC_NAME, SCIENTIFIC_NAME);
    setValue(note, SMPL_EXTRACTION_METHOD, EXTRACTION_METHOD);
    note.castAndSet(SMPL_AMPLIFICATION_STAFF, CONSTANT_VALUE_AMPL_STAFF);
  }

  private String getExtractPlateId(String val) throws InvalidRowException {
    int i = val.indexOf('-');
    if (i == -1) {
      throw InvalidRowException.custom(this, "missing hyphen ('-') in value for %s: %s", SMPL_SAMPLE_PLATE_ID, val);
    }
    return val.substring(0, i);
  }

  private String getExtractId(String val) throws InvalidRowException {
    if (PT_EXTRACT_ID.matcher(val).matches()) {
      return "e" + val;
    }
    throw InvalidRowException.custom(this, "Invalid extract ID: \"%s\"", val);
  }

}
