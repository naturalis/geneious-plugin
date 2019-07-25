package nl.naturalis.geneious.util;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.geneious.name.NotParsableException;
import nl.naturalis.geneious.name.SequenceNameParser;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.note.SeqPass;

import static org.junit.Assert.assertEquals;

import static nl.naturalis.geneious.Setting.DEBUG;
import static nl.naturalis.geneious.Setting.PRETTY_NOTES;
import static nl.naturalis.geneious.Settings.settings;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PASS;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PCR_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_SEQUENCING_STAFF;

public class SequenceNameParserTest {

  @BeforeClass
  public static void init() {
    settings().update(DEBUG, Boolean.TRUE);
    settings().update(PRETTY_NOTES, Boolean.TRUE);
  }

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
    expected.castAndSet(SEQ_PASS, SeqPass.NOT_DETERMINED);
    System.out.println("Expected: " + JsonUtil.toJson(expected));
    System.out.println("Actual: " + JsonUtil.toJson(actual));
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
    expected.castAndSet(SEQ_PASS, SeqPass.NOT_DETERMINED);
    assertEquals(expected, actual);
  }

  @Test(expected = NotParsableException.class)
  public void testAb1_02() throws NotParsableException {
    // Not enough underscores
    String name = "e4012524841_Phlter_RL031_COI-H2198.ab1";
    SequenceNameParser parser = new SequenceNameParser(name);
    parser.parseName();
  }

}
