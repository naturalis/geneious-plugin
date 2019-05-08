package nl.naturalis.geneious.crs;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.gui.log.LogSession;

/**
 * Framework-plumbing class used to import sample sheets.
 */
public class CrsDocumentOperation extends DocumentOperation {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(CrsDocumentOperation.class);

  public CrsDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("CRS Import")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, .99992)
        .setInMainToolbar(true, .99992)
        .setInPopupMenu(true, .99992)
        .setAvailableToWorkflows(true);
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new CrsImportOptions(DocumentUtilities.getSelectedDocuments());
  }

  @Override
  public String getHelp() {
    return "Updates documents with CRS data";
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
