package nl.naturalis.geneious.samplesheet;

import java.util.Collections;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.CommonUtils;

import static nl.naturalis.geneious.util.CommonUtils.allDocumentsWritableAndInSameFolder;

/**
 * The "Sample Sheet Import" plugin class.
 */
public class SampleSheetDocumentAction extends DocumentAction {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetDocumentAction.class);

  public SampleSheetDocumentAction() {
    super();
  }

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] docs) {
    try {
      if (!CommonUtils.checkTargetFolderNotNull()) {
        return;
      }
//      if (allDocumentsWritableAndInSameFolder(docs)) {
        new SampleSheetSelector(docs).show();
//      }
    } catch (Throwable t) {
    } finally {
      GuiLogManager.close();
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
