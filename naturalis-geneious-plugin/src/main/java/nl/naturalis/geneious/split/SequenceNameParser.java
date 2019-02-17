package nl.naturalis.geneious.split;

import java.util.regex.Pattern;

import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.common.base.NStringUtils.rchop;

/**
 * Extracts information from the name of an ab1/fasta file c.q. the header line(s) within a fasta File used to enrich a Geneious document
 * after it has been created from that file.
 * 
 * @author Ayco Holleman
 *
 */
public class SequenceNameParser {

  public NaturalisNote parse(String fileName) throws BadFileNameException {
//    if (fileName.endsWith(".ab1")) {
//      return parseAb1(fileName, rchop(fileName, ".ab1"));
//    }
//    if (fileName.endsWith(".ab1 (reversed)")) {
//      return parseAb1(fileName, rchop(fileName, ".ab1 (reversed)"));
//    }
//    if (fileName.endsWith(".fas")) {
//      return parseFasta(fileName, rchop(fileName, ".fas"));
//    }
//    if (fileName.endsWith(".fasta")) {
//      return parseFasta(fileName, rchop(fileName, ".fasta"));
//    }
    return null;
  }

  public NaturalisNote parseAb1(String name) throws BadFileNameException {
    String[] chunks = name.split(Pattern.quote("_"));
    if (chunks.length < 5) {
      throw BadFileNameException.notEnoughUnderscores(name, chunks.length - 1, 4);
    }
    NaturalisNote note = new NaturalisNote();
    note.setExtractId(chunks[0]);
    note.setPcrPlateId(chunks[3]);
    int i = chunks[4].indexOf('-');
    if (i == -1) {
      throw BadFileNameException.missingHyphenInMarkerSegment(name);
    }
    note.setMarker(chunks[4].substring(0, i));
    return note;
  }

  public NaturalisNote parseFasta(String name) throws BadFileNameException {
    String[] chunks = name.split(Pattern.quote("_"));
    if (chunks.length < 5) {
      throw BadFileNameException.notEnoughUnderscores(name, chunks.length - 1, 4);
    }
    NaturalisNote note = new NaturalisNote();
    note.setExtractId(chunks[0]);
    note.setPcrPlateId(chunks[3]);
    note.setMarker(chunks[4]);
    return note;
  }
}
