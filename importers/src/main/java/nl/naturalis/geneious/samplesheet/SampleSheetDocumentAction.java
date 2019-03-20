package nl.naturalis.geneious.samplesheet;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.gui.log.LogSession;
import nl.naturalis.geneious.util.CommonUtils;

/**
 * Framework-plumbing class used to import sample sheets.
 */
public class SampleSheetDocumentAction extends DocumentAction {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetDocumentAction.class);

  public SampleSheetDocumentAction() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    if (!CommonUtils.checkTargetFolderNotNull()) {
      return;
    }
    new SampleSheetSelector(docs, this::forwardToImporter).show();
  }

//  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
//    return null;
//  }

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

  private void forwardToImporter(UserInput input) {
    SampleSheetImporter importer = new SampleSheetImporter(input);
    try (LogSession session = GuiLogManager.startSession("Sample sheet import")) {
      importer.execute();
    }
  }

}
