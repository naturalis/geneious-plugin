package nl.naturalis.geneious.seq;

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
 * Hooks the AB1/Fasta Import operation into Geneious. Informs Geneious how to display and kick off the AB1/Fasta Import
 * operation.
 * 
 * @author Ayco Holleman
 */
public class Ab1FastaDocumentOperation extends DocumentOperation {

  private static final String DESCRIPTION = "Imports AB1/fasta files and adds extra annotations extracted from their names";

  // Releative position with menu and toolbar
  private static final double menuPos = .0000000000001;
  private static final double toolPos = .9999999999991;

  public Ab1FastaDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("AB1/Fasta Import", DESCRIPTION,
        getIconsFromJar(getClass(), "/images/nbc_red.png"))
            .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, menuPos)
            .setInMainToolbar(true, toolPos)
            .setInPopupMenu(true, menuPos)
            .setAvailableToWorkflows(true);
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new Ab1FastaOptions();
  }

  /**
   * The method called by Geneious to kick off the AB1/Fasta Import operation.
   */
  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    Ab1FastaOptions opts = (Ab1FastaOptions) options;
    Ab1FastaSwingWorker importer = new Ab1FastaSwingWorker(opts.configureOperation());
    importer.execute();
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


}
