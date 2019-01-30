package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.BadFileNameException;
import nl.naturalis.geneious.split.FileNameParser;
import nl.naturalis.geneious.util.RuntimeSettings;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Does the actual work of importing ab1/fasta files into Geneious.
 */
public class TraceFileImporter {

  private final GuiLogger guiLogger;
  private final File[] files;

  public TraceFileImporter(File[] traceFiles) {
    this.guiLogger = new GuiLogger(RuntimeSettings.INSTANCE.getLogLevel());
    this.files = traceFiles;
  }

  public List<AnnotatedPluginDocument> process() {
    List<AnnotatedPluginDocument> result = new ArrayList<>(files.length);
    int imported = 0;
    int rejected = 0;
    int enriched = 0;
    FileNameParser parser = new FileNameParser();
    try {
      for (File f : files) {
        guiLogger.debugf(() -> format("Processing file: %s", f.getName()));
        List<AnnotatedPluginDocument> apds;
        try {
          apds = PluginUtilities.importDocuments(f, null);
          ++imported;
        } catch (IOException | DocumentImportException e) {
          guiLogger.error("Error processing file %s", e, f.getAbsolutePath());
          ++rejected;
          continue;
        }
        if (apds.size() != 1) {
          guiLogger.fatal("Unexpected number of documents created from a single file: %s. Aborting.", apds.size());
          break;
        }
        try {
          NaturalisNote note = parser.parse(f.getName());
          if (note != null) {
            note.attach(apds.get(0));
            ++enriched;
          }
        } catch (BadFileNameException e) {
          guiLogger.error(e.getMessage());
          continue;
        }
        result.addAll(apds);
      }
      guiLogger.info("Number of files selected: %s", files.length);
      guiLogger.info("Number of files imported: %s", imported);
      guiLogger.info("Number of files rejected: %s", rejected);
      guiLogger.info("Number of documents enriched using file name: %s", enriched);
    } finally {
      guiLogger.showLog("Fasta/AB1 import log");
    }
    return result;
  }

}
