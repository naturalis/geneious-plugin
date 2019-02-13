package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FastaFileSplitterTest {

  @Test
  public void testCreateDir() throws IOException {
    try (FileOutputStream fos = FileUtils.openOutputStream(new File("/home/ayco/3/4/test.txt"))) {
      fos.write("Hello World".getBytes());
    }
  }

}
