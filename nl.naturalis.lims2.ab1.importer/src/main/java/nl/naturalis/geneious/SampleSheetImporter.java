package nl.naturalis.geneious;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import nl.naturalis.geneious.gui.SampleSheetSelector;
import nl.naturalis.geneious.gui.SampleSheetSelector.Selection;

public class SampleSheetImporter extends DocumentAction
    implements SampleSheetSelector.SelectionHandler {

  public SampleSheetImporter() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    SampleSheetSelector sss = new SampleSheetSelector(this);
    sss.show();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Samples [V2]")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools).setInMainToolbar(true)
        .setInPopupMenu(true).setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return null;
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  @Override
  public void processSampleSheet(Selection result) {
    System.out.println("XXXXXXXXXXXXXXXXXXXXX: " + result.createDummies);
    System.out.println("XXXXXXXXXXXXXXXXXXXXX: " + result.sampleSheet.getAbsolutePath());

  }

}
