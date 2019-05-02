package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;

class NameSplitterOptions extends Options {

  private final List<AnnotatedPluginDocument> documents;
  private final BooleanOption ignoreWithNaturalisNote;
  private final BooleanOption ignoreWithoutSuffix;

  NameSplitterOptions(List<AnnotatedPluginDocument> documents) {
    this.documents = documents;
    this.ignoreWithNaturalisNote = addIgnoreWithNaturalisNoteOption();
    this.ignoreWithoutSuffix = addIgnoreWithoutSuffix();
  }
  
  NameSplitterConfig createNameSplitterConfig() {
    NameSplitterConfig cfg = new NameSplitterConfig();
    cfg.setSelectedDocuments(documents);
    cfg.setIgnoreDocsWithNaturalisNote(ignoreWithNaturalisNote.getValue());
    cfg.setIgnoreDocsWithoutSuffix(ignoreWithoutSuffix.getValue());
    return cfg;
  }

  private BooleanOption addIgnoreWithoutSuffix() {
    String name = "nl.naturalis.geneious.split.ignoreWithNaturalisNote";
    String label = "Ignore documents with Naturalis annotations";
    BooleanOption opt = addBooleanOption(name, label, Boolean.TRUE);
    return opt;
  }

  private BooleanOption addIgnoreWithNaturalisNoteOption() {
    String name = "nl.naturalis.geneious.split.ignoreWithoutSuffix";
    String label = "Ignore documents without suffix .ab1, (ab1) or (fasta)";
    BooleanOption opt = addBooleanOption(name, label, Boolean.TRUE);
    return opt;
  }

}
