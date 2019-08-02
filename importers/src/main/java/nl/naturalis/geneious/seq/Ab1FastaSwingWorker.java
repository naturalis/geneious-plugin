package nl.naturalis.geneious.seq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import jebl.util.ProgressListener;
import nl.naturalis.geneious.NonFatalException;
import nl.naturalis.geneious.PluginSwingWorker;
import nl.naturalis.geneious.Precondition;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.Annotator;
import nl.naturalis.geneious.name.StorableDocument;
import nl.naturalis.geneious.util.Messages.Info;

import static nl.naturalis.geneious.Precondition.VALID_TARGET_FOLDER;

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
    try (SequenceInfoProvider provider = new SequenceInfoProvider(config.getFiles())) {
      List<AnnotatedPluginDocument> created = null;
      List<StorableDocument> docs = new ArrayList<>();
      List<StorableDocument> annotated = null;
      Ab1Importer ab1Importer = null;
      FastaImporter fastaImporter = null;
      List<Ab1Info> ab1s = provider.getAb1Sequences();
      if (!ab1s.isEmpty()) {
        ab1Importer = new Ab1Importer(ab1s);
        docs.addAll(ab1Importer.importFiles());
      }
      List<FastaInfo> fastas = provider.getFastaSequences();
      if (!fastas.isEmpty()) {
        fastaImporter = new FastaImporter(fastas);
        docs.addAll(fastaImporter.importFiles());
      }
      if (docs.size() != 0) {
        Annotator annotator = new Annotator(config, docs);
        annotated = annotator.annotateDocuments();
        created = new ArrayList<>(docs.size());
        for (StorableDocument doc : docs) {
          doc.saveAnnotations();
          created.add(config.getTargetFolder().addDocumentCopy(doc.getGeneiousDocument(), ProgressListener.EMPTY));
        }
      }
      int processed = 0, rejected = 0, imported = 0;
      if (!ab1s.isEmpty()) {
        processed = ab1Importer.getNumProcessed();
        rejected = ab1Importer.getNumRejected();
        imported = ab1Importer.getNumImported();
        logger.info("Number of AB1 files selected ..........: %3d", ab1s.size());
        logger.info("Number of AB1 documents created .......: %3d", processed);
        logger.info("Number of AB1 documents rejected ......: %3d", rejected);
        logger.info("Number of AB1 documents imported ......: %3d", imported);
      }
      if (!fastas.isEmpty()) {
        processed += fastaImporter.getNumProcessed();
        rejected += fastaImporter.getNumRejected();
        imported += fastaImporter.getNumImported();
        logger.info("Number of FASTA files selected ........: %3d", fastas.size());
        logger.info("Number of FASTA documents created .....: %3d", fastaImporter.getNumProcessed());
        logger.info("Number of FASTA documents rejected ....: %3d", fastaImporter.getNumRejected());
        logger.info("Number of FASTA documents imported ....: %3d", fastaImporter.getNumImported());
      }
      if (!ab1s.isEmpty() && !fastas.isEmpty()) {
        logger.info("Total number of files selected ........: %3d", config.getFiles().length);
        logger.info("Total number of documents created .....: %3d", processed);
        logger.info("Total number of documents rejected ....: %3d", rejected);
        logger.info("Total number of documents imported ....: %3d", imported);
      }
      if (annotated != null) {
        logger.info("Total number of documents annotated ...: %3d", annotated.size());
        logger.info("Total number of annotation failures ...: %3d", docs.size() - annotated.size());
      }
      Info.operationCompletedSuccessfully(logger, Ab1FastaDocumentOperation.NAME);
      return created == null ? Collections.emptyList() : created;
    }
  }

  @Override
  protected String getLogTitle() {
    return Ab1FastaDocumentOperation.NAME;
  }

  @Override
  protected Set<Precondition> getPreconditions() {
    return EnumSet.of(VALID_TARGET_FOLDER);
  }

}
