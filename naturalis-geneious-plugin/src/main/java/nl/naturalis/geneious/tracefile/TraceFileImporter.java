package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
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
    List<File> ab1Files = ab1AndFastaFiles.get(0);
    List<File> fastaFiles = ab1AndFastaFiles.get(1);
    Ab1FileImporter imp0 = new Ab1FileImporter(ab1Files);
    List<AnnotatedPluginDocument> result = imp0.importFiles();
    FastaFileImporter imp1 = new FastaFileImporter(fastaFiles);
    result.addAll(imp1.importFiles());
//    try (FastaFileImporter imp1 = new FastaFileImporter(fastaFiles)) {
//      result.addAll(imp1.importFiles());
//    }
    return result;
  }

}
