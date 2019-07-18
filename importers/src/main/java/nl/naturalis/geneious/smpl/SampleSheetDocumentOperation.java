package nl.naturalis.geneious.smpl;

import static com.biomatters.geneious.publicapi.utilities.IconUtilities.getIconsFromJar;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;

/**
 * Hooks the Sample Sheet Import operation into the Geneious plugin architecture. Informs Geneious how to display and
 * kick off the Sample Sheet Import operation.
 * 
 * @author Ayco Holleman
 */
public class SampleSheetDocumentOperation extends DocumentOperation {

  static final String NAME = "Sample Sheet Import";
  static final String DESCRIPTION = "Enriches documents with sample sheet data";

  // Releative position with menu and toolbar
  private static final double menuPos = .0000000000002;
  private static final double toolPos = .9999999999992;

  public SampleSheetDocumentOperation() {
    super();
  }

  /**
   * The method called by Geneious to kick off the Sample Sheet Import operation.
   */
  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions(NAME, DESCRIPTION,
        getIconsFromJar(getClass(), "/images/nbc_red.png"))
            .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, menuPos)
            .setInMainToolbar(true, toolPos)
            .setInPopupMenu(true, menuPos)
            .setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return DESCRIPTION;
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new SampleSheetImportOptions();
  }

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    SampleSheetImportOptions opts = (SampleSheetImportOptions) options;
    SampleSheetSwingWorker importer = new SampleSheetSwingWorker(opts.configureOperation());
    importer.execute();
    return null;
  }

}
