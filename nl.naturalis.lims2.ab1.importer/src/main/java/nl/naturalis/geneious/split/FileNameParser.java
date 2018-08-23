package nl.naturalis.geneious.split;

import static nl.naturalis.geneious.util.Str.rchop;
import static nl.naturalis.geneious.util.Str.split;
import nl.naturalis.geneious.note.NaturalisNote;

/**
 * Extracts information from the constituents parts of the name of a trace file.
 * 
 * @author Ayco Holleman
 *
 */
public class FileNameParser {

  private final String fileName;

  public FileNameParser(String fileName) {
    this.fileName = fileName;
  }

  public NaturalisNote parse() throws BadFileNameException {
    if (fileName.endsWith(".ab1")) {
      return parseAb1(rchop(fileName, ".ab1"));
    }
    if (fileName.endsWith(".ab1 (reversed)")) {
      return parseAb1(rchop(fileName, ".ab1 (reversed)"));
    }
    if (fileName.endsWith(".fas")) {
      return parseFasta(rchop(fileName, ".fas"));
    }
    if (fileName.endsWith(".fasta")) {
      return parseFasta(rchop(fileName, ".fasta"));
    }
    return null;
  }

  private NaturalisNote parseAb1(String baseName) throws BadFileNameException {
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

  private NaturalisNote parseFasta(String baseName) throws BadFileNameException {
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
