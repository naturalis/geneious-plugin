package nl.naturalis.geneious.seq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.ImportedFromNote;

import static com.biomatters.geneious.publicapi.documents.DocumentUtilities.createAnnotatedPluginDocument;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * Imports the AB1 files selected by the user into Geneious.
 */
class FastaImporter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FastaImporter.class);

  private final List<FastaInfo> sequences;

  private int processed;
  private int imported;
  private int rejected;

  FastaImporter(List<FastaInfo> sequences) {
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
    LinkedHashMap<File, ArrayList<FastaInfo>> fastas = mapMothersToChildren();
    DefaultNucleotideSequence sequence;
    AnnotatedPluginDocument apd;
    for (File motherFile : fastas.keySet()) {
      guiLogger.debugf(() -> format("Importing file %s", motherFile.getName()));
      Date date = new Date(motherFile.lastModified());
      for (FastaInfo info : fastas.get(motherFile)) {
        ++processed;
        guiLogger.debugf(() -> format("--> Importing sequence %s", info.getName()));
        sequence = new DefaultNucleotideSequence(info.getName(), null, info.getSequence(), date);
        apd = createAnnotatedPluginDocument(sequence);
        ++imported;
        ImportableDocument doc = new ImportableDocument(apd, info);
        doc.attach(new ImportedFromNote(motherFile));
        importables.add(doc);
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

  private LinkedHashMap<File, ArrayList<FastaInfo>> mapMothersToChildren() {
    LinkedHashMap<File, ArrayList<FastaInfo>> map = new LinkedHashMap<>();
    for (FastaInfo info : sequences) {
      ArrayList<FastaInfo> infos = map.get(info.getSourceFile());
      if (infos == null) {
        infos = new ArrayList<>();
        map.put(info.getSourceFile(), infos);
      }
      infos.add(info);
    }
    return map;
  }

}
