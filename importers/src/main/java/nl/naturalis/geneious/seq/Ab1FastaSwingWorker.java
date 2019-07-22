package nl.naturalis.geneious.seq;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.addAndReturnGeneratedDocuments;
import static nl.naturalis.geneious.util.PreconditionValidator.VALID_TARGET_FOLDER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.Annotator;
import nl.naturalis.geneious.name.StorableDocument;
import nl.naturalis.geneious.util.Messages.Info;
import nl.naturalis.geneious.util.PreconditionValidator;

/**
 * Manages and coordinates the import of AB1/fasta files into Geneious.
 * 
 * @author Ayco Holleman
 */
class Ab1FastaSwingWorker extends PluginSwingWorker<Ab1FastaImportConfig> {

  private static final GuiLogger logger = GuiLogManager.getLogger(Ab1FastaSwingWorker.class);

  Ab1FastaSwingWorker(Ab1FastaImportConfig config) {
    super(config);
  }

  @Override
  protected List<AnnotatedPluginDocument> performOperation() throws IOException, DatabaseServiceException, NonFatalException {
    PreconditionValidator validator = new PreconditionValidator(config, VALID_TARGET_FOLDER);
    validator.validate();
    List<AnnotatedPluginDocument> created = null;
    try(SequenceInfoProvider provider = new SequenceInfoProvider(config.getFiles())) {
      List<StorableDocument> docs = new ArrayList<>();
      List<StorableDocument> annotated = null;
      Ab1Importer ab1Importer = null;
      FastaImporter fastaImporter = null;
      List<Ab1Info> ab1s = provider.getAb1Sequences();
      if(ab1s.size() != 0) {
        ab1Importer = new Ab1Importer(ab1s);
        docs.addAll(ab1Importer.importFiles());
      }
      List<FastaInfo> fastas = provider.getFastaSequences();
      if(fastas.size() != 0) {
        fastaImporter = new FastaImporter(fastas);
        docs.addAll(fastaImporter.importFiles());
      }
      if(docs.size() != 0) {
        Annotator annotator = new Annotator(config, docs);
        annotated = annotator.annotateDocuments();
        created = new ArrayList<>(docs.size());
        for(StorableDocument doc : docs) {
          doc.saveAnnotations();
          created.add(doc.getGeneiousDocument());
        }
        created = addAndReturnGeneratedDocuments(created, true, Collections.emptyList());
      }
      int processed = 0, rejected = 0, imported = 0;
      if(ab1Importer != null) {
        processed = ab1Importer.getNumProcessed();
        rejected = ab1Importer.getNumRejected();
        imported = ab1Importer.getNumImported();
        logger.info("Number of AB1 files selected ..........: %3d", ab1s.size());
        logger.info("Number of AB1 documents created .......: %3d", processed);
        logger.info("Number of AB1 documents rejected ......: %3d", rejected);
        logger.info("Number of AB1 documents imported ......: %3d", imported);
      }
      if(fastaImporter != null) {
        processed += fastaImporter.getNumProcessed();
        rejected += fastaImporter.getNumRejected();
        imported += fastaImporter.getNumImported();
        logger.info("Number of FASTA files selected ........: %3d", fastas.size());
        logger.info("Number of FASTA documents created .....: %3d", fastaImporter.getNumProcessed());
        logger.info("Number of FASTA documents rejected ....: %3d", fastaImporter.getNumRejected());
        logger.info("Number of FASTA documents imported ....: %3d", fastaImporter.getNumImported());
      }
      if(ab1Importer != null && fastaImporter != null) {
        logger.info("Total number of files selected ........: %3d", config.getFiles().length);
        logger.info("Total number of documents created .....: %3d", processed);
        logger.info("Total number of documents rejected ....: %3d", rejected);
        logger.info("Total number of documents imported ....: %3d", imported);
      }
      if(annotated != null) {
        logger.info("Total number of documents annotated ...: %3d", annotated.size());
        logger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
      }
      Info.operationCompletedSuccessfully(logger, Ab1FastaDocumentOperation.NAME);
      return created;
    }
  }

  @Override
  protected String getLogTitle() {
    return Ab1FastaDocumentOperation.NAME;
  }

}
