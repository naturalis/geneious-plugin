package nl.naturalis.geneious.bold;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

public class BOLDImportDocumentAction extends DocumentAction {

  public BOLDImportDocumentAction() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    new BOLDFileSelector().show();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("BOLD Import")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools).setInMainToolbar(true)
        .setInPopupMenu(true).setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Enriches documents using data from BOLD files";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}
