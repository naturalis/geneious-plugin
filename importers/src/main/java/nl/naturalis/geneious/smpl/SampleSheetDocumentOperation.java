package nl.naturalis.geneious.smpl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import nl.naturalis.geneious.util.CommonUtils;

/**
 * Framework-plumbing class used to import sample sheets.
 */
public class SampleSheetDocumentOperation extends DocumentOperation {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetDocumentOperation.class);

  public SampleSheetDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Samples")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, .99991)
        .setInMainToolbar(true, .99991)
        .setInPopupMenu(true, .99991)
        .setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Updates documents with data from sample sheets";
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
    if (CommonUtils.checkTargetFolderNotNull()) {
      try (LogSession session = GuiLogManager.startSession("Sample sheet import")) {
        SampleSheetImportOptions opts = (SampleSheetImportOptions) options;
        SampleSheetImporter importer = new SampleSheetImporter(opts.createImportConfig());
        importer.execute();
        return importer.get();
      } catch (InterruptedException | ExecutionException e) {
        guiLogger.fatal(e);
      }
    }
    return Collections.emptyList();
  }

}
