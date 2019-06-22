package nl.naturalis.geneious.smpl;

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
 * Hooks the Sample Sheet Import operation into the Geneious plugin architecture. Informs Geneious how to display and
 * kick off the Sample Sheet Import operation.
 * 
 * @author Ayco Holleman
 */
public class SampleSheetDocumentOperation extends DocumentOperation {

  // Releative position with menu and toolbar
  private static final double menuPos = .0000000000002;
  private static final double toolPos = .9999999999992;

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetDocumentOperation.class);

  public SampleSheetDocumentOperation() {
    super();
  }

  /**
   * The method called by Geneious to kick off the Sample Sheet Import operation.
   */
  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Samples", "Enrich documents with data from sample sheets",
        getIconsFromJar(getClass(), "/images/nbc_red.png"))
            .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, menuPos)
            .setInMainToolbar(true, toolPos)
            .setInPopupMenu(true, menuPos)
            .setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Enrich documents with data sample sheet data";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new SampleSheetImportOptions(DocumentUtilities.getSelectedDocuments());
  }

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    try (LogSession session = GuiLogManager.startSession("Sample sheet import")) {
      SampleSheetImportOptions opts = (SampleSheetImportOptions) options;
      SampleSheetImporter importer = new SampleSheetImporter(opts.createImportConfig());
      importer.execute();
    }
    return null;
  }

}
