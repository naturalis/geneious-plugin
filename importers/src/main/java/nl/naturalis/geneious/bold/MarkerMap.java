package nl.naturalis.geneious.bold;

import static nl.naturalis.geneious.Settings.settings;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import nl.naturalis.geneious.NaturalisPluginException;

/**
 * Reads the marker mappings in the <i>Tools -&gt; Preferences</i> panel and converts them to a Java {@code HashMap}. Note that the marker
 * mappings in the <i>Tools -&gt; Preferences</i> panel map BOLD markers to (one or more) Naturalis markers, but this class represents a
 * reverse map, mapping one Naturalis marker to (exactly) one BOLD marker.
 * 
 * @author Ayco Holleman
 *
 */
class MarkerMap extends HashMap<String, String> {

  /**
   * Creates a new {@code MarkerMap} from the lines within the marker mappings input field.
   * 
   * @param markersInSpreadSheet
   * @throws BoldNormalizationException
   */
  MarkerMap(List<String> markersInSpreadSheet) throws BoldNormalizationException {
    // Markers in the spreadsheet for which no explicit mapping was found in Tools -> Preferences. Will be mapped to
    // themselves.
    HashSet<String> unmapped = new HashSet<>(markersInSpreadSheet);
    String mappings = settings().getMarkerMap();
    try (LineNumberReader lnr = new LineNumberReader(new StringReader(mappings))) {
      for (String s = lnr.readLine(); s != null; s = lnr.readLine()) {
        if ((s = s.strip()).isEmpty() || s.charAt(0) == '#') {
          continue;
        }
        int x = s.indexOf("->");
        if (x == -1) {
          throw invalidMarkerMapping(s, lnr.getLineNumber() + 1, "Missing mapping separator \"->\". ");
        }
        if (x == s.length() - 2) {
          throw invalidMarkerMapping(s, lnr.getLineNumber() + 1, "");
        }
        String bold = s.substring(0, x).strip().toUpperCase();
        if (bold.isEmpty()) {
          throw invalidMarkerMapping(s, lnr.getLineNumber() + 1, "");
        }
        if (containsValue(bold)) {
          throw invalidMarkerMapping(s, lnr.getLineNumber() + 1, "Duplicate BOLD marker: " + bold + ". ");
        }
        String[] naturalisMarkers = s.substring(x + 2).strip().split(",");
        for (int i = 0; i < naturalisMarkers.length; ++i) {
          String naturalis = naturalisMarkers[i].strip().toUpperCase();
          if (naturalis.isEmpty()) {
            throw invalidMarkerMapping(s, lnr.getLineNumber() + 1, "");
          }
          if (containsKey(naturalis)) {
            throw invalidMarkerMapping(s, lnr.getLineNumber() + 1, "Duplicate Naturalis marker: " + naturalis + ". ");
          }
          put(naturalis, bold);
        }
        unmapped.remove(bold);
      }
      // Map remaining markers in BOLD file to themselves
      unmapped.stream().forEach(s -> put(s, s));
    } catch (IOException e) {
      throw new NaturalisPluginException(e);
    }
  }

  private static BoldNormalizationException invalidMarkerMapping(String line, int lineNo, String err) {
    String fmt = "Invalid marker mapping at line %d: %s. %sGo to Tools -> Preferences (Marker mappings) to fix this problem";
    String msg = String.format(fmt, lineNo, line, err);
    return new BoldNormalizationException(msg);
  }

}
