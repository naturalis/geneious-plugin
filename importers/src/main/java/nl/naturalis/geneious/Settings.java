package nl.naturalis.geneious;

import static nl.naturalis.geneious.Setting.AB1_EXTS;
import static nl.naturalis.geneious.Setting.DEBUG;
import static nl.naturalis.geneious.Setting.DELETE_TMP_FASTAS;
import static nl.naturalis.geneious.Setting.DISABLE_FASTA_CACHE;
import static nl.naturalis.geneious.Setting.FASTA_EXTS;
import static nl.naturalis.geneious.Setting.PING_HISTORY;
import static nl.naturalis.geneious.Setting.*;

import java.util.EnumMap;

import jebl.evolution.io.FastaImporter;

/**
 * Provides access to the settings in the <i>Tools -&gt; Preferences</i> tab. This class mainly exists to work around an
 * awkward Geneious feature (if not bug) that makes directly accessing the options defined in the Preferences tab
 * tricky. Notably tricky are the hidden settings. For these there are setters in this class that do not update the
 * {@code Settings} object directly, but in stead update the corresponding option in the Preferences panel. That in turn
 * triggers a change listener in the panel which updates the {@code Settings} object. That sounds like a round-about
 * that could be short-circuited. Be careful if you try.
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
    cache.put(setting, value);
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
   * Returns the ping history as a JSON string.
   * 
   * @return
   */
  public String getPingHistory() {
    return (String) cache.get(PING_HISTORY);
  }

  /**
   * Sets the ping history as a JSON string (so it will survive Geneious sessions). The ping history is a
   * per-user-per-database map of ping start times.
   * 
   * @param history
   */
  public void setPingHistory(String history) {
    cache.put(PING_HISTORY, history);
  }

  /**
   * Returns the BOLD-to-Naturalis marker mappings as a JSON string.
   * 
   * @return
   */
  public String getMarkerMap() {
    return (String) cache.get(MARKER_MAP);
  }

  /**
   * Sets the BOLD-to-Naturalis marker mappings as a JSON string.
   * 
   * @param markerMap
   */
  public void setMarkerMap(String markerMap) {
    cache.put(MARKER_MAP, markerMap);
  }

  /**
   * Whether or not to show pretty notes when in DEBUG mode.
   * 
   * @return
   */
  public boolean isPrettyNotes() {
    return (Boolean) cache.get(PRETTY_NOTES);
  }

  /**
   * Returns a string of comma-separated extensions for AB1 files.
   * 
   * @return
   */
  public String getAb1FileExtensions() {
    return (String) cache.get(AB1_EXTS);
  }

  /**
   * Returns a string of comma-separated extensions for fasta files.
   * 
   * @return
   */
  public String getFastaFileExtensions() {
    return (String) cache.get(FASTA_EXTS);
  }

  /**
   * Whether or not to disable the fasta cache. If disabled, fasta imports will not be done in-memory.
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
