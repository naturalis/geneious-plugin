package nl.naturalis.geneious.name;

import org.junit.Test;

import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.Messages;
import static org.junit.Assert.*;

public class NameParsingErrorTest {

  /*
   * The following tests relate to a bug that manifested itself just as we were about to roll out V2.0.0
   */

  @Test
  public void nameParsingFailed_1() {
    GuiLogger logger = GuiLogManager.getLogger(NameParsingErrorTest.class);
    Messages.Error.nameParsingFailed(logger, "SOME_BAD_NAME", NotParsableException.notEnoughUnderscores("SOME_BAD_NAME.txt", 5, 4));
    assertTrue("Got here without exceptions!", true);
  }

  @Test
  public void nameParsingFailed_2() {
    String fmt = "Invalid extract ID in \"%s\": \"%s\"";
    String name = "SOME_BAD_NAME";
    String extractId = "e123456";
    String pattern = "[^BLA]";
    String.format(fmt, name, extractId, pattern);
    assertTrue("Got here without exceptions!", true);
  }

}
