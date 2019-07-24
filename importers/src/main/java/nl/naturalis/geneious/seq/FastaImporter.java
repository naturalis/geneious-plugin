package nl.naturalis.geneious.seq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.StorableDocument;
import nl.naturalis.geneious.note.ImportedFromNote;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.createAnnotatedPluginDocument;
import static nl.naturalis.geneious.log.GuiLogger.format;

/**
 * Imports the fasta files selected by the user into Geneious.
 */
class FastaImporter {

  private static final GuiLogger logger = GuiLogManager.getLogger(FastaImporter.class);

  private final List<FastaInfo> sequences;

  private int processed;
  private int imported;
  private int rejected;

  FastaImporter(List<FastaInfo> sequences) {
    logger.info("Starting fasta file importer");
    this.sequences = sequences;
  }

  /**
   * Imports the fasta files.
   * 
   * @return
   * @throws IOException
   */
  List<StorableDocument> importFiles() throws IOException {
    processed = imported = rejected = 0;
    List<StorableDocument> importables = new ArrayList<>();
    LinkedHashMap<File, ArrayList<FastaInfo>> fastas = mapMothersToChildren();
    DefaultNucleotideSequence sequence;
    AnnotatedPluginDocument apd;
    for(File motherFile : fastas.keySet()) {
      logger.debugf(() -> format("Importing file %s", motherFile.getName()));
      Date date = new Date(motherFile.lastModified());
      for(FastaInfo info : fastas.get(motherFile)) {
        ++processed;
        logger.debugf(() -> format("--> Importing sequence %s", info.getName()));
        sequence = new DefaultNucleotideSequence(info.getName(), null, info.getSequence(), date);
        apd = createAnnotatedPluginDocument(sequence);
        ++imported;
        StorableDocument doc = new StorableDocument(apd, info);
        doc.attach(new ImportedFromNote(motherFile));
        importables.add(doc);
      }
    }
    return importables;
  }

  /**
   * Returns the number of nucleotide sequences processed.
   * 
   * @return
   */
  int getNumProcessed() {
    return processed;
  }

  /**
   * Returns the number of nucleotide sequences imported.
   * 
   * @return
   */
  int getNumImported() {
    return imported;
  }

  /**
   * Returns the number of invalid nucleotide sequences.
   * 
   * @return
   */
  int getNumRejected() {
    return rejected;
  }

  private LinkedHashMap<File, ArrayList<FastaInfo>> mapMothersToChildren() {
    LinkedHashMap<File, ArrayList<FastaInfo>> map = new LinkedHashMap<>();
    for(FastaInfo info : sequences) {
      ArrayList<FastaInfo> infos = map.get(info.getImportedFrom());
      if(infos == null) {
        infos = new ArrayList<>();
        map.put(info.getImportedFrom(), infos);
      }
      infos.add(info);
    }
    return map;
  }

}
