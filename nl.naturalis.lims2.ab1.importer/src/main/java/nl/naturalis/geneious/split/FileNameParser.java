package nl.naturalis.geneious.split;

import nl.naturalis.geneious.note.NaturalisNote;

import static nl.naturalis.geneious.util.Str.rchop;
import static nl.naturalis.geneious.util.Str.split;

/**
 * Extracts information from the name of an ab1/fasta file, used to enrich a Geneious document after it has been created from that file.
 * 
 * @author Ayco Holleman
 *
 */
public class FileNameParser {

  public NaturalisNote parse(String fileName) throws BadFileNameException {
    if (fileName.endsWith(".ab1")) {
      return parseAb1(fileName, rchop(fileName, ".ab1"));
    }
    if (fileName.endsWith(".ab1 (reversed)")) {
      return parseAb1(fileName, rchop(fileName, ".ab1 (reversed)"));
    }
    if (fileName.endsWith(".fas")) {
      return parseFasta(fileName, rchop(fileName, ".fas"));
    }
    if (fileName.endsWith(".fasta")) {
      return parseFasta(fileName, rchop(fileName, ".fasta"));
    }
    return null;
  }

  private static NaturalisNote parseAb1(String fileName, String baseName) throws BadFileNameException {
    String[] chunks = split(baseName, "_");
    if (chunks.length < 5) {
      throw BadFileNameException.notEnoughUnderscores(fileName, chunks.length - 1, 4);
    }
    NaturalisNote note = new NaturalisNote();
    note.setExtractId(chunks[0]);
    note.setPcrPlateId(chunks[3]);
    int i = chunks[4].indexOf('-');
    if (i == -1) {
      throw BadFileNameException.missingHyphenInMarkerSegment(fileName);
    }
    note.setMarker(chunks[4].substring(0, i));
    return note;
  }

  private static NaturalisNote parseFasta(String fileName, String baseName) throws BadFileNameException {
    String[] chunks = split(baseName, "_");
    if (chunks.length < 5) {
      throw BadFileNameException.notEnoughUnderscores(fileName, chunks.length - 1, 4);
    }
    NaturalisNote note = new NaturalisNote();
    note.setExtractId(chunks[0]);
    note.setPcrPlateId(chunks[3]);
    note.setMarker(chunks[4]);
    return note;
  }
}
