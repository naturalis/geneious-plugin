package nl.naturalis.geneious.split;

import nl.naturalis.geneious.OperationConfig;

/**
 * Contains the user input for the Split Name operation.
 *
 * @author Ayco Holleman
 */
class SplitNameConfig extends OperationConfig {

  private boolean ignoreDocsWithNaturalisNote;
  
  SplitNameConfig() {
    super(); // initializes target folder & selected documents
  }

  /**
   * Whether or not to ignore documents that already have annotations (their names will not be split again).
   * 
   * @return
   */
  boolean isIgnoreDocsWithNaturalisNote() {
    return ignoreDocsWithNaturalisNote;
  }

  /**
   * Sets whether or not to ignore documents that already have annotations.
   * 
   * @param ignoreDocsWithNaturalisNote
   */
  void setIgnoreDocsWithNaturalisNote(boolean ignoreDocsWithNaturalisNote) {
    this.ignoreDocsWithNaturalisNote = ignoreDocsWithNaturalisNote;
  }

}
