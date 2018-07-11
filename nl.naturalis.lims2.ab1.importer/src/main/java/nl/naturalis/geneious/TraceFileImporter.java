package nl.naturalis.geneious;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.biomatters.geneious.publicapi.utilities.GuiUtilities;
import jebl.util.ProgressListener;
import nl.naturalis.geneious.notes.NaturalisNote;
import nl.naturalis.geneious.util.RuntimeSettings;

public class TraceFileImporter extends DocumentOperation {

  public TraceFileImporter() {
    super();
  }

  @Override
  public GeneiousActionOptions getActionOptions() {
    return new GeneiousActionOptions("AB1/Fasta [V2]").setInMainToolbar(true)
        .setInPopupMenu(true).setAvailableToWorkflows(true);
  }

  public List<AnnotatedPluginDocument> performOperation(AnnotatedPluginDocument[] docs,
      ProgressListener progress, Options options) {
    JFileChooser fc = new JFileChooser(RuntimeSettings.INSTANCE.getLastSelectedFolder());
    fc.setMultiSelectionEnabled(true);
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    if (fc.showOpenDialog(GuiUtilities.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
      RuntimeSettings.INSTANCE.setLastSelectedFolder(fc.getCurrentDirectory());
      File[] files = fc.getSelectedFiles();
      for (File f : files) {
        try {
          List<AnnotatedPluginDocument> apds = PluginUtilities.importDocuments(f, null);
          assert (apds.size() == 1); // Otherwise we seem to be misunderstanding something
          if (f.getName().endsWith(".ab1")) {
            if (f.getName().contains("_")) {
              String[] chunks = StringUtils.split(f.getName(), '_');
              if (chunks.length < 5) {
                // Do some reporting
                continue;
              }
              NaturalisNote note = new NaturalisNote();
              note.setExtractId(chunks[0]);
              note.setPcrPlateId(chunks[3]);
              int i = chunks[4].indexOf('-');
              String marker = i == -1 ? null : chunks[4].substring(0, i);
              note.setMarker(marker);
              note.attach(apds.get(0));
            }
          }
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
    return "V2 AB1/Fasta import";
  }

  @Override
  public DocumentSelectionSignature[] getSelectionSignatures() {
    return new DocumentSelectionSignature[0];
  }

}
