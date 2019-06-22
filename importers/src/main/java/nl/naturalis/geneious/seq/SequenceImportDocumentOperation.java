package nl.naturalis.geneious.seq;

import static com.biomatters.geneious.publicapi.utilities.IconUtilities.getIconsFromJar;

import java.util.List;

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
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.log.LogSession;
import nl.naturalis.geneious.util.RuntimeSettings;

/**
 * Hooks the AB1/Fasta Import operation into the Geneious plugin architecture. Informs Geneious how to display and kick
 * off the AB1/Fasta Import operation.
 * 
 * @author Ayco Holleman
 */
public class SequenceImportDocumentOperation extends DocumentOperation {

  // Releative position with menu and toolbar
  private static final double menuPos = .0000000000001;
  private static final double toolPos = .9999999999991;

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceImportDocumentOperation.class);

  public SequenceImportDocumentOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("AB1/Fasta", "Import AB1/fasta files with extra annotations extracted from their names",
        getIconsFromJar(getClass(), "/images/nbc_red.png"))
            .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, menuPos)
            .setInMainToolbar(true, toolPos)
            .setInPopupMenu(true, menuPos)
            .setAvailableToWorkflows(true);
  }

  /**
   * The method called by Geneious to kick off the AB1/Fasta Import operation.
   */
  @Override
  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs, ProgressListener progress, Options options) {
    JFileChooser fc = newFileChooser();
    if(fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
      RuntimeSettings.INSTANCE.setAb1FastaFolder(fc.getCurrentDirectory());
      try (LogSession session = GuiLogManager.startSession("AB1/Fasta import")) {
        SequenceImporter importer = new SequenceImporter(fc.getSelectedFiles());
        importer.execute();
      }
    }
    return null;
  }

  @Override
  public String getHelp() {
    return "Imports one or more AB1/fasta files and parses the file name to create extra search fields";
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
