package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;

class NameSplitterOptions extends Options {

  private final List<AnnotatedPluginDocument> documents;
  private final BooleanOption ignoreWithNaturalisNote;
  private final BooleanOption ignoreDummies;

  NameSplitterOptions(List<AnnotatedPluginDocument> documents) {
    this.documents = documents;
    ignoreWithNaturalisNote = addIgnoreWithNaturalisNoteOption();
    ignoreDummies = addIgnoreDummies();
  }

  NameSplitterConfig createNameSplitterConfig() {
    NameSplitterConfig cfg = new NameSplitterConfig();
    cfg.setSelectedDocuments(documents);
    cfg.setIgnoreDocsWithNaturalisNote(ignoreWithNaturalisNote.getValue());
    cfg.setIgnoreDummies(ignoreDummies.getValue());
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

  private BooleanOption addIgnoreDummies() {
    String name = "nl.naturalis.geneious.split.ignoreDummies";
    String label = "Ignore dummy documents";
    BooleanOption opt = addBooleanOption(name, label, Boolean.TRUE);
    opt.setHelp("This option allows you to do a \"Select All\" (Ctrl-A) within a document folder and "
        + "still ignore the dummy documents within it. Otherwise the plugin would generate harmless "
        + "but annoying errors, because the names of dummy documents cannot be split using the rules "
        + "for splitting document names.");
    return opt;
  }

}
