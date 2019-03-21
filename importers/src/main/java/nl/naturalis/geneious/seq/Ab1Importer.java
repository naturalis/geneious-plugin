package nl.naturalis.geneious.seq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Imports the AB1 files selected by the user into Geneious.
 */
class Ab1Importer {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(Ab1Importer.class);

  private final List<Ab1SequenceInfo> sequences;

  private int processed;
  private int imported;
  private int rejected;

  Ab1Importer(List<Ab1SequenceInfo> sequences) {
    guiLogger.info("Starting AB1 file importer");
    this.sequences = sequences;
  }

  /**
   * Imports the AB1 files.
   * 
   * @return
   * @throws IOException
   */
  List<ImportableDocument> importFiles() throws IOException {
    processed = imported = rejected = 0;
    List<ImportableDocument> importables = new ArrayList<>(sequences.size());
    for (Ab1SequenceInfo info : sequences) {
      ++processed;
      File f = info.getSourceFile();
      guiLogger.debugf(() -> format("Importing file: %s", f.getName()));
      try {
        List<AnnotatedPluginDocument> apds = PluginUtilities.importDocuments(f, null);
        if (apds.size() != 1) { // We don't understand when this might happen, so let's just crash
          String fmt = "Unexpected number of documents created from a single file (%s): %s. Aborting.";
          String msg = String.format(fmt, f.getName(), apds.size());
          throw new AssertionError(msg);
        }
        AnnotatedPluginDocument doc = apds.get(0);
        doc.setName(info.getName());
        importables.add(new ImportableDocument(doc, info));
        ++imported;
      } catch (DocumentImportException e) {
        guiLogger.error("Error processing file %s", e, f.getAbsolutePath());
        ++rejected;
      }
    }
    return importables;
  }

  int getNumProcessed() {
    return processed;
  }

  int getNumImported() {
    return imported;
  }

  int getNumRejected() {
    return rejected;
  }

}
