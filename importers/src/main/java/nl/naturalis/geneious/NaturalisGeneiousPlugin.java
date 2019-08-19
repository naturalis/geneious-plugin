package nl.naturalis.geneious;

import static com.biomatters.geneious.publicapi.utilities.IconUtilities.getIconsFromJar;

import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.Geneious;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.Icons;
import com.biomatters.geneious.publicapi.plugin.PluginPreferences;

import nl.naturalis.geneious.bold.BoldDocumentOperation;
import nl.naturalis.geneious.crs.CrsDocumentOperation;
import nl.naturalis.geneious.seq.Ab1FastaDocumentOperation;
import nl.naturalis.geneious.smpl.SampleSheetDocumentOperation;
import nl.naturalis.geneious.split.SplitNameDocumentOperation;

/**
 * <b>Main hook into the Geneious plugin architecture (start here!)</b>. Tells Geneious which services the plugin provides, and which classes
 * implement them. Everything drills down from here.
 * 
 * @author Ayco Holleman
 *
 */
public class NaturalisGeneiousPlugin extends GeneiousPlugin {

  /*
   * We must instantiate a NaturalisPluginPreferences object as soon as possible, before getDocumentOperations() is called. This method
   * returns our implementation classes these in turn have static initalizers that depend on the preferences being set and readable. That's
   * the only reason why we have this (unused) class variable here. (See also NaturalisPreferencesOptions)
   */
  @SuppressWarnings("unused")
  private static final NaturalisPluginPreferences prefs = new NaturalisPluginPreferences();

  /**
   * Returns the standard red Naturalis icon.
   */
  @Override
  public Icons getIcons() {
    return getIconsFromJar(getClass(), "/images/nbc_red.png");
  }

  /**
   * Returns the configuration for the <i>Tools -&gt; Preferences</i> panel.
   */
  @Override
  @SuppressWarnings("rawtypes")
  public List<PluginPreferences> getPluginPreferences() {
    // Must always return a new instance, otherwise Geneious will throw an exception.
    return Arrays.asList(new NaturalisPluginPreferences());
  }

  /**
   * Returns the classes implementing the various services provided by the plugin.
   */
  @Override
  public DocumentOperation[] getDocumentOperations() {
    return new DocumentOperation[] {
        new Ab1FastaDocumentOperation(),
        new SampleSheetDocumentOperation(),
        new CrsDocumentOperation(),
        new BoldDocumentOperation(),
        new SplitNameDocumentOperation()
    };
  }

  /**
   * Returns a string containing the team members of the Geneious Plugin V2 project.
   */
  @Override
  public String getAuthors() {
    return "Rudy Broekhuizen, Wilfred Gerritsen, Ayco Holleman, Judith Slaa, Chantal SlegtenHorst, Oscar Vorst";
  }

  /**
   * Provides a minimal description of the plugin
   */
  @Override
  public String getDescription() {
    return "Enriches nucleotide sequences with annotations from various sources";
  }

  /**
   * Currently just returns the description.
   */
  @Override
  public String getHelp() {
    return getDescription();
  }

  /**
   * Return the highest version of the Geneious API that the plugin is compatible with.
   */
  @Override
  public int getMaximumApiVersion() {
    return Geneious.getMajorApiVersion();
  }

  /**
   * Return the lowest version of the Geneious API that the plugin is compatible with.
   */
  @Override
  public String getMinimumApiVersion() {
    return Geneious.getApiVersion();
  }

  /**
   * Returns the display name of the plugin: &#34;Naturalis Geneious Plugin&#34;.
   */
  @Override
  public String getName() {
    return "Naturalis Geneious Plugin";
  }

  /**
   * Returns the current version of the plugin.
   */
  @Override
  public String getVersion() {
    // Geneious does not allow version strings like V2.0.0-ALPHA. Only 2.0.0 is allowed in this particular
    // example.
    String version = PluginInfo.getInstance().getVersion();
    int i = version.indexOf('-');
    if (i == -1) {
      return version.substring(1);
    }
    return version.substring(1, i);
  }
}
