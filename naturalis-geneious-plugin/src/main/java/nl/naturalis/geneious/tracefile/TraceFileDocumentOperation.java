package nl.naturalis.geneious.tracefile;

import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.util.RuntimeSettings;

public class TraceFileDocumentOperation extends DocumentOperation {

  public TraceFileDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("AB1/Fasta Import")
        .setInMainToolbar(true)
        .setInPopupMenu(true)
        .setAvailableToWorkflows(true);
  }

  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    JFileChooser fileChooser = new JFileChooser(RuntimeSettings.INSTANCE.getAb1FastaFolder());
    fileChooser.setMultiSelectionEnabled(true);
    if (fileChooser.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
      RuntimeSettings.INSTANCE.setAb1FastaFolder(fileChooser.getCurrentDirectory());
      return new TraceFileImporter(fileChooser.getSelectedFiles()).process();
    }
    return Collections.emptyList();
  }

  @Override
  public String getHelp() {
    return "Imports one or more AB1/Fasta files and parses the file name to create extra search fields";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}
