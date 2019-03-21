package nl.naturalis.geneious.seq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.createAnnotatedPluginDocument;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Imports the AB1 files selected by the user into Geneious.
 */
class FastaImporter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FastaImporter.class);

  private final List<FastaSequenceInfo> sequences;

  private int processed;
  private int imported;
  private int rejected;

  FastaImporter(List<FastaSequenceInfo> sequences) {
    guiLogger.info("Starting fasta file importer");
    this.sequences = sequences;
  }

  /**
   * Imports the fasta files.
   * 
   * @return
   * @throws IOException
   */
  List<ImportableDocument> importFiles() throws IOException {
    processed = imported = rejected = 0;
    List<ImportableDocument> importables = new ArrayList<>();
    LinkedHashMap<File, ArrayList<FastaSequenceInfo>> fastas = mapMothersToChildren();
    DefaultNucleotideSequence sequence;
    AnnotatedPluginDocument document;
    for (File motherFile : fastas.keySet()) {
      guiLogger.debugf(() -> format("Importing file \"%s\"", motherFile.getName()));
      for (FastaSequenceInfo info : fastas.get(motherFile)) {
        ++processed;
        guiLogger.debugf(() -> format("--> Importing sequence \"%s\"", info.getName()));
        sequence = new DefaultNucleotideSequence(info.getName(), info.getSequence());
        document = createAnnotatedPluginDocument(sequence);
        ++imported;
        importables.add(new ImportableDocument(document, info));
      }
    }
    return importables;
  }

  int getNumProcessed() {
    return processed;
  }

  int getNumImported() {
    return imported;
  }

  int getNumRejected() {
    return rejected;
  }

  private LinkedHashMap<File, ArrayList<FastaSequenceInfo>> mapMothersToChildren() {
    LinkedHashMap<File, ArrayList<FastaSequenceInfo>> map = new LinkedHashMap<>();
    for (FastaSequenceInfo info : sequences) {
      ArrayList<FastaSequenceInfo> infos = map.get(info.getSourceFile());
      if (infos == null) {
        infos = new ArrayList<>();
        map.put(info.getSourceFile(), infos);
      }
      infos.add(info);
    }
    return map;
  }

}
