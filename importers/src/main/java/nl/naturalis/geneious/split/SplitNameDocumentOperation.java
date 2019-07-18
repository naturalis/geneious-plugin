package nl.naturalis.geneious.split;

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
 * Hooks the Split Name operation into the Geneious plugin architecture. Informs Geneious how to display and kick off
 * the Split Name operation.
 * 
 * @author Ayco Holleman
 */
public class SplitNameDocumentOperation extends DocumentOperation {

  private static final String DESCRIPTION = "Enriches documents with annotations extracted from their names";

  // Releative position with menu and toolbar
  private static final double menuPos = .0000000000005;
  private static final double toolPos = .9999999999995;

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Split Name", DESCRIPTION,
        getIconsFromJar(getClass(), "/images/nbc_black.png"))
            .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, menuPos)
            .setInMainToolbar(true, toolPos)
            .setInPopupMenu(true, menuPos)
            .setAvailableToWorkflows(true);
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new SplitNameOptions();
  }

  /**
   * The method called by Geneious to kick off the Split Name operation.
   */
  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    SplitNameOptions opts = (SplitNameOptions) options;
    SplitNameSwingWorker nameSplitter = new SplitNameSwingWorker(opts.createNameSplitterConfig());
    nameSplitter.execute();
    return null;
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
  public boolean isDocumentGenerator() {
    return false;
  }

}
