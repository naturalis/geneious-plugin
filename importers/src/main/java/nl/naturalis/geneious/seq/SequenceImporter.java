package nl.naturalis.geneious.seq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.APDList;

/**
 * Does the actual work of importing ab1/fasta files into Geneious.
 */
class SequenceImporter extends SwingWorker<Void, Void> {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceImporter.class);

  private final File[] files;

  /**
   * Creates a new trace file importer that imports the specified files.
   * 
   * @param files The ab1/files selected by the user
   */
  SequenceImporter(File[] files) {
    this.files = files;
  }

  @Override
  protected Void doInBackground() {
    importSequences();
    return null;
  }

  /**
   * Imports the trace files.
   * 
   * @return
   * @throws IOException
   * @throws DatabaseServiceException
   */
  private void importSequences() {
    try (SequenceInfoProvider provider = new SequenceInfoProvider(files)) {
      List<ImportableDocument> docs = new ArrayList<>();
      AB1Importer ab1Importer = null;
      FastaImporter fastaImporter = null;
      Annotator annotator = null;
      List<AB1Info> ab1s = provider.getAb1Sequences();
      if (ab1s.size() != 0) {
        ab1Importer = new AB1Importer(ab1s);
        docs.addAll(ab1Importer.importFiles());
      }
      List<FastaInfo> fastas = provider.getFastaSequences();
      if (fastas.size() != 0) {
        fastaImporter = new FastaImporter(fastas);
        docs.addAll(fastaImporter.importFiles());
      }
      if (docs.size() != 0) {
        annotator = new Annotator(docs);
        annotator.annotateDocuments();
        docs.forEach(ImportableDocument::saveAnnotations);
      }
      int processed = 0, rejected = 0, imported = 0;
      if (ab1Importer != null) {
        processed = ab1Importer.getNumProcessed();
        rejected = ab1Importer.getNumRejected();
        imported = ab1Importer.getNumImported();
        guiLogger.info("Number of AB1 files selected ..........: %3d", ab1s.size());
        guiLogger.info("Number of AB1 documents created .......: %3d", processed);
        guiLogger.info("Number of AB1 documents rejected ......: %3d", rejected);
        guiLogger.info("Number of AB1 documents imported ......: %3d", imported);
      }
      if (fastaImporter != null) {
        processed += fastaImporter.getNumProcessed();
        rejected += fastaImporter.getNumRejected();
        imported += fastaImporter.getNumImported();
        guiLogger.info("Number of FASTA files selected ........: %3d", fastas.size());
        guiLogger.info("Number of FASTA documents created .....: %3d", fastaImporter.getNumProcessed());
        guiLogger.info("Number of FASTA documents rejected ....: %3d", fastaImporter.getNumRejected());
        guiLogger.info("Number of FASTA documents imported ....: %3d", fastaImporter.getNumImported());
      }
      if (ab1Importer != null && fastaImporter != null) {
        guiLogger.info("Total number of files selected ........: %3d", files.length);
        guiLogger.info("Total number of documents created .....: %3d", processed);
        guiLogger.info("Total number of documents rejected ....: %3d", rejected);
        guiLogger.info("Total number of documents imported ....: %3d", imported);
      }
      if (annotator != null) {
        guiLogger.info("Total number of documents annotated ...: %3d", annotator.getSuccessCount());
        guiLogger.info("Total number of annotation failures ...: %3d", annotator.getFailureCount());
      }
      APDList result = new APDList(docs.size());
      docs.forEach((d) -> result.add(d.getGeneiousDocument()));
      DocumentUtilities.addGeneratedDocuments(result, true, Collections.emptyList());
    } catch (Throwable t) {
      guiLogger.fatal(t.getMessage(), t);
    }
  }

}
