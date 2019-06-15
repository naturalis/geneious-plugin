package nl.naturalis.geneious;

import javax.swing.Icon;

import com.biomatters.geneious.publicapi.plugin.PluginPreferences;

/**
 * Geneious framework plumbing class.
 * 
 * @author Ayco Holleman
 *
 */
public class NaturalisPluginPreferences extends PluginPreferences<NaturalisOptions> {

  public NaturalisPluginPreferences() {
    super();
  }

  @Override
  protected NaturalisOptions createOptions() {
    return new NaturalisOptions();
  }

  @Override
  public String getTabName() {
    return "Naturalis";
  }

  @Override
  public Icon getTabIcon() {
    return null;
  }

}
