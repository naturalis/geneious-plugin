package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.Options;

import nl.naturalis.geneious.gui.log.GuiLogManager;

public class NaturalisPreferencesOptions extends Options {

  private static class State {
    private Boolean debug;
    private Boolean deleteTmpFastaFiles;
    private String fastaExtensions;
    private String ab1Extensions;
  }

  private static final State state = new State();

  public static boolean isDebug() {
    return state.debug.booleanValue();
  }

  public static boolean deleteTmpFastaFiles() {
    return state.deleteTmpFastaFiles.booleanValue();
  }

  public static String getFastaExtensions() {
    return state.fastaExtensions;
  }

  public static String getAb1Extensions() {
    return state.ab1Extensions;
  }

  public NaturalisPreferencesOptions() {
    super();
    init();
  }

  private void init() {

    addDivider("General Options ");
    BooleanOption bOpt1 = addBooleanOption("nl.naturalis.geneious.log.debug", "Show debug info in plugin logs", Boolean.FALSE);
    bOpt1.setHelp("Provide more detailed information as the Naturalis plugin is working.  Enable when creating "
        + "support tickets for bugs or perfomance issues.");
    bOpt1.addChangeListener(() -> {
      state.debug = bOpt1.getValue();
      GuiLogManager.setDebug(state.debug.booleanValue());
    });
    state.debug = bOpt1.getValue();
    GuiLogManager.setDebug(state.debug.booleanValue());

    BooleanOption bOpt2 =
        addBooleanOption("nl.naturalis.geneious.log.deleteTmpFastaFiles", "Delete intermediate fasta files", Boolean.TRUE);
    bOpt2.setHelp("When importing fasta files, they are first split into intermediate fasta files containing only one "
        + "nucleotide sequence. Even files that already contain just a single sequence are still copied to a temporary "
        + "location. Ordinarily the intermediate fasta files are deleted once the import completes. This option allows "
        + "you to you to leave them hanging around on the file system. You must then manually delete them to prevent "
        + "clogging up the file system");
    bOpt2.addChangeListener(() -> state.deleteTmpFastaFiles = bOpt2.getValue());
    state.deleteTmpFastaFiles = bOpt2.getValue();

    StringOption sOpt1 = addStringOption("nl.naturalis.geneious.fasta.extension", "Fasta file extensions", "fas,fasta");
    sOpt1.setHelp("A comma-separated list of valid file extensions for Fasta files. You can leave this field empty or "
        + "enter '*'. In any case only files whose first character is '>' will be considered for import");
    sOpt1.addChangeListener(() -> state.fastaExtensions = sOpt1.getValue());
    state.fastaExtensions = sOpt1.getValue();

    StringOption sOpt2 = addStringOption("nl.naturalis.geneious.ab1.extension", "AB1 file extendsions", "ab1,ab1 (reversed)");
    sOpt2.setHelp("A comma-separated list of valid file extensions for AB1 files. You can leave this field empty or enter '*'.");
    sOpt2.addChangeListener(() -> state.ab1Extensions = sOpt2.getValue());
    state.ab1Extensions = sOpt2.getValue();

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
