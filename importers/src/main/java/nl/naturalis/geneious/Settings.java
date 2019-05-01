package nl.naturalis.geneious;

import java.time.LocalDateTime;
import java.util.EnumMap;

import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.Options;

import org.apache.commons.lang3.StringUtils;

import jebl.evolution.io.FastaImporter;
import nl.naturalis.geneious.seq.SequenceImportDocumentOperation;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

import static com.biomatters.geneious.publicapi.plugin.PluginUtilities.getPluginForDocumentOperation;

import static nl.naturalis.geneious.Setting.AB1_EXTS;
import static nl.naturalis.geneious.Setting.DEBUG;
import static nl.naturalis.geneious.Setting.DELETE_TMP_FASTAS;
import static nl.naturalis.geneious.Setting.DISABLE_FASTA_CACHE;
import static nl.naturalis.geneious.Setting.FASTA_EXTS;
import static nl.naturalis.geneious.Setting.LAST_FINISHED;
import static nl.naturalis.geneious.Setting.MIN_WAIT_TIME;

/**
 * An easy means of accessing the settings in the <i>Tools -> Preferences</i> tab. This class mainly exists to work
 * around a Geneious bug that makes directly accessing the options defined in the Preferences tab very tricky. BE
 * CAREFUL WHEN TRYING TO MAKE THIS CODE LEANER OR MORE EFFICIENT. Notably tricky are the hidden settings in the
 * <i>Tools -> Preferences</i> panel. For these there are setters in this class, which update the value of the panel's
 * Swing component. The Swing component then fires a change listener which updates the {@code Settings} cache by calling
 * the {@link #update(Setting, Object) update}. That sounds like it could be short-circuited. Manage your expectations
 * if you try. See also
 * {@linkplain https://support.geneious.com/hc/en-us/community/posts/360043526672--Error-whilst-reading-memory-file-when-clicking-OK-or-Apply-in-Preferences-tabs}.
 *
 * @author Ayco Holleman
 */
/*
 */
public class Settings {

  private static Settings instance;

  /**
   * Returns the singleton instance of the {@code Settings} class.
   * 
   * @return
   */
  public static Settings settings() {
    if (instance == null) {
      instance = new Settings();
    }
    return instance;
  }

  private final EnumMap<Setting, Object> cache = new EnumMap<>(Setting.class);

  private Settings() {}

  /**
   * Callback method for the change listeners in {@link NaturalisOptions}.
   * 
   * @param setting
   * @param value
   */
  public void update(Setting setting, Object value) {
    System.out.println(String.format("%s updated to: \"%s\"", setting, value, value.getClass().getName()));
    cache.put(setting, value);
  }

  /**
   * Returns the end time of the most recently completed operation.
   * 
   * @return
   */
  public LocalDateTime getLastFinished() {
    String s = (String) cache.get(LAST_FINISHED);
    if (StringUtils.isBlank(s)) {
      return now().minusYears(1).truncatedTo(SECONDS);
    }
    return LocalDateTime.parse(s);
  }

  /**
   * Sets the end time of the most recently completed operation.
   * 
   * @param timestamp
   */
  public void setLastFinished(LocalDateTime timestamp) {
    String s = timestamp.toString();
    GeneiousPlugin me = getPluginForDocumentOperation(new SequenceImportDocumentOperation());
    Options opts = me.getPluginPreferences().get(0).getActiveOptions();
    opts.getOption(LAST_FINISHED.getName()).setValue(s);
    opts.savePreferences();
  }

  /**
   * Whether or now to show DEBUG messages in the execution logs.
   * 
   * @return
   */
  public boolean isDebug() {
    return (Boolean) cache.get(DEBUG);
  }

  /**
   * Returns the minium wait time (in seconds) between operations.
   * 
   * @return
   */
  public int getMinWaitTime() {
    return (Integer) cache.get(MIN_WAIT_TIME);
  }

  /**
   * Returns comma-separated extensions for AB1 files.
   * 
   * @return
   */
  public String getAb1FileExtensions() {
    return (String) cache.get(AB1_EXTS);
  }

  /**
   * Returns comma-separated extensions for fasta files.
   * 
   * @return
   */
  public String getFastaFileExtensions() {
    return (String) cache.get(FASTA_EXTS);
  }

  /**
   * Whether or not to disable the fasta cache.
   * 
   * @return
   */
  public boolean isDisableFastaCache() {
    return (Boolean) cache.get(DISABLE_FASTA_CACHE);
  }

  /**
   * Whether or not to delete the temporary fasta files created by the {@link FastaImporter}.
   * 
   * @return
   */
  public boolean isDeleteTmpFastas() {
    return (Boolean) cache.get(DELETE_TMP_FASTAS);
  }

}
