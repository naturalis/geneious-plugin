package nl.naturalis.geneious.crs;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import nl.naturalis.geneious.gui.Dialogs;

public class CrsImportDocumentAction extends DocumentAction {

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] selectedDocuments) {
    if (selectedDocuments.length == 0) {
      Dialogs.noDocumentsSelected();
      return;
    }
    CrsFileSelector fileSelector = new CrsFileSelector(selectedDocuments);
    fileSelector.show();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("CRS Import")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools).setInMainToolbar(true)
        .setInPopupMenu(true).setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Enriches Geneious documents with data from CRS";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}
