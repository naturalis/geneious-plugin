package nl.naturalis.geneious;

import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.Geneious;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.GeneiousService;
import com.biomatters.geneious.publicapi.plugin.Icons;
import com.biomatters.geneious.publicapi.plugin.PluginPreferences;
import com.biomatters.geneious.publicapi.utilities.IconUtilities;

import nl.naturalis.geneious.bold.BoldDocumentOperation;
import nl.naturalis.geneious.crs.CrsDocumentOperation;
import nl.naturalis.geneious.seq.SequenceImportDocumentOperation;
import nl.naturalis.geneious.smpl.SampleSheetDocumentOperation;
import nl.naturalis.geneious.split.SplitNameDocumentOperation;

public class NaturalisGeneiousPlugin extends GeneiousPlugin {

  /*
   * We must instantiate a NaturalisPluginPreferences object as soon as possible, before getDocumentOperations() is called. This method
   * returns our implementation classes these in turn have static initalizers that depend on the preferences being set and readable. That's
   * the only reason why we have this (unused) class variable here. (See also NaturalisPreferencesOptions)
   */
  @SuppressWarnings("unused")
  private static final NaturalisPluginPreferences prefs = new NaturalisPluginPreferences();

  @Override
  public Icons getIcons() {
    return IconUtilities.getIconsFromJar(getClass(), "/rood.ico");
  }

  @Override
  @SuppressWarnings("rawtypes")
  public List<PluginPreferences> getPluginPreferences() {
    // Must always return a new instance, otherwise Geneious will throw an exception.
    return Arrays.asList(new NaturalisPluginPreferences());
  }

  @Override
  public DocumentOperation[] getDocumentOperations() {
    return new DocumentOperation[] {
        new SequenceImportDocumentOperation(),
        new SampleSheetDocumentOperation(),
        new CrsDocumentOperation(),
        new BoldDocumentOperation(),
        new SplitNameDocumentOperation()
    };
  }

  @Override
  public GeneiousService[] getServices() {
    return new GeneiousService[0];
  }

  @Override
  public String getAuthors() {
    return "Rudy Broekhuizen, Wilfred Gerritsen, Ayco Holleman, Judith Slaa, Chantal SlegtenHorst, Oscar Vorst";
  }

  @Override
  public String getDescription() {
    return "Naturalis utilities for Geneious";
  }

  @Override
  public String getHelp() {
    return "Provides operations for enriching sequence documents with information from CRS, BOLD and information extracted "
        + "from the document names themselves";
  }

  @Override
  public int getMaximumApiVersion() {
    return Geneious.getMajorApiVersion();
  }

  @Override
  public String getMinimumApiVersion() {
    return Geneious.getApiVersion();
  }

  @Override
  public String getName() {
    return "Naturalis Geneious Plugin";
  }

  @Override
  public String getVersion() {
    // Geneious does not allow version strings like V2.0.0-ALPHA. Only 2.0.0 is allowed in this particular example.
    String version = PluginInfo.getInstance().getVersion();
    int i = version.indexOf('-');
    if (i == -1) {
      return version.substring(1);
    }
    return version.substring(1, i);
  }
}
