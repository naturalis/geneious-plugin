package nl.naturalis.geneious.trace;

/**
 * Maintains counters for the AB1/Fasta import process
 */
class ImportStats {

  /**
   * Number of files processed.
   */
  int processed;
  /**
   * Number of files imported into Geneious.
   */
  int imported;
  /**
   * Number of files rejected by Geneious or the Naturalis plugin.
   */
  int rejected;
  /**
   * Number of files imported with Naturalis-specific annotations derived from the name of the trace file.
   */
  int enriched;

  ImportStats() {}

  void merge(ImportStats other) {
    processed += other.processed;
    imported += other.imported;
    rejected += other.rejected;
    enriched += other.enriched;
  }

}
