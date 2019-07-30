package nl.naturalis.geneious.smpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.note.NaturalisNote;

import static java.util.stream.Collectors.joining;

import static nl.naturalis.geneious.note.NaturalisField.SMPL_AMPLIFICATION_STAFF;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACTION_METHOD;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_PLATE_POSITION;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGNO_PLUS_SCI_NAME;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_EXTRACTION_METHOD;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_EXTRACT_ID;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_PLATE_POSITION;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.smpl.SampleSheetColumn.COL_SCIENTIFIC_NAME;

/**
 * A factory of {@code NaturalisNote} instances specialized in populating the sample sheet-related fields of a {@code NaturalisNote}.
 */
class SmplNoteFactory extends NoteFactory<SampleSheetColumn> {

  private static final Pattern PTTRN_EXTRACT_ID = Pattern.compile("^\\d{4,16}$");
  private static final String AMPL_STAFF = "Naturalis Biodiversity Center Laboratories";
  private static final String ERR_MISSING_HYPHEN = "missing hyphen ('-') in value for %s: %s";

  SmplNoteFactory(SampleSheetRow row, int line) {
    super(row, line);
  }

  @Override
  protected void populate(NaturalisNote note) throws InvalidRowException {
    setRequiredValue(note, SMPL_EXTRACT_PLATE_ID, COL_EXTRACT_PLATE_ID, this::getExtractPlateId);
    setRequiredValue(note, SMPL_PLATE_POSITION, COL_PLATE_POSITION);
    setRequiredValue(note, SMPL_EXTRACT_ID, COL_EXTRACT_ID, this::getExtractId);
    setRequiredValue(note, SMPL_SAMPLE_PLATE_ID, COL_SAMPLE_PLATE_ID);
    setValue(note, SMPL_REGISTRATION_NUMBER, COL_REGISTRATION_NUMBER);
    setValue(note, SMPL_SCIENTIFIC_NAME, COL_SCIENTIFIC_NAME);
    setValue(note, SMPL_EXTRACTION_METHOD, COL_EXTRACTION_METHOD);
    setValue(note, SMPL_REGNO_PLUS_SCI_NAME, this::getRegnoPlusSciName);
    setValue(note, SMPL_AMPLIFICATION_STAFF, () -> AMPL_STAFF);
  }

  private String getExtractPlateId(String val) throws InvalidRowException {
    int i = val.indexOf('-');
    if (i == -1) {
      throw InvalidRowException.custom(this, ERR_MISSING_HYPHEN, SMPL_SAMPLE_PLATE_ID, val);
    }
    return val.substring(0, i);
  }

  private String getExtractId(String val) throws InvalidRowException {
    if (PTTRN_EXTRACT_ID.matcher(val).matches()) {
      return "e" + val;
    }
    throw InvalidRowException.custom(this, "Invalid extract ID: \"%s\"", val);
  }

  private String getRegnoPlusSciName() {
    List<String> both = Arrays.asList(get(COL_REGISTRATION_NUMBER), get(COL_SCIENTIFIC_NAME));
    String s = both.stream().filter(Objects::nonNull).map(x -> x.replaceAll("\\s", "_")).collect(joining("_"));
    return StringUtils.trimToNull(s);
  }

}
