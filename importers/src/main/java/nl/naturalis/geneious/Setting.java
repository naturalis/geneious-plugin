package nl.naturalis.geneious;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Symbolic constants for configuration settings for the Naturalis plugin.
 *
 * @author Ayco Holleman
 */
public enum Setting {

  /**
   * Whether or not to show DEBUG messages in the log file.
   */
  DEBUG("nl.naturalis.geneious.log.debug"),
  /**
   * Show pretty notes when in DEBUG mode.
   */
  PRETTY_NOTES("nl.naturalis.geneious.log.prettyNotes"),
  /**
   * The value to wait for as an indication that indexing is complete.
   */
  PING_TIME("nl.naturalis.geneious.pingTime"),

  /**
   * AB1 file extensions.
   */
  AB1_EXTS("nl.naturalis.geneious.seq.ext.ab1"),

  /**
   * Fasta file extensions.
   */
  FASTA_EXTS("nl.naturalis.geneious.seq.ext.fasta"),

  /**
   * Always write fasta sequences to temporary files.
   */
  DISABLE_FASTA_CACHE("nl.naturalis.geneious.seq.disableFastaCache"),

  /**
   * Remove tempoerary fasta files from file system when done.
   */
  DELETE_TMP_FASTAS("nl.naturalis.geneious.seq.deleteTmpFastas");

  private static final HashMap<String, Setting> reverse = new HashMap<>(values().length, 1F);

  static {
    Arrays.stream(values()).forEach(s -> reverse.put(s.getName(), s));
  }

  /**
   * Returns the setting corresponding to the provided name, which is supposed to be the fully-qualified name known to Geneious.
   * 
   * @param name
   * @return
   */
  public static Setting forName(String name) {
    return reverse.get(name);
  }

  private final String name;

  private Setting(String name) {
    this.name = name;
  }

  /**
   * Returns the fully-qualified name by which Geneious knows this configuration setting.
   * 
   * @return
   */
  public String getName() {
    return name;
  }

}
