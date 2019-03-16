package nl.naturalis.geneious.trace;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.gui.Ab1FastaFileFilter;
import nl.naturalis.geneious.gui.GeneiousGUI;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.CommonUtils;
import nl.naturalis.geneious.util.RuntimeSettings;

/**
 * Framework-plumbing class used to import AB1 and fasta files. Instantiates a {@link TraceFileImporter} and lets it do most of the work.
 *
 * @author Ayco Holleman
 */
public class TraceFileDocumentOperation extends DocumentOperation {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(TraceFileDocumentOperation.class);

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

  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    if (CommonUtils.checkTargetFolderNotNull()) {
      try {
        JFileChooser fc = newFileChooser();
        if (fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
          RuntimeSettings.INSTANCE.setAb1FastaFolder(fc.getCurrentDirectory());
          GuiLogManager.showLog("AB1/Fasta import log");
          TraceFileImporter importer = new TraceFileImporter(fc.getSelectedFiles());
          importer.execute();
          return importer.get();
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      } finally {
        GuiLogManager.close();
      }
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

  private static JFileChooser newFileChooser() {
    JFileChooser fc = new JFileChooser(RuntimeSettings.INSTANCE.getAb1FastaFolder());
    fc.setDialogTitle("Choose AB1/fasta files to import");
    fc.setApproveButtonText("Import files");
    fc.setMultiSelectionEnabled(true);
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(true, true));
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(true, false));
    fc.addChoosableFileFilter(new Ab1FastaFileFilter(false, true));
    fc.setAcceptAllFileFilterUsed(false);
    GeneiousGUI.scale(fc, .6, .5, 800, 560);
    return fc;
  }

}
