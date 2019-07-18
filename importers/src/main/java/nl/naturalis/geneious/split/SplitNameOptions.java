package nl.naturalis.geneious.split;

import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.Options;

/**
 * Sets up a Geneious dialog requesting user input for the {@link SplitNameDocumentOperation Split Name} operation. Once
 * the user click OK, this class produces a {@link SplitNameConfig} object, which is then passed on to the
 * {@link SplitNameSwingWorker}.
 * 
 * @author Ayco Holleman
 *
 */
class SplitNameOptions extends Options {

  private final BooleanOption ignoreWithNaturalisNote;

  SplitNameOptions() {
    ignoreWithNaturalisNote = addIgnoreWithNaturalisNoteOption();
  }

  /**
   * Produces an object containing all the user input for the Split Name operation.
   */
  SplitNameConfig createNameSplitterConfig() {
    SplitNameConfig config = new SplitNameConfig();
    config.setIgnoreDocsWithNaturalisNote(ignoreWithNaturalisNote.getValue());
    return config;
  }

  @Override
  public String verifyOptionsAreValid() {
    if(DocumentUtilities.getSelectedDocuments().isEmpty()) {
      return "Please select at least one document";
    }
    return null;
  }

  private BooleanOption addIgnoreWithNaturalisNoteOption() {
    String name = "nl.naturalis.geneious.split.ignoreWithNaturalisNote";
    String label = "Ignore documents that already have Naturalis annotations";
    BooleanOption opt = addBooleanOption(name, label, Boolean.TRUE);
    opt.setHelp("If a document has Naturalis-specific annotations its name must by definition already have been "
        + "split by the Naturalis plugin, either through the AB1/Fasta Import operation or during a or previous run "
        + "of the Split Name operation. So ordinarily you would want to enable this option");
    return opt;
  }

}
