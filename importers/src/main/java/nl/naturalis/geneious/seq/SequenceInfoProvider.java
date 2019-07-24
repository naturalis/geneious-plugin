package nl.naturalis.geneious.seq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.NotParsableException;

import static nl.naturalis.geneious.Settings.settings;
import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.util.DocumentUtils.isAb1File;
import static nl.naturalis.geneious.util.DocumentUtils.isFastaFile;

/**
 * Separates AB1 files from fasta files and then calls the {@link FastaFileSplitter} to split the fasta files into
 * individual nucleotide sequences. Note that this is an Autocloseable class. You SHOULD create instances of it using a
 * try-with-resources block. This will ensure that the temporary files created by the {@code FastaFileSplitter} will be
 * deleted once the import completes.
 * 
 * {@link FastaFileSplitter}.
 */
class SequenceInfoProvider implements AutoCloseable {

  /**
   * The maximum number of user-selected fasta files that will be dealt with in-memory (500). If the user selects more
   * than this number of fasta files, or if he/she has disabled fasta file caching in the Prefences panel, the individual
   * nucleotide sequences will be written to temporary files, otherwise they will be processed in-memory.
   */
  static final int MAX_FASTAS_IN_MEMORY = 500;

  private static final GuiLogger logger = GuiLogManager.getLogger(SequenceInfoProvider.class);

  private final boolean inMemory;
  private final FastaFileSplitter splitter;
  private final List<Ab1Info> ab1Sequences;
  private final List<FastaInfo> fastaSequences;

  /**
   * Creates a new {@code SequenceInfoProvider} for the specified AB1/fasta files. Ordinarily these files would come from
   * a file chooser dialog presented to the user.
   * 
   * @param files
   * @throws NotParsableException
   */
  SequenceInfoProvider(File[] files) {
    this.inMemory = !settings().isDisableFastaCache() && files.length <= MAX_FASTAS_IN_MEMORY;
    this.splitter = new FastaFileSplitter(inMemory);
    this.ab1Sequences = new ArrayList<>();
    this.fastaSequences = new ArrayList<>();
    logger.debug(() -> "Separating AB1 files from fasta files");
    for (File f : files) {
      try {
        if (isAb1File(f)) {
          ab1Sequences.add(new Ab1Info(f));
        } else if (isFastaFile(f)) {
          fastaSequences.addAll(splitter.split(f));
        } else {
          logger.error("Cannot determine file type of %s", f.getName());
        }
      } catch (IOException e) {
        logger.error("Error processing %s: %s", f.getPath(), e.getMessage());
      }
    }
  }

  /**
   * Returns {@code Ab1SequenceInfo} instances created from the files selected by the user.
   * 
   * @return
   */
  List<Ab1Info> getAb1Sequences() {
    return ab1Sequences;
  }

  /**
   * Returns {@code FastaSequenceInfo} instances created from the files selected by the user.
   * 
   * @return
   */
  List<FastaInfo> getFastaSequences() {
    return fastaSequences;
  }

  /**
   * Returns the unique extractIDs of the selected files.
   * 
   * @return
   */
  Set<String> getExtractIDs() {
    return null;
  }

  @Override
  public void close() throws IOException {
    if (!inMemory) {
      File dir = splitter.getFastaTempDirectory();
      if (settings().isDeleteTmpFastas()) {
        logger.debugf(() -> format("Deleting temporary fasta files in %s", dir.getPath()));
        FileUtils.deleteDirectory(dir);
      } else {
        logger.info("Temporary fasta files were saved in %s", dir.getPath());
        logger.info("Please remember to delete the directory");
      }
    }
  }

}
