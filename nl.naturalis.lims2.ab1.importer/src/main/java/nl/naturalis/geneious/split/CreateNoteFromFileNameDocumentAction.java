package nl.naturalis.geneious.split;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;

public class CreateNoteFromFileNameDocumentAction extends DocumentAction {

  @Override
  public void actionPerformed(AnnotatedPluginDocument[] selectedDocuments) {
    GuiLogger logger = new GuiLogger();
    int good = 0;
    int bad = 0;
    FileNameParser parser = new FileNameParser();
    try {
      for (AnnotatedPluginDocument doc : selectedDocuments) {
        NaturalisNote note;
        try {
          note = parser.parse(doc.getName());
          note.attach(doc);
          ++good;
        } catch (BadFileNameException e) {
          logger.error(e.getMessage());
          ++bad;
        }
      }
    } finally {
      logger.info("Number of documents selected: %s", selectedDocuments.length);
      logger.info("Number of documents enriched: %s", good);
      logger.info("Number of unprocessable documents: %s", bad);
      logger.showLog("Split Name log");
    }
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Split Name")
        .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools)
        .setInMainToolbar(true)
        .setInPopupMenu(true)
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
