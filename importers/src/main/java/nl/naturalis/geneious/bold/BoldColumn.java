package nl.naturalis.geneious.bold;

/**
 * Symbolic constants for the columns in a <i>normalized</i> BOLD file. A normalized file (actually it is an in-memory
 * structure that's never saved to file) is stripped of the columns that aren't used (e.g. <i>Stop Codon</i>) and it has
 * an extra (derived) column for the marker. The value of the marker is derived from the column header in the source file.
 *
 * @author Ayco Holleman
 */
enum BoldColumn {

  PROJECT_CODE,
  PROCCES_ID,
  SAMPLE_ID,
  FIELD_ID,
  BIN,
  MARKER,
  SEQ_LENGTH,
  TRACE_COUNT,
  ACCESSION,
  IMAGE_COUNT;

}
