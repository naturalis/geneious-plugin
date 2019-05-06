package nl.naturalis.geneious.bold;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.gui.WaitTimer;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.gui.log.LogSession;

/**
 * Framework-plumbing class used to import sample sheets.
 */
public class BoldDocumentOperation extends DocumentOperation {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldDocumentOperation.class);

  public BoldDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("BOLD Import")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, .99993)
        .setInMainToolbar(true, .99993)
        .setInPopupMenu(true, .99993)
        .setAvailableToWorkflows(true);
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new BoldImportOptions(DocumentUtilities.getSelectedDocuments());
  }

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    try (LogSession session = GuiLogManager.startSession("BOLD import")) {
      if (WaitTimer.isOperationAllowed()) {
        BoldImportOptions opts = (BoldImportOptions) options;
        BoldImporter importer = new BoldImporter(opts.createImportConfig());
        importer.execute();
        WaitTimer.setNewEndTime();
      }
    }
    return null;
  }

  @Override
  public String getHelp() {
    return "Updates documents with BOLD data";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  @Override
  public boolean isDocumentGenerator() {
    return false;
  }

}
