package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.split.NotParsableException;

import static nl.naturalis.geneious.NaturalisPreferencesOptions.disableFastaCache;
import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.util.DocumentUtils.isAb1File;
import static nl.naturalis.geneious.util.DocumentUtils.isFastaFile;

/**
 * Divides the files selected by the user in the file chooser dialog into AB1 files and fasta files and then calls the
 * {@link FastaFileSplitter} to split the fasta files into nucleotide sequences. Note that this is an Autocloseable class. You SHOULD create
 * instances of it using a try-with-resources block. This will ensure that the temporary files created by the fasta will be deleted once the
 * import completes.
 * 
 * {@link FastaFileSplitter}.
 */
class SequenceInfoProvider implements AutoCloseable {

  /**
   * The maximum number of user-selected fasta files that will be dealt with in-memory (500). If the user selects more than this number of
   * fasta files, or if he/she has disabled fasta file caching in the Prefences panel, the individual nucleotide sequences will be written
   * to temporary files.
   */
  static final int MAX_FASTAS_IN_MEMORY = 500;

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceInfoProvider.class);

  private final boolean inMemory;
  private final FastaFileSplitter splitter;
  private final List<Ab1SequenceInfo> ab1Sequences;
  private final List<FastaSequenceInfo> fastaSequences;

  /**
   * Creates a new {@code SequenceInfoProvider} for the specified AB1/fasta files. Ordinarily these files would come from a file chooser
   * dialog presented to the user.
   * 
   * @param files
   * @throws NotParsableException
   */
  SequenceInfoProvider(File[] files) {
    this.inMemory = !disableFastaCache() && files.length <= MAX_FASTAS_IN_MEMORY;
    this.splitter = new FastaFileSplitter(inMemory);
    this.ab1Sequences = new ArrayList<>();
    this.fastaSequences = new ArrayList<>();
    guiLogger.debug(() -> "Separating AB1 files from fasta files");
    for (File f : files) {
      try {
        if (isAb1File(f)) {
          ab1Sequences.add(new Ab1SequenceInfo(f));
        } else if (isFastaFile(f)) {
          fastaSequences.addAll(splitter.split(f));
        } else {
          guiLogger.error("Cannot determine file type of %s", f.getName());
        }
      } catch (IOException e) {
        guiLogger.error("Error processing %s: %s", f.getPath(), e.getMessage());
      }
    }
  }

  /**
   * Returns {@code Ab1SequenceInfo} instances created from the files selected by the user.
   * 
   * @return
   */
  List<Ab1SequenceInfo> getAb1Sequences() {
    return ab1Sequences;
  }

  /**
   * Returns {@code FastaSequenceInfo} instances created from the files selected by the user.
   * 
   * @return
   */
  List<FastaSequenceInfo> getFastaSequences() {
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
      if (NaturalisPreferencesOptions.deleteTmpFastaFiles()) {
        guiLogger.debugf(() -> format("Deleting temporary fasta files in %s", dir.getAbsolutePath()));
        FileUtils.deleteDirectory(dir);
      } else {
        guiLogger.warnf(() -> format("Please remember to delete temporary fasta files in %s", dir.getAbsolutePath()));
      }
    }
  }

}
