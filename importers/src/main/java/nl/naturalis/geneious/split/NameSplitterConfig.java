package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

class NameSplitterConfig {

  private List<AnnotatedPluginDocument> selectedDocuments;
  private boolean ignoreDocsWithNaturalisNote;
  private boolean ignoreDocsWithSuffix;
  private boolean ignoreDummies;

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

  boolean isIgnoreDocsWithSuffix() {
    return ignoreDocsWithSuffix;
  }

  void setIgnoreDocsWithSuffix(boolean ignoreDocsWithoutSuffix) {
    this.ignoreDocsWithSuffix = ignoreDocsWithoutSuffix;
  }

  public boolean isIgnoreDummies() {
    return ignoreDummies;
  }

  public void setIgnoreDummies(boolean ignoreDummies) {
    this.ignoreDummies = ignoreDummies;

  }

}
