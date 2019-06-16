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
 * Reads the (user-configurable) marker mappings in the <i>Tools -&gt; Preferences</i> panel and converts them
 * to an actual Java {@code HashMap}.
 * 
 * @author Ayco Holleman
 *
 */
class MarkerMap extends HashMap<String, String[]> {

  MarkerMap(List<String> markersInBoldFile) throws BoldNormalizationException {
    HashSet<String> copy = new HashSet<>(markersInBoldFile);
    String mappings = settings().getMarkerMap();
    try (LineNumberReader lnr = new LineNumberReader(new StringReader(mappings))) {
      for (String s = lnr.readLine(); s != null; s = lnr.readLine()) {
        if ((s = s.strip()).isEmpty() || s.charAt(0) == '#') {
          continue;
        }
        int x = s.indexOf("->");
        if (x == -1) {
          throw invalidMarkerMapping(s, lnr.getLineNumber() + 1);
        }
        if (x == s.length() - 2) {
          throw invalidMarkerMapping(s, lnr.getLineNumber() + 1);
        }
        String bold = s.substring(0, x).strip();
        if (bold.isEmpty()) {
          throw invalidMarkerMapping(s, lnr.getLineNumber() + 1);
        }
        if (!copy.contains(bold)) {
          continue;
        }
        String[] naturalis = s.substring(x + 2).strip().split(",");
        for (int i = 0; i < naturalis.length; ++i) {
          String n = naturalis[i].strip();
          if (n.isEmpty()) {
            throw invalidMarkerMapping(s, lnr.getLineNumber() + 1);
          }
          naturalis[i] = n;
        }
        put(bold, naturalis);
        copy.remove(bold);
      }
      // Map remaining markers in BOLD file to themselves
      copy.stream().forEach(s -> put(s, new String[] { s }));
    } catch (IOException e) {
      throw new NaturalisPluginException(e);
    }
  }

  private static BoldNormalizationException invalidMarkerMapping(String line, int lineNo) {
    String msg = String.format("Invalid marker mapping at line %d: %s. Go to Tools -> Preferences (Marker mappings) to fix this problem",
        lineNo, line);
    return new BoldNormalizationException(msg);
  }

}
