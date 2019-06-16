package nl.naturalis.geneious.bold;

import static nl.naturalis.geneious.bold.BoldColumn.ACCESSION;
import static nl.naturalis.geneious.bold.BoldColumn.BIN;
import static nl.naturalis.geneious.bold.BoldColumn.FIELD_ID;
import static nl.naturalis.geneious.bold.BoldColumn.IMAGE_COUNT;
import static nl.naturalis.geneious.bold.BoldColumn.MARKER;
import static nl.naturalis.geneious.bold.BoldColumn.PROCCES_ID;
import static nl.naturalis.geneious.bold.BoldColumn.PROJECT_CODE;
import static nl.naturalis.geneious.bold.BoldColumn.SAMPLE_ID;
import static nl.naturalis.geneious.bold.BoldColumn.SEQ_LENGTH;
import static nl.naturalis.geneious.bold.BoldColumn.TRACE_COUNT;
import static nl.naturalis.geneious.log.GuiLogger.plural;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import nl.naturalis.geneious.csv.RowSupplier;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;

/**
 * Normalizes BOLD source files so that they can be processed like any of the other types of source files
 * (sample sheets and CRS files). BOLD files contain repeating triplets of marker-related columns, one triplet
 * per marker. Each triplet gets its own row, with the <i>non-repeating</i> columns now repeating row-wise.
 * The marker itself does not have its own column in the original file. Instead it must be inferred (excised)
 * from the column headers. In the normalized version the marker does have its own column.
 *
 * @author Ayco Holleman
 */
class BoldNormalizer {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldNormalizer.class);

  private BoldImportConfig cfg;

  private ArrayList<String> markers;

  BoldNormalizer(BoldImportConfig cfg) {
    this.cfg = cfg;
  }

  /**
   * Produces a map that maps each marker in the BOLD file to a list of rows.
   * 
   * @return
   * @throws BoldNormalizationException
   */
  Map<String, List<String[]>> normalizeRows() throws BoldNormalizationException {
    if (cfg.getSkipLines() == 0) {
      throw new BoldNormalizationException("BOLD files must have a header (\"Lines to skip\" must not be zero)");
    }
    List<String[]> rows = new RowSupplier(cfg).getAllRows();
    int numRows = rows.size() - cfg.getSkipLines();
    guiLogger.info("BOLD file contains %s row%s", numRows, plural(numRows));
    String[] header = rows.get(cfg.getSkipLines() - 1);
    guiLogger.info("Analyzing header");
    checkHeader(header);
    markers = getMarkers(header);
    guiLogger.info("Found %s marker%s: %s", markers.size(), plural(markers), markers.stream().collect(Collectors.joining(", ")));
    Map<String, List<String[]>> normalized = new LinkedHashMap<>(markers.size(), 1F);
    guiLogger.info("Normalizing BOLD file");
    for (int i = 0; i < markers.size(); ++i) {
      guiLogger.info("Extracting rows for marker \"%s\"", markers.get(i));
      List<String[]> markerRows = new ArrayList<>(rows.size() - cfg.getSkipLines());
      normalized.put(markers.get(i), markerRows);
      for (int j = cfg.getSkipLines(); j < rows.size(); ++j) {
        String[] line = rows.get(j);
        String[] compact = new String[BoldColumn.values().length];
        compact[PROJECT_CODE.ordinal()] = line[0];
        compact[PROCCES_ID.ordinal()] = line[1];
        compact[SAMPLE_ID.ordinal()] = line[2];
        compact[FIELD_ID.ordinal()] = line[3];
        compact[BIN.ordinal()] = line[4];
        compact[MARKER.ordinal()] = markers.get(i);
        compact[SEQ_LENGTH.ordinal()] = line[6 + (i * 3)];
        compact[TRACE_COUNT.ordinal()] = line[7 + (i * 3)];
        compact[ACCESSION.ordinal()] = line[8 + (i * 3)];
        compact[IMAGE_COUNT.ordinal()] = line[9];
        markerRows.add(compact);
      }
    }
    numRows *= markers.size();
    guiLogger.info("BOLD file contains %s normalized row%s", numRows, plural(numRows));
    return normalized;
  }

  /**
   * Returns the markers that the {@code BoldNormalizer} extracted from the column headers in the BOLD file.
   * 
   * @return
   */
  List<String> getMarkers() {
    Preconditions.checkState(markers != null, "Can only provide markers after normalization");
    return markers;
  }

  private static ArrayList<String> getMarkers(String[] header) {
    ArrayList<String> markers = new ArrayList<>(5);
    for (int i = 6; i < header.length && !header[i].equals("Image Count"); i += 3) {
      markers.add(StringUtils.substringBefore(header[i], "Seq. Length").trim());
    }
    return markers;
  }

  private static void checkHeader(String[] header) throws BoldNormalizationException {
    if (header.length < 10) {
      throw new BoldNormalizationException("Not enough columns in header: " + header.length);
    }
    if (!header[0].equals("Project Code")) {
      throw new BoldNormalizationException("Unexpected name for 1st column: " + header[0]);
    }
    if (!header[6].endsWith("Seq. Length")) {
      throw new BoldNormalizationException("At least one marker required. Instead found: " + header[6]);
    }
  }

}
