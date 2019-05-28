package nl.naturalis.geneious.util;

import nl.naturalis.geneious.log.GuiLogger;

/**
 * Utility class aimed at logging statistics for operations that deal with CSV files / spreadsheets.
 * 
 * @author Ayco Holleman
 *
 */
public class CommonStatistics {

  private int good;
  private int bad;
  private int unused;
  private int selected;
  private int updated;
  private int unchanged;

  public CommonStatistics() {}

  /**
   * Statistics about the rows in the source file.
   * 
   * @param good
   * @param bad
   * @param unused
   * @return
   */
  public CommonStatistics rowStats(int good, int bad, int unused) {
    this.good = good;
    this.bad = bad;
    this.unused = unused;
    return this;
  }

  /**
   * Statistics about the affected documents.
   * 
   * @param selected
   * @param updated
   * @param unchanged
   * @return
   */
  public CommonStatistics documentStats(int selected, int updated) {
    this.selected = selected;
    this.updated = updated;
    this.unchanged = selected - updated;
    return this;
  }

  /**
   * Writes out the statistics using the provided logger.
   * 
   * @param logger
   */
  public void write(GuiLogger logger) {
    logger.info("Number of valid rows ............: %3d", good);
    logger.info("Number of empty/bad rows ........: %3d", bad);
    logger.info("Number of unused rows ...........: %3d", unused);
    logger.info("Number of selected documents ....: %3d", selected);
    logger.info("Number of updated documents .....: %3d", updated);
    logger.info("Number of unchanged documents ...: %3d", unchanged);
  }

}
