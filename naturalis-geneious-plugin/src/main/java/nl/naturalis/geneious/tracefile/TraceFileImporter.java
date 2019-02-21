package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

/**
 * Does the actual work of importing ab1/fasta files into Geneious.
 */
class TraceFileImporter {

  @SuppressWarnings("unused")
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
    TraceFilePreprocessor preprocessor = new TraceFilePreprocessor(files);
    List<List<File>> ab1AndFastaFiles = preprocessor.divideByFileType();
    List<AnnotatedPluginDocument> result = new ArrayList<>(64);
    List<File> ab1Files = ab1AndFastaFiles.get(0);
    List<File> fastaFiles = ab1AndFastaFiles.get(1);
    if (ab1Files.size() != 0) {
      Ab1FileImporter importer = new Ab1FileImporter(ab1Files);
      result.addAll(importer.importFiles());
    }
    if (fastaFiles.size() != 0) {
      try (FastaFileImporter importer = new FastaFileImporter(fastaFiles)) {
        result.addAll(importer.importFiles());
      }
    }
    return result;
  }

}
