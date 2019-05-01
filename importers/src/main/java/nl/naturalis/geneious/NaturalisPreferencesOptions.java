package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.Options;

import nl.naturalis.geneious.gui.SettingsEditor;
import nl.naturalis.geneious.note.AnnotationMetadataUpdater;

import static nl.naturalis.geneious.Setting.DEBUG;
import static nl.naturalis.geneious.Setting.LAST_FINISHED;
import static nl.naturalis.geneious.Setting.MIN_WAIT_TIME;
import static nl.naturalis.geneious.Settings.settings;

public class NaturalisPreferencesOptions extends Options {

  private static class State {
    private Boolean disableFastaCache;
    private Boolean deleteTmpFastaFiles;
    private String fastaExtensions;
    private String ab1Extensions;
  }

  private static final State state = new State();

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

  public BooleanOption optDebug;

  public NaturalisPreferencesOptions() {

    super();

    // HIDDEN SETTINGS
    StringOption optLastFinished = addStringOption(LAST_FINISHED.getName(), "", "");
    optLastFinished.setHidden();
    optLastFinished.addChangeListener(()->settings().update(LAST_FINISHED, optLastFinished.getValue()));
    settings().update(LAST_FINISHED, optLastFinished.getValue());

    // GENERAL SETTINGS
    addDivider("General ");

    optDebug = addBooleanOption(DEBUG.getName(), "Show debug info in plugin logs", Boolean.FALSE);
    optDebug.setHelp("Provide more detailed information as the Naturalis plugin is working. Enable when creating "
        + "support tickets for bugs or perfomance issues.");

    IntegerOption optMinWaitTime = addIntegerOption(MIN_WAIT_TIME.getName(), "Min. wait time (secs)", 60, 1, Integer.MAX_VALUE);
    optMinWaitTime.setHelp("The minimum amount of time (in seconds) the plugin will force you to wait between two operations "
        + "(e.g. a Sample sheet import followed by a CRS import). This setting is a work-around for a Geneious bug and will be "
        + "removed once the bug has been fixed. Warning: if you set the minimum wait time too low, you may end up with wrongly "
        + "annotated documents!");
    optMinWaitTime.addChangeListener(() -> settings().update(MIN_WAIT_TIME, optMinWaitTime.getValue()));
    settings().update(MIN_WAIT_TIME, optMinWaitTime.getValue());

    ButtonOption general04 =
        addButtonOption("nl.naturalis.geneious.updateAnnotationMetadata", "", "Update annotation metadata");
    general04.addActionListener(e -> AnnotationMetadataUpdater.saveFieldDefinitions());

    ButtonOption general05 = addButtonOption("nl.naturalis.geneious.settings", "", "Advanced settings ...");
    general05.addActionListener(e -> new SettingsEditor().show());

    // AB1/FASTA IMPORT SETTINGS
    addDivider("AB1/Fasta import ");

    BooleanOption ab1Fasta01 =
        addBooleanOption("nl.naturalis.geneious.log.disableFastaCache", "Disable fasta cache", Boolean.FALSE);
    ab1Fasta01.setHelp("When importing fasta files, they are first split into single nucleotide sequences. If you import "
        + "fewer than 500 fasta files, this is all done in-memory; otherwise the nucleotide sequence is written to a "
        + "temporary file. This option allows you to force the plugin to always create temporary files so you could "
        + "inspect or use them afterwards.");
    ab1Fasta01.addChangeListener(() -> state.disableFastaCache = ab1Fasta01.getValue());
    state.disableFastaCache = ab1Fasta01.getValue();

    BooleanOption ab1Fasta02 =
        addBooleanOption("nl.naturalis.geneious.log.deleteTmpFastaFiles", "Delete intermediate fasta files", Boolean.TRUE);
    ab1Fasta02.setHelp("When you import a large number of fasta files at once, a lot of temporary files will be created. "
        + "Ordinarily these temporary fasta files are deleted once the import is finished. This option allows you to "
        + "keep them on the file system. The plugin will tell you where they are. Please make sure you delete them "
        + "yourself when you are done with them.");
    ab1Fasta02.addChangeListener(() -> state.deleteTmpFastaFiles = ab1Fasta02.getValue());
    state.deleteTmpFastaFiles = ab1Fasta02.getValue();

    StringOption ab1Fasta03 = addStringOption("nl.naturalis.geneious.fasta.extension", "Fasta file extensions", "fas,fasta");
    ab1Fasta03.setHelp("A comma-separated list of valid file extensions for Fasta files. You can leave this field empty or "
        + "enter '*'. In any case only files whose first character is '>' will be considered for import");
    ab1Fasta03.addChangeListener(() -> state.fastaExtensions = ab1Fasta03.getValue());
    state.fastaExtensions = ab1Fasta03.getValue();

    StringOption ab1Fasta04 = addStringOption("nl.naturalis.geneious.ab1.extension", "AB1 file extendsions", "ab1,ab1 (reversed)");
    ab1Fasta04.setHelp("A comma-separated list of valid file extensions for AB1 files. You can leave this field empty or enter '*'.");
    ab1Fasta04.addChangeListener(() -> state.ab1Extensions = ab1Fasta04.getValue());
    state.ab1Extensions = ab1Fasta04.getValue();

    // VERSION INFO
    addDivider("Version Info ");
    addLabel("Version: " + PluginInfo.getInstance().getVersion());
    addLabel("Release date: " + PluginInfo.getInstance().getBuildDate());
    addLabel("Git branch: " + PluginInfo.getInstance().getGitBranch());
    addLabel("Git commit: " + PluginInfo.getInstance().getGitCommit());
    addLabel("Build number: " + PluginInfo.getInstance().getCommitCount());

    // TEAM
    addDivider("Team ");
    addLabel("Rudi Broekhuizen * Wilfred Gerritsen * Ayco Holleman * Judith Slaa * Chantal SlegtenHorst * Oscar Vorst");

  }

}
