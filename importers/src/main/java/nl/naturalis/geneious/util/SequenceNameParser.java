package nl.naturalis.geneious.util;

import java.util.regex.Pattern;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;

import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PCR_PLATE_ID;

/**
 * Extracts information from the name of an ab1/fasta file c.q. the header line(s) within a fasta File used to enrich a Geneious document
 * after it has been created from that file.
 * 
 * @author Ayco Holleman
 *
 */
public class SequenceNameParser {

  private static final Pattern PT_EXTRACT_ID = Pattern.compile("^e\\d{10}$");
  private static final Pattern PT_PCR_PLATE_ID = Pattern.compile("^[A-Z]{2}\\D{3}$");
  private static final Pattern PT_MARKER = Pattern.compile("^[A-Z]{3}($|\\.|-)");

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
    throw NotParsableException.badExtractId(name, id, PT_EXTRACT_ID.pattern());
  }

  private String processMarker(String marker) throws NotParsableException {
    if (PT_MARKER.matcher(marker).matches()) {
      return marker.substring(0, 2);
    }
    throw NotParsableException.badMarkerSegment(name, marker, PT_MARKER.pattern());
  }

}
