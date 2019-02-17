package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.plugin.TestGeneious;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.geneious.NaturalisGeneiousPlugin;

public class FastaFileImporterTest {

  @BeforeClass
  public static void init() {
    TestGeneious.initializeAllPlugins();
    NaturalisGeneiousPlugin ngp=new NaturalisGeneiousPlugin();
  }

  @Test
  public void importFiles() throws URISyntaxException, IOException {
    File f0 = new File(getClass().getResource("four_valid_fastas.fasta").toURI());
    List<File> files = Arrays.asList(f0);
    FastaFileImporter imp=new FastaFileImporter(files);
    imp.importFiles();
  }

}
