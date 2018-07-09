package nl.naturalis.geneious;

import java.io.File;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import nl.naturalis.geneious.gui.SampleSheetSelector;

public class SampleSheetImporter extends DocumentAction
    implements SampleSheetProcessor {

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
  public void process(File sampleSheet, List<AnnotatedPluginDocument> documentsToEnrich,
      boolean createDummies) {
    System.out.println("XXXXXXXXXXXXX sheet: " + sampleSheet.getAbsolutePath());
    System.out.println("XXXXXXXXXXXXX docs: " + documentsToEnrich.size());
    System.out.println("XXXXXXXXXXXXX dummies: " + createDummies);
  }


}
