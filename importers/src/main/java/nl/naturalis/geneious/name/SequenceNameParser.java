package nl.naturalis.geneious.name;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PASS;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PCR_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_SEQUENCING_STAFF;
import static nl.naturalis.geneious.util.JsonUtil.toJson;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import java.util.regex.Pattern;

import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.SeqPass;

/**
 * Parses an AB1 file name or fasta sequence header and creates a {@link NaturalisNote} from the extracted information.
 * This is in effect a {@link NoteFactory} for the AB1/Fasta Import opration and the Split Name operation.
 * 
 * @author Ayco Holleman
 *
 */
public class SequenceNameParser {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceNameParser.class);

  private static final Pattern PT_EXTRACT_ID = Pattern.compile("^e\\d{4,16}$");
  private static final Pattern PT_PCR_PLATE_ID = Pattern.compile("^[A-Z]{1,4}\\d{1,5}$");
  private static final Pattern PT_MARKER = Pattern.compile("^[A-Za-z0-9]{2,16}$");

  private static final String CONSTANT_VALUE_SEQ_STAFF = "Naturalis Biodiversity Center Laboratories";

  private final String name;

  /**
   * Creates a name parser for the provded name.
   * 
   * @param name
   */
  public SequenceNameParser(String name) {
    this.name = name;
  }

  /**
   * Parses the name passed to the {@link #SequenceNameParser(String) constructor} and turns it into a
   * {@code NaturalisNote}.
   * 
   * @return
   * @throws NotParsableException
   */
  public NaturalisNote parseName() throws NotParsableException {
    // With names like "e25918193_Oxy_syl_RL007_COI Assembly" we must take everything up to the 1st whitespace character.
    String[] segments = substringBefore(name, " ").split(Pattern.quote("_"));
    if(segments.length < 5) {
      throw NotParsableException.notEnoughUnderscores(name, segments.length - 1, 4);
    }
    NaturalisNote note = new NaturalisNote();
    note.castAndSet(SEQ_EXTRACT_ID, processExtractID(segments[0]));
    note.castAndSet(SEQ_PCR_PLATE_ID, processPcrPlateID(segments[3]));
    note.castAndSet(SEQ_MARKER, processMarker(segments[4]));
    note.castAndSet(SEQ_SEQUENCING_STAFF, CONSTANT_VALUE_SEQ_STAFF);
    note.castAndSet(SEQ_PASS, SeqPass.NOT_DETERMINED);
    guiLogger.debugf(() -> format("Note created: %s", toJson(note)));
    return note;
  }

  private String processExtractID(String id) throws NotParsableException {
    if(PT_EXTRACT_ID.matcher(id).matches()) {
      return id;
    }
    throw NotParsableException.badExtractId(name, id, PT_EXTRACT_ID.pattern());
  }

  private String processPcrPlateID(String id) throws NotParsableException {
    if(PT_PCR_PLATE_ID.matcher(id).matches()) {
      return id;
    }
    throw NotParsableException.badPcrPlateID(name, id, PT_PCR_PLATE_ID.pattern());
  }

  private String processMarker(String marker) throws NotParsableException {
    int i = marker.indexOf('-');
    if(i == -1) {
      i = marker.indexOf('.');
      if(i == -1) {
        i = marker.length();
      }
    }
    marker = marker.substring(0, i);
    if(PT_MARKER.matcher(marker).matches()) {
      return marker;

    }
    throw NotParsableException.badMarkerSegment(name, marker, PT_MARKER.pattern());
  }

}
