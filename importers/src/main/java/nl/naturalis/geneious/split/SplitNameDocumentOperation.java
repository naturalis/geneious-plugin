package nl.naturalis.geneious.split;

import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

public class SplitNameDocumentOperation extends DocumentOperation {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SplitNameDocumentOperation.class);

   @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Split")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, .99991)
        .setInMainToolbar(true, .99995)
        .setInPopupMenu(true, .99995)
        .setAvailableToWorkflows(true);
  }

  @Override
  public String getHelp() {
    return "Creates separate fields for each of the constituent parts of the file name";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[] {
        new DocumentSelectionSignature(NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE)};
  }

}
