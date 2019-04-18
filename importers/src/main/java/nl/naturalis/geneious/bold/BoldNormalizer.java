package nl.naturalis.geneious.bold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

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
import static nl.naturalis.geneious.gui.log.GuiLogger.plural;

/**
 * Normalizes BOLD source files so that they can be processed like any of the other types of source files (sample sheets
 * and CRS files). It takes the rows in a BOLD file as input and produces a new set of rows, each one having just one
 * quartet of marker column. (The original BOLD file only has three marker-related columns, with the name of the marker
 * being implicit in the headers of those columns. In the output produced by the {@code BoldNormalizer} the name of the
 * marker becomes a value in a 4th column.)
 *
 * @author Ayco Holleman
 */
public class BoldNormalizer {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldNormalizer.class);

  private BoldImportConfig cfg;
  private List<String[]> lines;

  public BoldNormalizer(BoldImportConfig cfg, List<String[]> lines) {
    this.cfg = cfg;
    this.lines = lines;
  }

  public List<String[]> normalizeRows() throws BoldNormalizationException {
    if (lines.size() < cfg.getSkipLines()) {
      throw new BoldNormalizationException("Number of rows in BOLD file must be greater than number of lines to skip");
    }
    String[] header = lines.get(cfg.getSkipLines() - 1);
    guiLogger.info("Analyzing header");
    checkHeader(header);
    List<String> markers = getMarkers(header);
    List<String[]> normalized = new ArrayList<>(lines.size() * markers.size());
    guiLogger.info("Found %s marker%s: %s", markers.size(), plural(markers), markers.stream().collect(Collectors.joining("  ")));
    for (int i = 0; i < markers.size(); ++i) {
      guiLogger.info("Extracting rows for marker \"%s\"", markers.get(i));
      for (int j = cfg.getSkipLines(); j < lines.size(); ++j) {
        String[] line = lines.get(j);
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
        normalized.add(compact);
      }
    }
    return normalized;
  }

  private static List<String> getMarkers(String[] header) {
    List<String> markers = new ArrayList<>(5);
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
