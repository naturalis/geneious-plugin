package nl.naturalis.geneious.util;

import org.junit.Test;

import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.NotParsableException;
import nl.naturalis.geneious.split.SequenceNameParser;

import static org.junit.Assert.*;

import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.*;

public class SequenceNameParserTest {

  @Test
  public void testFasta_01() throws NotParsableException {
    String name = "e4012524841_Phl_ter_RL031_COI.fas";
    SequenceNameParser parser = new SequenceNameParser(name);
    NaturalisNote actual = parser.parseName();
    NaturalisNote expected = new NaturalisNote();
    expected.parseAndSet(SEQ_EXTRACT_ID, "e4012524841");
    expected.parseAndSet(SEQ_PCR_PLATE_ID, "RL031");
    expected.parseAndSet(SEQ_MARKER, "COI");
    expected.parseAndSet(SEQ_SEQUENCING_STAFF, "Naturalis Biodiversity Center Laboratories");
    System.out.println(DebugUtil.toJson(actual, true));
    System.out.println(DebugUtil.toJson(expected, true));
    assertEquals(expected, actual);
  }

  @Test
  public void testAb1_01() throws NotParsableException {
    String name = "e4012524841_Phl_ter_RL031_COI-H2198.ab1";
    SequenceNameParser parser = new SequenceNameParser(name);
    NaturalisNote actual = parser.parseName();
    NaturalisNote expected = new NaturalisNote();
    expected.parseAndSet(SEQ_EXTRACT_ID, "e4012524841");
    expected.parseAndSet(SEQ_PCR_PLATE_ID, "RL031");
    expected.parseAndSet(SEQ_MARKER, "COI");
    expected.parseAndSet(SEQ_SEQUENCING_STAFF, "Naturalis Biodiversity Center Laboratories");
    assertEquals(expected, actual);
  }

}
