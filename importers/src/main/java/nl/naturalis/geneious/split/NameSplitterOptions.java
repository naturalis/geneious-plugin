package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;

class NameSplitterOptions extends Options {

  private final List<AnnotatedPluginDocument> documents;
  private final BooleanOption ignoreWithNaturalisNote;

  NameSplitterOptions(List<AnnotatedPluginDocument> documents) {
    this.documents = documents;
    ignoreWithNaturalisNote = addIgnoreWithNaturalisNoteOption();
  }

  NameSplitterConfig createNameSplitterConfig() {
    NameSplitterConfig cfg = new NameSplitterConfig();
    cfg.setSelectedDocuments(documents);
    cfg.setIgnoreDocsWithNaturalisNote(ignoreWithNaturalisNote.getValue());
    return cfg;
  }

  private BooleanOption addIgnoreWithNaturalisNoteOption() {
    String name = "nl.naturalis.geneious.split.ignoreWithNaturalisNote";
    String label = "Ignore documents with Naturalis annotations";
    BooleanOption opt = addBooleanOption(name, label, Boolean.TRUE);
    opt.setHelp("If a document already has Naturalis-specific annotations it must by definition have "
        + "been imported by the Naturalis plugin or split during a previous run of the Split operation. "
        + "So ordinarily you would want to enable this option");
    return opt;
  }

}
