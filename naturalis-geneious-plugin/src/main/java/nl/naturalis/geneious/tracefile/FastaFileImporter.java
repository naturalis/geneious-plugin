package nl.naturalis.geneious.tracefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Imports the fasta files of the AB1/Fasta import. Note that this is an Autocloseable class, and you should create instances of it using a
 * try-with-resources block. That will ensure that the temporary single-sequence fasta files will be deleted once the import completes.
 */
class FastaFileImporter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FastaFileImporter.class);

  private final List<FastaFileInfo> fastaFiles;

  FastaFileImporter(List<FastaFileInfo> fastaFiles) {
    guiLogger.debug("Initializing fasta file importer");
    this.fastaFiles = fastaFiles;
  }

  List<AnnotatedPluginDocument> importFiles() throws IOException {
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    LinkedHashMap<File, ArrayList<FastaFileInfo>> fastas = mapMothersToChildren();
    int enriched = 0;
    for (File mother : fastas.keySet()) {
      guiLogger.debugf(() -> format("Processing file \"%s\"", mother.getName()));
      for (FastaFileInfo info : fastas.get(mother)) {
        File f = info.getSourceFile();
        String[] contents = getFastaContents(f);
        guiLogger.debugf(() -> format("--> Processing sequence \"%s\"", contents[0]));
        DefaultNucleotideSequence seq = new DefaultNucleotideSequence(contents[0], contents[1]);
        AnnotatedPluginDocument apd = DocumentUtilities.createAnnotatedPluginDocument(seq);
        result.add(apd);
        try {
          info.getNote().attach(apd);
          ++enriched;
        } catch (SequenceNameNotParsableException e) {
          guiLogger.error(e.getMessage());
          continue;
        }
      }
    }
    guiLogger.info("Number of fasta files selected: %s", fastas.size());
    guiLogger.info("Number of single-sequence fasta files created: %s", fastaFiles.size());
    guiLogger.info("Number of files imported: %s", result.size());
    guiLogger.info("Number of documents enriched: %s", enriched);
    return result;
  }

  private LinkedHashMap<File, ArrayList<FastaFileInfo>> mapMothersToChildren() {
    LinkedHashMap<File, ArrayList<FastaFileInfo>> map = new LinkedHashMap<>();
    for (FastaFileInfo info : fastaFiles) {
      ArrayList<FastaFileInfo> infos = map.get(info.getMotherFile());
      if (infos == null) {
        infos = new ArrayList<>();
        map.put(info.getMotherFile(), infos);
      }
      infos.add(info);
    }
    return map;
  }

  // Returns the Fasta header as the 1st array elements and the nucleotide sequence as the 2nd.
  private static String[] getFastaContents(File f) throws IOException {
    String[] contents = new String[2];
    StringBuilder sb = new StringBuilder(1024);
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
      contents[0] = br.readLine().substring(1);
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        sb.append(line);
      }
      contents[1] = sb.toString();
    }
    return contents;
  }

}
