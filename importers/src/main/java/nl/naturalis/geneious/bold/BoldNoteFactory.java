package nl.naturalis.geneious.bold;

import nl.naturalis.geneious.csv.InvalidRowException;
import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.note.NaturalisField.*;
import static nl.naturalis.geneious.bold.BoldColumn.*;

/**
 * Produces a {@link NaturalisNote} from the data in a {@link BoldRow}.
 */
class BoldNoteFactory extends NoteFactory<BoldColumn> {

  private static final String BOLD_URI_PREFIX = "http://www.boldsystems.org/index.php/Public_RecordView?processid=";
  private static final String GEN_BANK_URI_PREFIX = "http://www.ncbi.nlm.nih.gov/nuccore/";

  BoldNoteFactory(int rownum, BoldRow row) {
    super(rownum, row);
  }

  @Override
  protected void populate(NaturalisNote note) throws InvalidRowException {
    setRequiredValue(note, BOLD_ID, PROJECT_CODE);
    setRequiredValue(note, BOLD_PROJECT_ID, PROCCES_ID);
    setRequiredValue(note, BOLD_FIELD_ID, FIELD_ID);
    setRequiredValue(note, BOLD_BIN_CODE, BIN);
    setRequiredValue(note, BOLD_NUCLEOTIDE_LENGTH, SEQ_LENGTH);
    setRequiredValue(note, BOLD_NUM_TRACES, TRACE_COUNT);
    setRequiredValue(note, BOLD_GEN_BANK_ID, ACCESSION);
    setRequiredValue(note, BOLD_NUM_IMAGES, IMAGE_COUNT);
    setValue(note, BOLD_URI, PROCCES_ID, val -> BOLD_URI_PREFIX + val);
    setValue(note, BOLD_GEN_BANK_URI, ACCESSION, val -> GEN_BANK_URI_PREFIX + val);
  }

}
