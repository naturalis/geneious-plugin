package nl.naturalis.geneious;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.biomatters.geneious.publicapi.plugin.PluginPreferences;

public class NaturalisPluginPreferences extends PluginPreferences<NaturalisOptions> {

  public NaturalisPluginPreferences() {
    super();
  }

  @Override
  protected NaturalisOptions createOptions() {
    InputStream is = getClass().getResourceAsStream("/git.properties");
    Properties props = new Properties();
    try {
      props.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    NaturalisOptions options = new NaturalisOptions();
    options.addBooleanOption("nl.naturalis.geneious.log.debug", "Show debug info in plugin logs", false);
    return options;
  }

  @Override
  public String getTabName() {
    return "Naturalis";
  }

}
