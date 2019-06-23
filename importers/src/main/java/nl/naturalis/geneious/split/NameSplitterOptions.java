package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.Options;

/**
 * Sets up a Geneious dialog requesting user input for the {@link SplitNameDocumentOperation Split Name} operation. Once
 * the user click OK, this class produces a {@link NameSplitterConfig} object, which is then passed on to the
 * {@link SplitNameSwingWorker}.
 * 
 * @author Ayco Holleman
 *
 */
class NameSplitterOptions extends Options {

  private final List<AnnotatedPluginDocument> documents;
  private final BooleanOption ignoreWithNaturalisNote;

  NameSplitterOptions(List<AnnotatedPluginDocument> documents) {
    this.documents = documents;
    ignoreWithNaturalisNote = addIgnoreWithNaturalisNoteOption();
  }

  /**
   * Produces a object containing all the user input for the Split Name operation.
   */
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
