package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.geneious.NaturalisPreferencesOptions.disableFastaCache;
import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.util.DocumentUtils.isAb1File;
import static nl.naturalis.geneious.util.DocumentUtils.isFastaFile;

/**
 * Divides the files selected by the user in the file chooser dialog into AB1 files and fasta files, and calls the {@link FastaFileSplitter}
 * to split the fasta files into nucleotide sequences. Note that this is an Autocloseable class; you should create instances of it using a
 * try-with-resources block. That will ensure that the temporary single-sequence fasta files will be deleted once the import completes.
 * 
 * {@link FastaFileSplitter}.
 */
class SequenceInfoProvider implements AutoCloseable {

  /**
   * The maximum number of fasta files that will be kept in-memory (500). If a user selects more than this number of fasta files, the
   * individual nucleotide sequences will be written to temporary files.
   */
  static final int MAX_FILES_IN_MEMORY = 500;

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceInfoProvider.class);

  private final boolean inMemory;
  private final FastaFileSplitter splitter;
  private final List<Ab1SequenceInfo> ab1Sequences;
  private final List<FastaSequenceInfo> fastaSequences;

  SequenceInfoProvider(File[] files) {
    this.inMemory = !disableFastaCache() && files.length <= MAX_FILES_IN_MEMORY;
    this.splitter = new FastaFileSplitter(inMemory);
    this.ab1Sequences = new ArrayList<>();
    this.fastaSequences = new ArrayList<>();
    for (File file : files) {
      try {
        if (isAb1File(file)) {
          ab1Sequences.add(new Ab1SequenceInfo(file));
        } else if (isFastaFile(file)) {
          splitter.split(file).forEach(fastaSequences::add);
        } else {
          guiLogger.error("Cannot determine file type of %s (probably a bug)", file.getName());
        }
      } catch (IOException e) {
        guiLogger.error("Error processing file %s", e, file.getName());
      }
    }
  }

  /**
   * Returns {@code Ab1SequenceInfo} instances created from the files selected by the user.
   * @return
   */
  public List<Ab1SequenceInfo> getAb1Files() {
    return ab1Sequences;
  }

  /**
   * Returns {@code FastaSequenceInfo} instances created from the files selected by the user.
   * @return
   */
  public List<FastaSequenceInfo> getFastaFiles() {
    return fastaSequences;
  }

  @Override
  public void close() throws IOException {
    if (!inMemory) {
      File dir = splitter.getFastaTempDirectory();
      if (NaturalisPreferencesOptions.deleteTmpFastaFiles()) {
        guiLogger.debugf(() -> format("Deleting temporary fasta files in %s", dir.getAbsolutePath()));
        FileUtils.deleteDirectory(dir);
      } else {
        guiLogger.warnf(() -> format("Please remember to delete temporary fasta files in %s", dir.getAbsolutePath()));
      }
    }
  }

}
