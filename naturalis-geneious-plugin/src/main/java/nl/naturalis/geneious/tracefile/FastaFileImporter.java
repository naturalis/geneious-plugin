package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.split.SequenceNameNotParsableException;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Imports the fasta files of the AB1/Fasta import.
 */
class FastaFileImporter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FastaFileImporter.class);

  private final List<FastaSequenceInfo> fastaFiles;
  private final TraceFileImportStats stats;

  FastaFileImporter(List<FastaSequenceInfo> fastaFiles, TraceFileImportStats stats) {
    guiLogger.info("Starting fasta file importer");
    this.fastaFiles = fastaFiles;
    this.stats = stats;
  }

  List<AnnotatedPluginDocument> importFiles() throws IOException {
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    TraceFileImportStats myStats = new TraceFileImportStats();
    LinkedHashMap<File, ArrayList<FastaSequenceInfo>> fastas = mapMothersToChildren();
    for (File mother : fastas.keySet()) {
      guiLogger.debugf(() -> format("Processing file \"%s\"", mother.getName()));
      for (FastaSequenceInfo info : fastas.get(mother)) {
        ++myStats.processed;
        if (guiLogger.isDebugEnabled()) {
          guiLogger.debug(String.format("--> Processing sequence \"%s\"", info.getName()));
        }
        DefaultNucleotideSequence seq = new DefaultNucleotideSequence(info.getName(), info.getSequence());
        AnnotatedPluginDocument apd = DocumentUtilities.createAnnotatedPluginDocument(seq);
        result.add(apd);
        try {
          info.getNote().attach(apd);
          ++myStats.enriched;
        } catch (SequenceNameNotParsableException e) {
          guiLogger.error(e.getMessage());
          continue;
        }
      }
    }
    guiLogger.info("Number of fasta files selected: %s", fastas.size());
    guiLogger.info("Number of fasta files processed: %s", myStats.processed);
    guiLogger.info("Number of fasta files rejected: %s", 0);
    guiLogger.info("Number of fasta files imported: %s", result.size());
    guiLogger.info("Number of fasta documents enriched: %s", myStats.enriched);
    stats.merge(myStats);
    return result;
  }

  private LinkedHashMap<File, ArrayList<FastaSequenceInfo>> mapMothersToChildren() {
    LinkedHashMap<File, ArrayList<FastaSequenceInfo>> map = new LinkedHashMap<>();
    for (FastaSequenceInfo info : fastaFiles) {
      ArrayList<FastaSequenceInfo> infos = map.get(info.getMotherFile());
      if (infos == null) {
        infos = new ArrayList<>();
        map.put(info.getMotherFile(), infos);
      }
      infos.add(info);
    }
    return map;
  }

}
