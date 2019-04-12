package nl.naturalis.geneious.split;

import java.util.regex.Pattern;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PCR_PLATE_ID;
import static nl.naturalis.geneious.util.DebugUtil.toJson;

/**
 * Extracts information from the name of an ab1/fasta file c.q. the header line(s) within a fasta File used to enrich a
 * Geneious document after it has been created from that file.
 * 
 * @author Ayco Holleman
 *
 */
public class SequenceNameParser {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceNameParser.class);

  private static final Pattern PT_EXTRACT_ID = Pattern.compile("^e\\d{4,16}$");
  private static final Pattern PT_PCR_PLATE_ID = Pattern.compile("^[A-Z]{1,4}\\d{1,5}$");
  private static final Pattern PT_MARKER = Pattern.compile("^[A-Za-z0-9]{2,16}$");

  private final String name;

  public SequenceNameParser(String name) {
    this.name = name;
  }

  public NaturalisNote parseName() throws NotParsableException {
    String[] segments = name.split(Pattern.quote("_"));
    if (segments.length < 5) {
      throw NotParsableException.notEnoughUnderscores(name, segments.length - 1, 4);
    }
    NaturalisNote n = new NaturalisNote();
    n.parseAndSet(SEQ_EXTRACT_ID, processExtractID(segments[0]));
    n.parseAndSet(SEQ_PCR_PLATE_ID, processPcrPlateID(segments[3]));
    n.parseAndSet(SEQ_MARKER, processMarker(segments[4]));
    guiLogger.debugf(() -> format("Note created: %s", toJson(n, false)));
    return n;
  }

  private String processExtractID(String id) throws NotParsableException {
    if (PT_EXTRACT_ID.matcher(id).matches()) {
      return id;
    }
    throw NotParsableException.badExtractId(name, id, PT_EXTRACT_ID.pattern());
  }

  private String processPcrPlateID(String id) throws NotParsableException {
    if (PT_PCR_PLATE_ID.matcher(id).matches()) {
      return id;
    }
    throw NotParsableException.badPcrPlateID(name, id, PT_PCR_PLATE_ID.pattern());
  }

  private String processMarker(String marker) throws NotParsableException {
    int i = marker.indexOf('-');
    if (i == -1) {
      i = marker.indexOf('.');
      if (i == -1) {
        i = marker.length();
      }
    }
    marker = marker.substring(0, i);
    if (PT_MARKER.matcher(marker).matches()) {
      return marker;

    }
    throw NotParsableException.badMarkerSegment(name, marker, PT_MARKER.pattern());
  }

}
