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
    
    options.addDivider("General Options ");
    options.addBooleanOption("nl.naturalis.geneious.log.debug", "Show debug info in plugin logs", false);
    
    options.addDivider("Version Info ");
    options.addLabel("Version: " + props.getProperty("git.closest.tag.name"));
    options.addLabel("Build time: " + props.getProperty("git.build.time"));
    options.addLabel("Git branch: " + props.getProperty("git.branch"));
    options.addLabel("Git commit: " + props.getProperty("git.commit.id.abbrev"));
    
    options.addDivider("Team ");
    options.addLabel("Rudy Broekhuizen");
    options.addLabel("Wilfred Gerritsen");
    options.addLabel("Ayco Holleman");
    options.addLabel("Judith Slaa");
    options.addLabel("Chantal SlegtenHorst");
    options.addLabel("Nick Stolk");
    options.addLabel("Oscar Vorst");
   
    return options;
  }

  @Override
  public String getTabName() {
    return "Naturalis";
  }

}
