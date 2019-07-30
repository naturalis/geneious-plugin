package nl.naturalis.geneious.bold;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.bold.BoldColumn.COL_ACCESSION;
import static nl.naturalis.geneious.bold.BoldColumn.COL_BIN;
import static nl.naturalis.geneious.bold.BoldColumn.COL_FIELD_ID;
import static nl.naturalis.geneious.bold.BoldColumn.COL_IMAGE_COUNT;
import static nl.naturalis.geneious.bold.BoldColumn.COL_PROCCES_ID;
import static nl.naturalis.geneious.bold.BoldColumn.COL_PROJECT_CODE;
import static nl.naturalis.geneious.bold.BoldColumn.COL_SEQ_LENGTH;
import static nl.naturalis.geneious.bold.BoldColumn.COL_TRACE_COUNT;
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
    setRequiredValue(note, BOLD_ID, COL_PROCCES_ID);
    setRequiredValue(note, BOLD_PROJECT_ID, COL_PROJECT_CODE);
    setValue(note, BOLD_FIELD_ID, COL_FIELD_ID);
    setValue(note, BOLD_BIN_CODE, COL_BIN);
    setValue(note, BOLD_NUM_IMAGES, COL_IMAGE_COUNT);
    setValue(note, BOLD_URI, COL_PROCCES_ID, val -> BOLD_URI_PREFIX + val);
    if(!ignoreMarkerColumns) {
      setValue(note, BOLD_NUCLEOTIDE_LENGTH, COL_SEQ_LENGTH);
      setValue(note, BOLD_NUM_TRACES, COL_TRACE_COUNT);
      setValue(note, BOLD_GEN_BANK_ID, COL_ACCESSION);
      setValue(note, BOLD_GEN_BANK_URI, COL_ACCESSION, val -> val == null ? null : GEN_BANK_URI_PREFIX + val);
    }
  }

}
