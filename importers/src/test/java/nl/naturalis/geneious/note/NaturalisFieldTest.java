package nl.naturalis.geneious.note;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.junit.Test;
import static org.junit.Assert.*;

public class NaturalisFieldTest {

  @Test
  public void testCodesAreSameAsV1Codes() throws IOException {
    InputStream is = getClass().getResourceAsStream("V1_note_type_codes.txt");
    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
    OUTER_LOOP: for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
      for (NaturalisField nf : NaturalisField.values()) {
        if (nf.getCode().equals(line)) {
          continue OUTER_LOOP;
        }
      }
      fail("Not found: " + line);
    }
  }

}
