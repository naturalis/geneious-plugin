package nl.naturalis.geneious;

import javax.swing.Icon;

import com.biomatters.geneious.publicapi.plugin.PluginPreferences;

public class NaturalisPluginPreferences extends PluginPreferences<NaturalisPreferencesOptions> {

  public NaturalisPluginPreferences() {
    super();
  }

  @Override
  protected NaturalisPreferencesOptions createOptions() {
    return new NaturalisPreferencesOptions();
  }

  @Override
  public String getTabName() {
    return "Naturalis";
  }

  @Override
  public Icon getTabIcon() {
    return null; // NaturalisIcons.NATURALIS.getIcon16();
  }

}
