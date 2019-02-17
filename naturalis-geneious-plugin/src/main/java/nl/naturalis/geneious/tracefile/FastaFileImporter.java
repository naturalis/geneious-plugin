package nl.naturalis.geneious.tracefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

import org.apache.commons.io.FileUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.BadFileNameException;
import nl.naturalis.geneious.split.SequenceNameParser;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

class FastaFileImporter implements AutoCloseable {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(FastaFileImporter.class);

  private final List<File> fastaFiles;
  private final FastaFileSplitter splitter;

  FastaFileImporter(List<File> fastaFiles) {
    guiLogger.debug("Initializing fasta file importer");
    this.fastaFiles = fastaFiles;
    this.splitter = new FastaFileSplitter();
  }

  List<AnnotatedPluginDocument> importFiles() throws IOException {
    List<AnnotatedPluginDocument> result = new ArrayList<>();
    int imported = 0;
    int rejected = 0;
    int enriched = 0;
    SequenceNameParser parser = new SequenceNameParser();
    for (File fastaFile : fastaFiles) {
      guiLogger.debugf(() -> format("Processing file: %s", fastaFile.getName()));
      List<File> files = splitter.split(fastaFile);
      for (File f : files) {
        guiLogger.debugf(() -> format("Processing temporary fasta file: %s", f.getName()));
        List<AnnotatedPluginDocument> apds;
        try {
          //apds = PluginUtilities.importDocuments(f, null);
          DefaultNucleotideSequence seq = new DefaultNucleotideSequence(getSequenceName(f), getSequence(f));
          AnnotatedPluginDocument apd=DocumentUtilities.createAnnotatedPluginDocument(seq);
          apds=Arrays.asList(apd);
          DocumentUtilities.addGeneratedDocument(apd, true);
          // DocumentUtilities.
          ++imported;
        } catch (Exception e) {
          guiLogger.error("Error processing file %s", e, f.getAbsolutePath());
          ++rejected;
          continue;
        }
        if (apds.size() != 1) {
          /*
           * We don't understand yet under what circumstances PluginUtilities.importDocuments would return multiple documents, so we
           * basically make an assertion here.
           */
          guiLogger.error("Unexpected number of documents created from a single file: %s. Aborting.", apds.size());
          break;
        }
        try {
          NaturalisNote note = parser.parseFasta(getSequenceName(f));
          if (note != null) {
            note.attach(apds.get(0));
            ++enriched;
          }
        } catch (BadFileNameException e) {
          guiLogger.error(e.getMessage());
          continue;
        }
        result.addAll(apds);
      }
    }
    guiLogger.info("Number of fasta files selected: %s", fastaFiles.size());
    guiLogger.info("Number of single-sequence fasta files created: %s", splitter.getSplitCount());
    guiLogger.info("Number of files imported: %s", imported);
    guiLogger.info("Number of files rejected: %s", rejected);
    guiLogger.info("Number of documents enriched: %s", enriched);
    return result;
  }

  @Override
  public void close() throws IOException {
    File dir = splitter.getFastaTempDirectory();
    if (NaturalisPreferencesOptions.deleteTmpFastaFiles()) {
      guiLogger.debugf(() -> format("Deleting temporary fasta files in %s", dir.getAbsolutePath()));
      FileUtils.deleteDirectory(dir);
    } else {
      guiLogger.warnf(() -> format("Directory containing intermediate fasta files not deleted: %s", dir.getAbsolutePath()));
    }
  }

  private static String getSequenceName(File f) throws IOException {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
      return br.readLine().substring(1);
    }
  }

  private static String getSequence(File f) throws IOException {
    StringBuilder sb = new StringBuilder(1024);
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
      br.readLine();
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        sb.append(line);
      }
      return sb.toString();
    }
  }
}
