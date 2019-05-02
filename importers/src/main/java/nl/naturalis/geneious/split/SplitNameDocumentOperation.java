package nl.naturalis.geneious.split;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideGraphSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.gui.WaitTimer;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.gui.log.LogSession;

public class SplitNameDocumentOperation extends DocumentOperation {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SplitNameDocumentOperation.class);

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Split names")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, .99995)
        .setInMainToolbar(true, .99995)
        .setInPopupMenu(true, .99995)
        .setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Parses and splits documents names";
  }

  @Override
  public Options getOptions(AnnotatedPluginDocument... docs) throws DocumentOperationException {
    return new NameSplitterOptions(DocumentUtilities.getSelectedDocuments());
  }

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    try (LogSession session = GuiLogManager.startSession("Split name")) {
      if (WaitTimer.isOperationAllowed()) {
        NameSplitterOptions opts = (NameSplitterOptions) options;
        NameSplitter nameSplitter = new NameSplitter(opts.createNameSplitterConfig());
        nameSplitter.execute();
        WaitTimer.setNewEndTime();
      }
    }
    return null;
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[] {
        new DocumentSelectionSignature(NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE),
        new DocumentSelectionSignature(DefaultNucleotideGraphSequence.class, 0, Integer.MAX_VALUE)
    };
  }

}
