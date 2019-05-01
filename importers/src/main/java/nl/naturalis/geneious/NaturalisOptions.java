package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.plugin.Options;

import nl.naturalis.geneious.note.AnnotationMetadataUpdater;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static nl.naturalis.geneious.Setting.AB1_EXTS;
import static nl.naturalis.geneious.Setting.DEBUG;
import static nl.naturalis.geneious.Setting.DELETE_TMP_FASTAS;
import static nl.naturalis.geneious.Setting.DISABLE_FASTA_CACHE;
import static nl.naturalis.geneious.Setting.FASTA_EXTS;
import static nl.naturalis.geneious.Setting.LAST_FINISHED;
import static nl.naturalis.geneious.Setting.MIN_WAIT_TIME;
import static nl.naturalis.geneious.Settings.settings;

public class NaturalisOptions extends Options {

  public NaturalisOptions() {

    super();

    addHiddenOptions();

    addGeneralOptions();

    addAb1FastaImportOptions();

    addVersionInfo();

    addTeamInfo();

  }

  private void addHiddenOptions() {
    final StringOption lastFinished = addStringOption(LAST_FINISHED.getName(), "", "");
    lastFinished.setHidden();
    settings().update(LAST_FINISHED, lastFinished.getValue());
    lastFinished.addChangeListener(() -> settings().update(LAST_FINISHED, lastFinished.getValue()));
  }

  private void addGeneralOptions() {
    addDivider("General ");

    BooleanOption debug = addBooleanOption(DEBUG.getName(), "Show debug info in plugin logs", FALSE);
    debug.setHelp("Provide more detailed information as the Naturalis plugin is working. Enable when creating "
        + "support tickets for bugs or perfomance issues.");
    settings().update(DEBUG, debug.getValue());
    debug.addChangeListener(() -> settings().update(DEBUG, debug.getValue()));

    IntegerOption minWaitTime = addIntegerOption(MIN_WAIT_TIME.getName(), "Min. wait time (secs)", 60, 1, Integer.MAX_VALUE);
    minWaitTime.setHelp("The minimum amount of time (in seconds) the plugin will force you to wait between two operations "
        + "(e.g. a Sample sheet import followed by a CRS import). This setting is a work-around for a Geneious bug and will be "
        + "removed once the bug has been fixed. Warning: if you set the minimum wait time too low, you may end up with wrongly "
        + "annotated documents!");
    settings().update(MIN_WAIT_TIME, minWaitTime.getValue());
    minWaitTime.addChangeListener(() -> settings().update(MIN_WAIT_TIME, minWaitTime.getValue()));

    ButtonOption general04 = addButtonOption("foo", "", "Update annotation metadata");
    general04.addActionListener(e -> AnnotationMetadataUpdater.saveFieldDefinitions());
  }

  private void addAb1FastaImportOptions() {
    addDivider("AB1/Fasta import ");

    BooleanOption disableFastaCache = addBooleanOption(DISABLE_FASTA_CACHE.getName(), "Disable fasta cache", FALSE);
    disableFastaCache.setHelp("When importing fasta files, they are first split into single nucleotide sequences. If the "
        + "resulting number of sequences is less than 500, they are processed in-memory; otherwise the nucleotide sequence is "
        + "written to a temporary file. This option allows you to force the plugin to always create temporary files so you could "
        + "inspect or use them afterwards.");
    settings().update(DISABLE_FASTA_CACHE, disableFastaCache.getValue());
    disableFastaCache.addChangeListener(() -> settings().update(DISABLE_FASTA_CACHE, disableFastaCache.getValue()));

    BooleanOption deleteTmpFastas = addBooleanOption(DELETE_TMP_FASTAS.getName(), "Delete temporary fasta files", TRUE);
    deleteTmpFastas.setHelp("When you import a large number of fasta files at once, a lot of temporary files will be created. "
        + "These are deleted once the import is finished. This option allows you to keep them on the file system. The plugin will "
        + "tell you where they are. Make sure you delete them yourself when you are done with them.");
    settings().update(DELETE_TMP_FASTAS, deleteTmpFastas.getValue());
    deleteTmpFastas.addChangeListener(() -> settings().update(DELETE_TMP_FASTAS, deleteTmpFastas.getValue()));

    StringOption fastaExts = addStringOption(FASTA_EXTS.getName(), "Fasta file extensions", "fas,fasta");
    fastaExts.setHelp("A comma-separated list of valid file extensions for Fasta files. You can leave this field empty or "
        + "enter '*'. In any case only files whose first character is '>' will be considered for import");
    settings().update(FASTA_EXTS, fastaExts.getValue());
    fastaExts.addChangeListener(() -> settings().update(FASTA_EXTS, fastaExts.getValue()));

    StringOption ab1Exts = addStringOption(AB1_EXTS.getName(), "AB1 file extendsions", "ab1,ab1 (reversed)");
    ab1Exts.setHelp("A comma-separated list of valid file extensions for AB1 files. You can leave this field empty or enter '*'.");
    settings().update(AB1_EXTS, ab1Exts.getValue());
    ab1Exts.addChangeListener(() -> settings().update(AB1_EXTS, ab1Exts.getValue()));
  }

  private void addVersionInfo() {
    addDivider("Version Info ");
    addLabel("Version: " + PluginInfo.getInstance().getVersion());
    addLabel("Release date: " + PluginInfo.getInstance().getBuildDate());
    addLabel("Git branch: " + PluginInfo.getInstance().getGitBranch());
    addLabel("Git commit: " + PluginInfo.getInstance().getGitCommit());
    addLabel("Build number: " + PluginInfo.getInstance().getCommitCount());
  }

  private void addTeamInfo() {
    addDivider("Team ");
    addLabel("Rudi Broekhuizen * Wilfred Gerritsen * Ayco Holleman * Judith Slaa * Chantal SlegtenHorst * Oscar Vorst");
  }

}
