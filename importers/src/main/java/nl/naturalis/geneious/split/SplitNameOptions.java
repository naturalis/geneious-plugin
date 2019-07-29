package nl.naturalis.geneious.split;

import com.biomatters.geneious.publicapi.documents.DocumentUtilities;

import nl.naturalis.geneious.OperationOptions;

/**
 * Underpins the user input dialog for the {@link SplitNameDocumentOperation Split Name} operation.
 * 
 * @author Ayco Holleman
 *
 */
class SplitNameOptions extends OperationOptions<SplitNameConfig> {

  private final BooleanOption ignoreWithNaturalisNote;

  SplitNameOptions() {
    ignoreWithNaturalisNote = addIgnoreWithNaturalisNoteOption();
  }

  @Override
  public SplitNameConfig configureOperation() {
    SplitNameConfig config = new SplitNameConfig();
    super.configureDefaults(config);
    config.setIgnoreDocsWithNaturalisNote(ignoreWithNaturalisNote.getValue());
    return config;
  }

  @Override
  public String verifyOptionsAreValid() {
    String msg = super.verifyOptionsAreValid();
    if(msg != null) {
      return msg;
    }
    if(DocumentUtilities.getSelectedDocuments().isEmpty()) {
      return "Please select at least one document";
    }
    return null;
  }

  private BooleanOption addIgnoreWithNaturalisNoteOption() {
    String name = "nl.naturalis.geneious.split.ignoreWithNaturalisNote";
    String label = "Ignore documents that already have Naturalis annotations";
    BooleanOption opt = addBooleanOption(name, label, Boolean.TRUE);
    opt.setHelp("If a document has Naturalis-specific annotations, its name must by definition already have been "
        + "parsed (a.k.a. split) by Naturalis plugin, either through the AB1/Fasta Import operation or during a "
        + "or previous run of the Split Name operation. So ordinarily you would want to enable this option");
    return opt;
  }

}
