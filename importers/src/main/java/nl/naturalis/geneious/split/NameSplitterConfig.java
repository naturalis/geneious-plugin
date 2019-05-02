package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

class NameSplitterConfig {

  private List<AnnotatedPluginDocument> selectedDocuments;
  private boolean ignoreDocsWithNaturalisNote;
  private boolean ignoreDocsWithoutSuffix;

  List<AnnotatedPluginDocument> getSelectedDocuments() {
    return selectedDocuments;
  }

  void setSelectedDocuments(List<AnnotatedPluginDocument> selectedDocuments) {
    this.selectedDocuments = selectedDocuments;
  }

  boolean isIgnoreDocsWithNaturalisNote() {
    return ignoreDocsWithNaturalisNote;
  }

  void setIgnoreDocsWithNaturalisNote(boolean ignoreDocsWithNaturalisNote) {
    this.ignoreDocsWithNaturalisNote = ignoreDocsWithNaturalisNote;
  }

  boolean isIgnoreDocsWithoutSuffix() {
    return ignoreDocsWithoutSuffix;
  }

  void setIgnoreDocsWithoutSuffix(boolean ignoreDocsWithoutSuffix) {
    this.ignoreDocsWithoutSuffix = ignoreDocsWithoutSuffix;
  }

}
