package nl.naturalis.geneious.smpl;

import com.biomatters.geneious.publicapi.plugin.TestGeneious;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.geneious.smpl.SmplNoteFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SampleSheetRowTest {

  @BeforeClass
  public static void init() {
    TestGeneious.initializeAllPlugins();
  }


  @Test
  public void isEmpty_01() {
    String[] cells = new String[] {
        null,
        "",
        " ",
        "\t",
        " \t \n"
    };
  }

  @Test
  public void isEmpty_02() {
    String[] cells = new String[] {
        null,
        "",
        " ",
        "\t",
        " \tHello World\n"
    };
  }

}
