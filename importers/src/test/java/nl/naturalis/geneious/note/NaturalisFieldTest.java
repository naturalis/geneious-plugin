package nl.naturalis.geneious.note;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.junit.Test;
import static org.junit.Assert.*;

public class NaturalisFieldTest {

  @Test
  public void testNumCodes() {
    // SMPL_SEQUENCING_STAFF is defined in V1, but never actually used (so not in database)
    assertEquals(44, NaturalisField.values().length - 1);
  }

  @Test // Test all note type codes correspond to the V1 note type codes
  public void testCodesAreSameAsV1Codes() throws IOException {
    InputStream is = getClass().getResourceAsStream("V1_note_type_codes.txt");
    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
    OUTER_LOOP: for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
      for (NaturalisField nf : NaturalisField.values()) {
        if (nf.code.equals(line)) {
          continue OUTER_LOOP;
        }
      }
      fail("Not found: " + line);
    }
  }

  @Test // Test all note type codes correspond to the V1 note type codes
  public void testNamesAreSameAsV1Names() throws IOException {
    InputStream is = getClass().getResourceAsStream("V1_note_type_names.txt");
    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
    OUTER_LOOP: for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
      for (NaturalisField nf : NaturalisField.values()) {
        if (nf.name.equals(line)) {
          continue OUTER_LOOP;
        }
      }
      fail("Not found: " + line);
    }
  }

}
