package nl.naturalis.geneious.bold;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import static nl.naturalis.geneious.gui.log.GuiLogger.*;
import static nl.naturalis.geneious.bold.BoldColumn.*;

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

  private String[] header;
  private List<String[]> lines;

  public BoldNormalizer(String[] header, List<String[]> lines) {
    this.header = header;
    this.lines = lines;
  }

  public List<String[]> normalize() throws BoldNormalizationException {
    guiLogger.info("Analyzing header");
    checkHeader();
    List<String> markers = getMarkers();
    List<String[]> normalized = new ArrayList<>(lines.size() * markers.size());
    guiLogger.info("Found %s marker%s: %s", markers.size(), plural(markers), markers.stream().collect(Collectors.joining(", ")));
    for (int i = 0; i < markers.size(); ++i) {
      guiLogger.info("Extracting rows for marker \"%s\"");
      for (String[] line : lines) {
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

  private List<String> getMarkers() {
    List<String> markers = new ArrayList<>(5);
    for (int i = 6; i < header.length && !header[i].equals("Image Count"); i += 3) {
      markers.add(StringUtils.substringBefore(header[i], "Seq. Length").trim());
    }
    return markers;
  }

  private void checkHeader() throws BoldNormalizationException {
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
