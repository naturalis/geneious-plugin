package nl.naturalis.geneious.samplesheet;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
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
public class SampleSheetDocumentAction extends DocumentOperation {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SampleSheetDocumentAction.class);

  public SampleSheetDocumentAction() {
    super();
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

  @Override
  public Options getOptions(AnnotatedPluginDocument... documents) throws DocumentOperationException {
    return new SampleSheetImportOptions(documents);
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
