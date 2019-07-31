package nl.naturalis.geneious.bold;

import static java.util.stream.Collectors.joining;
import static nl.naturalis.geneious.bold.BoldColumn.COL_ACCESSION;
import static nl.naturalis.geneious.bold.BoldColumn.COL_BIN;
import static nl.naturalis.geneious.bold.BoldColumn.COL_FIELD_ID;
import static nl.naturalis.geneious.bold.BoldColumn.COL_IMAGE_COUNT;
import static nl.naturalis.geneious.bold.BoldColumn.COL_PROCCES_ID;
import static nl.naturalis.geneious.bold.BoldColumn.COL_PROJECT_CODE;
import static nl.naturalis.geneious.bold.BoldColumn.COL_SAMPLE_ID;
import static nl.naturalis.geneious.bold.BoldColumn.COL_SEQ_LENGTH;
import static nl.naturalis.geneious.bold.BoldColumn.COL_TRACE_COUNT;
import static nl.naturalis.geneious.log.GuiLogger.plural;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;

/**
 * Normalizes BOLD source files so that they can be processed just like sample sheets and CRS files. BOLD files contain repeating triplets
 * of marker-related columns. After normalization each triplet is in a separate row, with the <i>non-repeating</i> columns in the original
 * file now repeating row-wise. In the original file the marker itself does not have its own column. Instead it must be inferred (excised)
 * from the column headers. In the normalized version the marker <i>does</i> have its own column.
 *
 * @author Ayco Holleman
 */
class BoldNormalizer {

  private static final GuiLogger logger = GuiLogManager.getLogger(BoldNormalizer.class);

  private final BoldImportConfig cfg;
  private final List<String[]> originalRows;
  private final ArrayList<String> markers;
  private final Map<String, List<String[]>> rowsPerMarker;

  BoldNormalizer(BoldImportConfig cfg) throws BoldNormalizationException {
    this.cfg = cfg;
    this.originalRows = new RowSupplier(cfg).getAllRows();
    int x = originalRows.size() - cfg.getSkipLines();
    logger.info("BOLD file contains %s row%s (excluding header rows)", x, plural(x));
    checkHeader();
    this.rowsPerMarker = normalize();
    this.markers = new ArrayList<>(rowsPerMarker.keySet());
  }

  /**
   * Returns the number of rows in the spreadsheet before normalization and excluding header rows;
   * 
   * @return
   */
  int countRows() {
    return originalRows.size() - cfg.getSkipLines();
  }

  /**
   * Returns the markers found in the header of the spreadsheet. Note however that if the "Seq. Length" column of a triplet of marker
   * columns is completely empty (i.e. none of the rows have a value in the "Seq. Length" column), that marker is considered absent and will
   * <i>not</i> be included in the returned list.
   * 
   * @return
   */
  List<String> getMarkers() {
    return markers;
  }

  /**
   * Returns a per-marker list of rows. The map might be empty if the BOLD spreadsheet did not contain any marker-related columns or (more
   * hypothetically) it did, but the "Seq. Length" column was blank for all rows. The keys in the returned map are the markers, in the same
   * order as they were found in the spreadsheet.
   * 
   * @return
   */
  Map<String, List<String[]>> getRowsPerMarker() {
    return rowsPerMarker;
  }

  /**
   * Returns the rows for an arbitrary marker or, if the spreadsheet did not contain any markers, a new list of rows with only specimen
   * information.
   * 
   * @return
   */
  List<String[]> getRows() {
    if (rowsPerMarker.isEmpty()) {
      ArrayList<String[]> rows = new ArrayList<>(countRows());
      for (int j = cfg.getSkipLines(); j < originalRows.size(); ++j) {
        String[] line = originalRows.get(j);
        String[] row = new String[BoldColumn.values().length];
        row[COL_PROJECT_CODE.ordinal()] = line[0];
        row[COL_PROCCES_ID.ordinal()] = line[1];
        row[COL_SAMPLE_ID.ordinal()] = line[2];
        row[COL_FIELD_ID.ordinal()] = line[3];
        row[COL_BIN.ordinal()] = line[4];
        row[COL_IMAGE_COUNT.ordinal()] = line[6];
        rows.add(row);
      }
      return rows;
    }
    return rowsPerMarker.entrySet().iterator().next().getValue();
  }

  private Map<String, List<String[]>> normalize() {
    String[] header = originalRows.get(cfg.getSkipLines() - 1);
    ArrayList<String> markers = getMarkersInHeader(header);
    Map<String, List<String[]>> rowsPerMarker = new LinkedHashMap<>(markers.size(), 1F);
    if (markers.isEmpty()) {
      logger.info("Found 0 markers");
      return rowsPerMarker;
    }
    logger.info("Found %s marker%s: %s", markers.size(), plural(markers), markers.stream().collect(joining(", ")));
    logger.info("Normalizing BOLD file");
    for (int i = 0; i < markers.size(); ++i) {
      String marker = markers.get(i);
      List<String[]> rows = new ArrayList<>(countRows());
      boolean allBlank = true;
      for (int j = cfg.getSkipLines(); j < originalRows.size(); ++j) {
        String[] line = originalRows.get(j);
        String[] row = new String[BoldColumn.values().length];
        row[COL_PROJECT_CODE.ordinal()] = line[0];
        row[COL_PROCCES_ID.ordinal()] = line[1];
        row[COL_SAMPLE_ID.ordinal()] = line[2];
        row[COL_FIELD_ID.ordinal()] = line[3];
        row[COL_BIN.ordinal()] = line[4];
        row[COL_SEQ_LENGTH.ordinal()] = line[6 + (i * 3)];
        if (isNotBlank(row[COL_SEQ_LENGTH.ordinal()])) {
          allBlank = false;
          row[COL_TRACE_COUNT.ordinal()] = line[7 + (i * 3)];
          row[COL_ACCESSION.ordinal()] = line[8 + (i * 3)];
        } else if (isNotBlank(line[7 + (i * 3)]) || isNotBlank(line[8 + (i * 3)])) {
          logger.warn("Line %d: ignoring marker info (missing value for \"%s Seq. Length\")", j + 1, marker);
        }
        row[COL_IMAGE_COUNT.ordinal()] = line[6 + (markers.size() * 3)];
        rows.add(row);
      }
      if (allBlank) {
        logger.warn("Will not process marker %$1s. Column \"%$1s Seq. Length\" is empty.", marker);
      } else {
        rowsPerMarker.put(marker, rows);
      }
    }
    return rowsPerMarker;
  }

  /*
   * Returns the markers found in the header of the spreadsheet.
   */
  private static ArrayList<String> getMarkersInHeader(String[] header) {
    ArrayList<String> markers = new ArrayList<>(5);
    for (int i = 6; i < header.length && !header[i].equals("Image Count"); i += 3) {
      markers.add(StringUtils.substringBefore(header[i], "Seq. Length").trim().toUpperCase());
    }
    return markers;
  }

  private void checkHeader() throws BoldNormalizationException {
    logger.info("Analyzing header");
    String[] header = originalRows.get(cfg.getSkipLines() - 1);
    if (header.length < 10) {
      throw new BoldNormalizationException("Not enough columns in header: " + header.length);
    }
    if (!header[0].equals("Project Code")) {
      throw new BoldNormalizationException("Unexpected header for 1st column: " + header[0]);
    }
    if (!header[6].endsWith("Seq. Length") && !header[6].equals("Image Count")) {
      String fmt = "Header for column 7 must be either \"<marker> Seq. Length\" or \"Image Count\". Found: \"%s\"";
      String msg = String.format(fmt, header[6]);
      throw new BoldNormalizationException(msg);
    }
  }

}
