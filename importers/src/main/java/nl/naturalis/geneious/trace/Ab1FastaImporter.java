package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.StoredDocument;
import nl.naturalis.geneious.util.QueryUtils;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Does the actual work of importing ab1/fasta files into Geneious.
 */
class Ab1FastaImporter extends SwingWorker<List<AnnotatedPluginDocument>, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(Ab1FastaImporter.class);

  private final File[] files;

  /**
   * Creates a new trace file importer that imports the specified files.
   * 
   * @param traceFiles
   */
  Ab1FastaImporter(File[] traceFiles) {
    this.files = traceFiles;
  }

  @Override
  protected List<AnnotatedPluginDocument> doInBackground() {
    return importTraceFiles();
  }

  /**
   * Imports the trace files.
   * 
   * @return
   * @throws IOException
   * @throws DatabaseServiceException
   */
  private List<AnnotatedPluginDocument> importTraceFiles() {
    try (SequenceInfoProvider provider = new SequenceInfoProvider(files)) {
      List<ImportableDocument> docs = new ArrayList<>();
      Ab1Importer ab1Importer = null;
      FastaImporter fastaImporter = null;
      DocumentAnnotator annotator = null;
      List<Ab1SequenceInfo> ab1s = provider.getAb1Sequences();
      if (ab1s.size() != 0) {
        ab1Importer = new Ab1Importer(ab1s);
        docs.addAll(ab1Importer.importFiles());
      }
      List<FastaSequenceInfo> fastas = provider.getFastaSequences();
      if (fastas.size() != 0) {
        fastaImporter = new FastaImporter(fastas);
        docs.addAll(fastaImporter.importFiles());
      }
      if (docs.size() != 0) {
        annotator = new DocumentAnnotator(docs);
        annotator.annotateImportedDocuments();
        Set<StoredDocument> dummies = annotator.getObsoleteDummyDocuments();
        if (!dummies.isEmpty()) {
          guiLogger.info("Deleting obsolete dummy documents");
          QueryUtils.deleteDocuments(dummies);
          guiLogger.debugf(() -> format("Deleted %s obsolete dummy document(s)", dummies.size()));
        }
      }
      int processed = 0, rejected = 0, imported = 0;
      if (ab1Importer != null) {
        guiLogger.info("Number of AB1 files selected ...........: %3d", ab1s.size());
        guiLogger.info("Number of chromatograms created ........: %3d", (processed = ab1Importer.getNumProcessed()));
        guiLogger.info("Number of chromatograms rejected .......: %3d", (rejected = ab1Importer.getNumRejected()));
        guiLogger.info("Number of chromatograms imported .......: %3d", (imported = ab1Importer.getNumImported()));
      }
      if (fastaImporter != null) {
        guiLogger.info("Number of fasta files selected .........: %3d", fastas.size());
        guiLogger.info("Number of sequences created ............: %3d", (processed += fastaImporter.getNumProcessed()));
        guiLogger.info("Number of sequences rejected ...........: %3d", (rejected += fastaImporter.getNumRejected()));
        guiLogger.info("Number of sequences imported ...........: %3d", (imported += fastaImporter.getNumImported()));
      }
      if (ab1Importer != null && fastaImporter != null) {
        guiLogger.info("Total number of trace files selected ...: %3d", files.length);
        guiLogger.info("Total number of documents created ......: %3d", processed);
        guiLogger.info("Total number of documents rejected .....: %3d", rejected);
        guiLogger.info("Total number of documents imported .....: %3d", imported);
      }
      if (annotator != null) {
        guiLogger.info("Total number of documents annotated ....: %3d", annotator.getSuccessCount());
        guiLogger.info("Total number of annotation failures ....: %3d", annotator.getFailureCount());
      }
      return docs.stream().map(ImportableDocument::getGeneiousDocument).collect(Collectors.toList());
    } catch (Throwable t) {
      guiLogger.fatal(t.getMessage(), t);
      return Collections.emptyList();
    }
  }

}
