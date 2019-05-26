package nl.naturalis.geneious.seq;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.*;
import static nl.naturalis.geneious.util.PreconditionValidator.BASIC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.StorableDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.name.Annotator;
import nl.naturalis.geneious.util.APDList;
import nl.naturalis.geneious.util.PreconditionValidator;

/**
 * Does the actual work of importing AB1/fasta files into Geneious.
 */
class SequenceImporter extends PluginSwingWorker {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceImporter.class);

  private final File[] files;

  SequenceImporter(File[] files) {
    this.files = files;
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws IOException, DatabaseServiceException, NonFatalException {
    PreconditionValidator validator = new PreconditionValidator(BASIC);
    validator.validate();
    List<AnnotatedPluginDocument> created = null;
    try (SequenceInfoProvider provider = new SequenceInfoProvider(files)) {
      List<StorableDocument> docs = new ArrayList<>();
      List<StorableDocument> annotated = null;
      AB1Importer ab1Importer = null;
      FastaImporter fastaImporter = null;
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
        Annotator annotator = new Annotator(docs);
        annotated = annotator.annotateDocuments();
        created = new ArrayList<>(docs.size());
        for (StorableDocument doc : docs) {
          doc.saveAnnotations();
          created.add(doc.getGeneiousDocument());
        } ;
        created = addAndReturnGeneratedDocuments(created, true, Collections.emptyList());
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
      if (annotated != null) {
        guiLogger.info("Total number of documents annotated ...: %3d", annotated.size());
        guiLogger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
      }
      guiLogger.info("Operation completed successfully");
      return created;
    }
  }

}
