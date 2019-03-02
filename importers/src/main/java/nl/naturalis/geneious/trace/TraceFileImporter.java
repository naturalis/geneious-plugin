package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
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
   * @throws DatabaseServiceException
   */
  List<AnnotatedPluginDocument> process() throws IOException, DatabaseServiceException {
    List<ImportedDocument> importedDocuments = new ArrayList<>();
    try (SequenceInfoProvider provider = new SequenceInfoProvider(files)) {
      List<Ab1SequenceInfo> ab1s = provider.getAb1Sequences();
      if (ab1s.size() != 0) {
        AB1Importer importer = new AB1Importer(ab1s);
        importedDocuments.addAll(importer.importFiles());
      }
      List<FastaSequenceInfo> fastas = provider.getFastaSequences();
      if (fastas.size() != 0) {
        FastaImporter importer = new FastaImporter(fastas);
        importedDocuments.addAll(importer.importFiles());
      }
      DocumentAnnotator annotator = new DocumentAnnotator(importedDocuments);
      annotator.annotateImportedDocuments();
    }
    return importedDocuments.stream().map(ImportedDocument::getDocument).collect(Collectors.toList());
  }

}
