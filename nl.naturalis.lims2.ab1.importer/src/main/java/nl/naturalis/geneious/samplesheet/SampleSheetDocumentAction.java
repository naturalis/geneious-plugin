package nl.naturalis.geneious.samplesheet;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * The "Sample Sheet Import" plugin class.
 */
public class SampleSheetDocumentAction extends DocumentAction {

  public SampleSheetDocumentAction() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    new SampleSheetSelector(docs).show();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Sample Sheet")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools)
        .setInMainToolbar(true)
        .setInPopupMenu(true)
        .setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Enriches documents using data from sample sheets";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}
