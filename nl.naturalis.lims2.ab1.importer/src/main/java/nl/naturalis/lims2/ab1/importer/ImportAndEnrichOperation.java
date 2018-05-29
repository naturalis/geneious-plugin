package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import jebl.util.ProgressListener;
import nl.naturalis.geneious.util.RuntimeSettings;

public class ImportAndEnrichOperation extends DocumentOperation {

  public ImportAndEnrichOperation() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("Ayco Rocks").setInMainToolbar(true);
  }

  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs,
      ProgressListener progress, Options options) {
    System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD: " + docs.length);
    JFileChooser fc = new JFileChooser(RuntimeSettings.INSTANCE.getLastSelectedFolder());
    fc.setMultiSelectionEnabled(true);
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    if (fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
      RuntimeSettings.INSTANCE.setLastSelectedFolder(fc.getCurrentDirectory());
      File[] files = fc.getSelectedFiles();
      for (File f : files) {
        try {
          List<AnnotatedPluginDocument> apds = PluginUtilities.importDocuments(f, null);
          result.addAll(apds);
        } catch (IOException | DocumentImportException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return result;
  }

  @Override
  public String getHelp() {
    // TODO Auto-generated method stub
    return "Won't tell ya";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}
