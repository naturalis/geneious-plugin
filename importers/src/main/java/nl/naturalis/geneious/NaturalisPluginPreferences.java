package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.PluginPreferences;

/**
 * Provides global information about the <i>Tools -&gt; Preferences</i> panel (e&#46;g&#46; the text to be displayed on the
 * panel's tab).
 * 
 * @author Ayco Holleman
 *
 */
public class NaturalisPluginPreferences extends PluginPreferences<NaturalisOptions> {

  public NaturalisPluginPreferences() {
    super();
  }

  /**
   * Returns the text to be displayed on the panel's tab: &#34;Naturalis&#34;.
   */
  @Override
  public String getTabName() {
    return "Naturalis";
  }

  /**
   * Returns the actual configuration for the <i>Tools -&gt; Preferences panel.
   */
  @Override
  protected NaturalisOptions createOptions() {
    return new NaturalisOptions();
  }

}
