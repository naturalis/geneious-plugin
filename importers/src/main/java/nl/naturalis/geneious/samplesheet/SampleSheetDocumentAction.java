package nl.naturalis.geneious.samplesheet;

import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.WritableDatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;
import static nl.naturalis.geneious.util.CommonUtils.*;

/**
 * The "Sample Sheet Import" plugin class.
 */
public class SampleSheetDocumentAction extends DocumentAction {

  public SampleSheetDocumentAction() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    if(allDocumentsWritableAndInSameFolder(docs)) {
      new SampleSheetSelector(docs).show();    
    }
    System.out.println("selected id: " + ServiceUtilities.getSelectedService().getUniqueID());
    System.out.println("selected id: " + ServiceUtilities.getSelectedService().getFullPath());
    List<WritableDatabaseService> svcs = PluginUtilities.getWritableDatabaseServiceRoots();
    for(WritableDatabaseService svc:svcs) {
      System.out.println("***** getFolderName: " + svc.getFolderName());
      System.out.println("***** getName: " + svc.getName());
      System.out.println("***** getUniqueID: " + svc.getUniqueID());
      System.out.println("***** getFullPath: " + svc.getFullPath());
    }
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
