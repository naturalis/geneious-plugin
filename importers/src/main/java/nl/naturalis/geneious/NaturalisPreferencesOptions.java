package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.Options;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.note.FieldDefinitionPersister;

public class NaturalisPreferencesOptions extends Options {

  private static class State {
    private Boolean debug = Boolean.FALSE;
    private Boolean disableFastaCache;
    private Boolean deleteTmpFastaFiles;
    private String fastaExtensions;
    private String ab1Extensions;
  }

  private static final State state = new State();

  public static boolean isDebug() {
    return state.debug.booleanValue();
  }

  public static boolean disableFastaCache() {
    return state.disableFastaCache.booleanValue();
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
        addBooleanOption("nl.naturalis.geneious.log.disableFastaCache", "Disable fasta cache", Boolean.FALSE);
    bOpt2.setHelp("When importing fasta files, they are first split into single nucleotide sequences. If you import "
        + "fewer than 500 fasta files, this is all done in-memory; otherwise the nucleotide sequence is written to a "
        + "temporary file. This option allows you to force the plugin to always create temporary files so you could "
        + "inspect or use them afterwards");
    bOpt2.addChangeListener(() -> state.disableFastaCache = bOpt2.getValue());
    state.disableFastaCache = bOpt2.getValue();

    BooleanOption bOpt3 =
        addBooleanOption("nl.naturalis.geneious.log.deleteTmpFastaFiles", "Delete intermediate fasta files", Boolean.TRUE);
    bOpt3.setHelp("When you import a large number of fasta files at once, a lot of temporary files will be created. "
        + "Ordinarily these temporary fasta files are deleted once the import is finished. This option allows you to "
        + "keep them on the file system. The plugin will tell you where they are. Please make sure you delete them "
        + "yourself when you are done with them!");
    bOpt3.addChangeListener(() -> state.deleteTmpFastaFiles = bOpt3.getValue());
    state.deleteTmpFastaFiles = bOpt3.getValue();

    StringOption sOpt1 = addStringOption("nl.naturalis.geneious.fasta.extension", "Fasta file extensions", "fas,fasta");
    sOpt1.setHelp("A comma-separated list of valid file extensions for Fasta files. You can leave this field empty or "
        + "enter '*'. In any case only files whose first character is '>' will be considered for import");
    sOpt1.addChangeListener(() -> state.fastaExtensions = sOpt1.getValue());
    state.fastaExtensions = sOpt1.getValue();

    StringOption sOpt2 = addStringOption("nl.naturalis.geneious.ab1.extension", "AB1 file extendsions", "ab1,ab1 (reversed)");
    sOpt2.setHelp("A comma-separated list of valid file extensions for AB1 files. You can leave this field empty or enter '*'.");
    sOpt2.addChangeListener(() -> state.ab1Extensions = sOpt2.getValue());
    state.ab1Extensions = sOpt2.getValue();

    ButtonOption butOpt1 =
        addButtonOption("nl.naturalis.geneious.regenerateAnnotationMetadata", "", "Regenerate annotation metadata");
    butOpt1.addActionListener(e -> FieldDefinitionPersister.saveFieldDefinitions());

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
