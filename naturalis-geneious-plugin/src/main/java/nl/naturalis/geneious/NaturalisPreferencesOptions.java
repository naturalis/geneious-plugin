package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.Options;

public class NaturalisPreferencesOptions extends Options {

  public static class State {
    private boolean debug;

    public boolean isDebug() {
      return debug;
    }
  }

  public static final State STATE = new State();

  public NaturalisPreferencesOptions() {
    super();
    init();
  }

  private void init() {

    addDivider("General Options ");
    BooleanOption opt = addBooleanOption("nl.naturalis.geneious.log.debug", "Show debug info in plugin logs", false);
    opt.addChangeListener(() -> STATE.debug = opt.getValue().booleanValue());

    addDivider("Version Info ");
    addLabel("Version: " + PluginInfo.getInstance().getVersion());
    addLabel("Build time: " + PluginInfo.getInstance().getBuildDate());
    addLabel("Git branch: " + PluginInfo.getInstance().getGitBranch());
    addLabel("Git commit: " + PluginInfo.getInstance().getGitCommit());

    addDivider("Team ");
    addLabel("Rudi Broekhuizen");
    addLabel("Wilfred Gerritsen");
    addLabel("Ayco Holleman");
    addLabel("Judith Slaa");
    addLabel("Chantal SlegtenHorst");
    addLabel("Nick Stolk");
    addLabel("Oscar Vorst");

  }

}
