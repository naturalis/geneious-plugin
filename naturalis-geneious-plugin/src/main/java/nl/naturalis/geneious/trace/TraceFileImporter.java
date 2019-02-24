package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.ServiceUtilities;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

/**
 * Does the actual work of importing ab1/fasta files into Geneious.
 */
class TraceFileImporter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(TraceFileImporter.class);

  private final File[] files;

  /**
   * Creates a new trace file importer that imports the specified files.
   * 
   * @param traceFiles
   */
  TraceFileImporter(File[] traceFiles) {
    this.files = traceFiles;
  }

  /**
   * Imports the trace files.
   * 
   * @return
   * @throws IOException
   */
  List<AnnotatedPluginDocument> process() throws IOException {
    System.out.println("XXXXXXXXXXXX: " + ServiceUtilities.getResultsDestination().getFolderName());
    System.out.println("XXXXXXXXXXXX: " + ServiceUtilities.getSelectedService());
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    TraceFileImportStats stats = new TraceFileImportStats();
    try (SequenceInfoProvider provider = new SequenceInfoProvider(files)) {
      List<Ab1SequenceInfo> ab1Files = provider.getAb1Files();
      if (ab1Files.size() != 0) {
        Ab1FileImporter importer = new Ab1FileImporter(ab1Files, stats);
        result.addAll(importer.importFiles());
      }
      List<FastaSequenceInfo> fastaFiles = provider.getFastaFiles();
      if (fastaFiles.size() != 0) {
        FastaFileImporter importer = new FastaFileImporter(fastaFiles, stats);
        result.addAll(importer.importFiles());
      }
      if (ab1Files.size() != 0 && fastaFiles.size() != 0) {
        guiLogger.info("Total Number of files selected: %s", files.length);
        guiLogger.info("Total Number of files processed: %s", stats.processed);
        guiLogger.info("Total Number of files rejected: %s", stats.rejected);
        guiLogger.info("Total Number of documents enriched: %s", stats.enriched);
        guiLogger.info("Total Number of files imported: %s", result.size());
      }
    }
    return result;
  }

}
