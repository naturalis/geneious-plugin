package nl.naturalis.geneious.util;

import java.util.regex.Pattern;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;

/**
 * Extracts information from the name of an ab1/fasta file c.q. the header line(s) within a fasta File used to enrich a Geneious document
 * after it has been created from that file.
 * 
 * @author Ayco Holleman
 *
 */
public class SequenceNameParser {

  public static NaturalisNote parseAb1(String name) throws NotParsableException {
    String[] chunks = name.split(Pattern.quote("_"));
    if (chunks.length < 5) {
      throw NotParsableException.notEnoughUnderscores(name, chunks.length - 1, 4);
    }
    NaturalisNote note = new NaturalisNote();
    note.setExtractId(chunks[0]);
    note.setPcrPlateId(chunks[3]);
    int i = chunks[4].indexOf('-');
    if (i == -1) {
      throw NotParsableException.missingHyphenInMarkerSegment(name);
    }
    note.setMarker(chunks[4].substring(0, i));
    return note;
  }

  public static NaturalisNote parseFasta(String name) throws NotParsableException {
    String[] chunks = name.split(Pattern.quote("_"));
    if (chunks.length < 5) {
      throw NotParsableException.notEnoughUnderscores(name, chunks.length - 1, 4);
    }
    NaturalisNote note = new NaturalisNote();
    note.setExtractId(chunks[0]);
    note.setPcrPlateId(chunks[3]);
    note.setMarker(chunks[4]);
    return note;
  }
}
