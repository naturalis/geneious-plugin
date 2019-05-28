package nl.naturalis.geneious.split;

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

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SplitNameDocumentOperation.class);

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Split Name")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, .99995)
        .setInMainToolbar(true, .99995)
        .setInPopupMenu(true, .99995)
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
    return "Parses and splits documents names";
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
