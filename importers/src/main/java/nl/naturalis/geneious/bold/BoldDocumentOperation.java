package nl.naturalis.geneious.bold;

import static com.biomatters.geneious.publicapi.utilities.IconUtilities.getIconsFromJar;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.log.LogSession;

/**
 * Hooks the BOLD Import operation into the Geneious plugin architecture. Informs Geneious how to display and kick off
 * the BOLD Import operation.
 * 
 * @author Ayco Holleman
 */
public class BoldDocumentOperation extends DocumentOperation {

  // Releative position with menu and toolbar
  private static final double menuPos = .0000000000004;
  private static final double toolPos = .9999999999994;

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(BoldDocumentOperation.class);

  public BoldDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("BOLD Import", "Enriches documents with BOLD data",
        getIconsFromJar(getClass(), "/images/nbc_blue.png"))
            .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, menuPos)
            .setInMainToolbar(true, toolPos)
            .setInPopupMenu(true, menuPos)
            .setAvailableToWorkflows(true);
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new BoldImportOptions(DocumentUtilities.getSelectedDocuments());
  }

  /**
   * The method called by Geneious to kick off the BOLD Import operation.
   */
  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    try (LogSession session = GuiLogManager.startSession("BOLD import")) {
      BoldImportOptions opts = (BoldImportOptions) options;
      BoldSwingWorker importer = new BoldSwingWorker(opts.createImportConfig());
      importer.execute();
    }
    return null;
  }

  @Override
  public String getHelp() {
    return "Enriches documents with BOLD data";
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
