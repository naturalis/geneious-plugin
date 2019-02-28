package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.DocumentManager;

import static nl.naturalis.geneious.util.QueryUtils.findByExtractID;

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
   * @throws DatabaseServiceException
   */
  List<AnnotatedPluginDocument> process() throws IOException, DatabaseServiceException {
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    try (SequenceInfoProvider provider = new SequenceInfoProvider(files)) {
      List<AnnotatedPluginDocument> oldDocuments = findByExtractID(provider.getExtractIDs());
      List<Ab1SequenceInfo> ab1s = provider.getAb1Sequences();
      if (ab1s.size() != 0) {
        AB1Importer importer = new AB1Importer(ab1s);
        result.addAll(importer.importFiles());
      }
      List<FastaSequenceInfo> fastas = provider.getFastaSequences();
      if (fastas.size() != 0) {
        FastaImporter importer = new FastaImporter(fastas);
        result.addAll(importer.importFiles());
      }
    }
    return result;
  }

}
