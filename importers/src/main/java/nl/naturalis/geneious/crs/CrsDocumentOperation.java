package nl.naturalis.geneious.crs;

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
 * Framework-plumbing class telling Geneious how to display and kick off the CRS Import operation.
 * 
 * @author Ayco Holleman
 */
public class CrsDocumentOperation extends DocumentOperation {

  // Releative position with menu and toolbar
  private static final double position = .99993;

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(CrsDocumentOperation.class);

  public CrsDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("CRS Import", "Enriches documents with CRS data", getIconsFromJar(getClass(), "/images/nbc_red.png"))
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, position)
        .setInMainToolbar(true, position)
        .setInPopupMenu(true, position)
        .setAvailableToWorkflows(true);
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new CrsImportOptions(DocumentUtilities.getSelectedDocuments());
  }

  @Override
  public String getHelp() {
    return "Enriches documents with CRS data";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  @Override
  public boolean isDocumentGenerator() {
    return false;
  }

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    try (LogSession session = GuiLogManager.startSession("CRS import")) {
      CrsImportOptions opts = (CrsImportOptions) options;
      CrsImporter importer = new CrsImporter(opts.createImportConfig());
      importer.execute();
    }
    return null;
  }

}
