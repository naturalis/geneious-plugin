package nl.naturalis.geneious;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static nl.naturalis.geneious.Setting.AB1_EXTS;
import static nl.naturalis.geneious.Setting.DEBUG;
import static nl.naturalis.geneious.Setting.DELETE_TMP_FASTAS;
import static nl.naturalis.geneious.Setting.DISABLE_FASTA_CACHE;
import static nl.naturalis.geneious.Setting.FASTA_EXTS;
import static nl.naturalis.geneious.Setting.MARKER_MAP;
import static nl.naturalis.geneious.Setting.PING_HISTORY;
import static nl.naturalis.geneious.Setting.PRETTY_NOTES;
import static nl.naturalis.geneious.Settings.settings;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.biomatters.geneious.publicapi.plugin.Options;
import com.google.common.base.Charsets;

import nl.naturalis.geneious.note.AnnotationMetadataUpdater;
import nl.naturalis.geneious.util.Ping;

/**
 * A subclass of {@code Options} underpinning the <i>Tools -&gt; Preferences</i> panel.
 * 
 * @author Ayco Holleman
 *
 */
public class GlobalOptions extends Options {

  /**
   * Configures up the <i>Tools -&gt; Preferences</i> panel.
   */
  public GlobalOptions() {

    super();

    addHiddenOptions();

    addLoggingOptions();

    addAb1FastaImportOptions();

    addBoldOptions();

    addGeneralOptions();

    addVersionInfo();

    addTeamInfo();

  }

  private void addHiddenOptions() {
    StringOption pingTime = addStringOption(PING_HISTORY.getName(), "", "");
    pingTime.setHidden();
    settings().update(PING_HISTORY, pingTime.getValue());
    pingTime.addChangeListener(() -> settings().update(PING_HISTORY, pingTime.getValue()));
  }

  private void addGeneralOptions() {
    addDivider("General ");
    beginAlignHorizontally();

    ButtonOption clearPingdata = addButtonOption("foo-0", "", "Clear ping history");
    clearPingdata.setHelp("Press this button if you accidentally deleted a \"ping\" folder while waiting for document "
        + "indexing to complete. Make sure all documents have been indexed before continuing to use the plugin");
    clearPingdata.addActionListener(e -> Ping.clear());

    ButtonOption updateMetadata = addButtonOption("foo-1", "", "Update annotation metadata");
    updateMetadata.addActionListener(e -> AnnotationMetadataUpdater.saveFieldDefinitions());
    updateMetadata.setHelp("Advanced functionality. Don't do this unless you know what you are doing.");

    endAlignHorizontally();
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

    StringOption fastaExts = addStringOption(FASTA_EXTS.getName(), "Fasta file extensions", "fas,fasta,txt");
    fastaExts.setHelp("A comma-separated list of valid file extensions for Fasta files. You can leave this field empty or "
        + "enter '*'. In any case only files whose first character is '>' will be considered for import");
    settings().update(FASTA_EXTS, fastaExts.getValue());
    fastaExts.addChangeListener(() -> settings().update(FASTA_EXTS, fastaExts.getValue()));

    StringOption ab1Exts = addStringOption(AB1_EXTS.getName(), "AB1 file extendsions", "ab1,ab1 (reversed)");
    ab1Exts.setHelp("A comma-separated list of valid file extensions for AB1 files. You can leave this field empty or enter '*'.");
    settings().update(AB1_EXTS, ab1Exts.getValue());
    ab1Exts.addChangeListener(() -> settings().update(AB1_EXTS, ab1Exts.getValue()));

  }

  private void addLoggingOptions() {
    addDivider("Logging ");

    BooleanOption debug = addBooleanOption(DEBUG.getName(), "Show debug info in plugin logs", FALSE);
    debug.setHelp("Provide more detailed information as the Naturalis plugin is working. Please, enable when creating "
        + "support tickets for bugs or perfomance issues.");
    settings().update(DEBUG, debug.getValue());

    BooleanOption prettyNotes = addBooleanOption(PRETTY_NOTES.getName(), "Show pretty notes", FALSE);
    prettyNotes.setHelp("When in DEBUG mode, format the notes generated by the plugin in a more readable manner");
    settings().update(PRETTY_NOTES, prettyNotes.getValue());
    prettyNotes.addChangeListener(() -> settings().update(PRETTY_NOTES, prettyNotes.getValue()));
    prettyNotes.setEnabled(debug.getValue());

    debug.addChangeListener(() -> {
      settings().update(DEBUG, debug.getValue());
      prettyNotes.setEnabled(debug.getValue());
    });

  }

  private void addBoldOptions() {
    addDivider("BOLD import ");
    String defaultMap;
    try {
      defaultMap = IOUtils.toString(getClass().getResourceAsStream("/default-marker-map.txt"), Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    MultipleLineStringOption markerMap = addMultipleLineStringOption(MARKER_MAP.getName(), "Marker mappings", defaultMap, 5, false);
    markerMap.setHelp("Provide the BOLD-to-Naturalis marker mappings. BOLD markers must be in the left column. Naturalis markers must "
        + "be in the right column. An arrow (->) must be used to separate the two. To map a BOLD marker to multiple Naturalis markers, "
        + "use a comma to separate the Naturalis markers. For example: COI-5P -> COI, COI-5P. Whitespace and empty lines are ignored. "
        + "Lines starting with a # sign are ignored as well. If the BOLD marker is the same as the Naturalis marker (and has no "
        + "multiple mappings) including the mapping is optional. By default the plugin will assume that the BOLD marker maps to just "
        + "one Naturalis marker with the same name.");
    settings().update(MARKER_MAP, markerMap.getValue());
    markerMap.addChangeListener(() -> settings().update(MARKER_MAP, markerMap.getValue()));
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
