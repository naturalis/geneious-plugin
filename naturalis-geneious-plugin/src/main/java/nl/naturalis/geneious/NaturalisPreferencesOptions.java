package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.Options;

public class NaturalisPreferencesOptions extends Options {

  public static class State {
    private boolean debug;
    private String fastaExtensions;
    private String ab1Extensions;

    public boolean isDebug() {
      return debug;
    }

    public String getFastaExtensions() {
      return fastaExtensions;
    }

    public String getAb1Extensions() {
      return ab1Extensions;
    }
  }

  public static final State STATE = new State();

  public NaturalisPreferencesOptions() {
    super();
    init();
  }

  private void init() {

    addDivider("General Options ");
    BooleanOption bOpt1 = addBooleanOption("nl.naturalis.geneious.log.debug", "Show debug info in plugin logs", false);
    bOpt1.setHelp("Show debug info in plugin logs. Enable when creating support tickets for bugs or perfomance issues.");
    bOpt1.addChangeListener(() -> STATE.debug = bOpt1.getValue().booleanValue());
    StringOption sOpt1 = addStringOption("nl.naturalis.geneious.fasta.extension", "Fasta file extensions", "fas,fasta");
    sOpt1.setHelp("A comma-separated list of valid file extensions for Fasta files.");
    sOpt1.addChangeListener(() -> STATE.fastaExtensions = sOpt1.getValue());
    StringOption sOpt2 = addStringOption("nl.naturalis.geneious.ab1.extension", "AB1 file extendsions", "ab1,ab1 (reversed)");
    sOpt2.setHelp("A comma-separated list of valid file extensions for AB1 files.");
    sOpt2.addChangeListener(() -> STATE.ab1Extensions = sOpt2.getValue());

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
