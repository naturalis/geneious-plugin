package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * Contains the user input for the Split Name operation.
 *
 * @author Ayco Holleman
 */
class NameSplitterConfig {

  private List<AnnotatedPluginDocument> selectedDocuments;
  private boolean ignoreDocsWithNaturalisNote;

  List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  void setSelectedDocuments(List<AnnotatedPluginDocument> selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
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
