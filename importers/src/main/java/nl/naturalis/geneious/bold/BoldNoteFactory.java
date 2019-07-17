package nl.naturalis.geneious.bold;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.bold.BoldColumn.ACCESSION;
import static nl.naturalis.geneious.bold.BoldColumn.BIN;
import static nl.naturalis.geneious.bold.BoldColumn.FIELD_ID;
import static nl.naturalis.geneious.bold.BoldColumn.IMAGE_COUNT;
import static nl.naturalis.geneious.bold.BoldColumn.PROCCES_ID;
import static nl.naturalis.geneious.bold.BoldColumn.PROJECT_CODE;
import static nl.naturalis.geneious.bold.BoldColumn.SEQ_LENGTH;
import static nl.naturalis.geneious.bold.BoldColumn.TRACE_COUNT;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_BIN_CODE;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_FIELD_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_GEN_BANK_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_GEN_BANK_URI;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_NUCLEOTIDE_LENGTH;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_NUM_IMAGES;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_NUM_TRACES;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_PROJECT_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_URI;

/**
 * A factory of {@code NaturalisNote} instances specialized in populating the BOLD-related fields of a {@code NaturalisNote}.
 */
class BoldNoteFactory extends NoteFactory<BoldColumn> {

  private static final String BOLD_URI_PREFIX = "http://www.boldsystems.org/index.php/Public_RecordView?processid=";
  private static final String GEN_BANK_URI_PREFIX = "http://www.ncbi.nlm.nih.gov/nuccore/";

  private final boolean ignoreMarkerColumns;

  /**
   * Creates a factory of {@code NaturalisNote} instances specialized in populating the BOLD-related fields.
   * @param row
   * @param line
   * @param ignoreMarkerColumns
   */
  BoldNoteFactory(BoldRow row, int line, boolean ignoreMarkerColumns) {
    super(row, line);
    this.ignoreMarkerColumns = ignoreMarkerColumns;
  }

  @Override
  protected void populate(NaturalisNote note) throws InvalidRowException {
    setRequiredValue(note, BOLD_ID, PROCCES_ID);
    setRequiredValue(note, BOLD_PROJECT_ID, PROJECT_CODE);
    setValue(note, BOLD_FIELD_ID, FIELD_ID);
    setValue(note, BOLD_BIN_CODE, BIN);
    setValue(note, BOLD_NUM_IMAGES, IMAGE_COUNT);
    setValue(note, BOLD_URI, PROCCES_ID, val -> BOLD_URI_PREFIX + val);
    if(!ignoreMarkerColumns) {
      setValue(note, BOLD_NUCLEOTIDE_LENGTH, SEQ_LENGTH);
      setValue(note, BOLD_NUM_TRACES, TRACE_COUNT);
      setValue(note, BOLD_GEN_BANK_ID, ACCESSION);
      setValue(note, BOLD_GEN_BANK_URI, ACCESSION, val -> val == null ? null : GEN_BANK_URI_PREFIX + val);
    }
  }

}
