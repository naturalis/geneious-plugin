package nl.naturalis.geneious.split;

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

public class SplitNameDocumentOperation extends DocumentOperation {
  
  // Releative position with menu and toolbar
  private static final double position = .99991;

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SplitNameDocumentOperation.class);

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Split Name", "Enriches documents by parsing their name", getIconsFromJar(getClass(), "/images/nbc_green.png"))
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, position)
        .setInMainToolbar(true, position)
        .setInPopupMenu(true, position)
        .setAvailableToWorkflows(true);
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new NameSplitterOptions(DocumentUtilities.getSelectedDocuments());
  }

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    try (LogSession session = GuiLogManager.startSession("Split Name")) {
      NameSplitterOptions opts = (NameSplitterOptions) options;
      NameSplitter nameSplitter = new NameSplitter(opts.createNameSplitterConfig());
      nameSplitter.execute();
    }
    return null;
  }

  @Override
  public String getHelp() {
    return "Enriches documents by parsing their name";
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
