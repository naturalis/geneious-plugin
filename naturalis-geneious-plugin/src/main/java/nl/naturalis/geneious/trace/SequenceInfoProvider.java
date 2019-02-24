package nl.naturalis.geneious.trace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
  static final int MAX_FILES_IN_MEMORY = 500;

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(SequenceInfoProvider.class);

  private final boolean inMemory;
  private final FastaFileSplitter splitter;
  private final List<Ab1SequenceInfo> ab1Sequences;
  private final List<FastaSequenceInfo> fastaSequences;
  private final Set<String> extractIDs;

  /**
   * Creates a new {@code SequenceInfoProvider} for the specified AB1/fasta files. Ordinarily these files would come from a file chooser
   * dialog presented to the user.
   * 
   * @param files
   */
  SequenceInfoProvider(File[] files) {
    this.inMemory = !disableFastaCache() && files.length <= MAX_FILES_IN_MEMORY;
    this.splitter = new FastaFileSplitter(inMemory);
    this.ab1Sequences = new ArrayList<>();
    this.fastaSequences = new ArrayList<>();
    this.extractIDs = new HashSet<>(files.length);
    for (File file : files) {
      try {
        if (isAb1File(file)) {
          Ab1SequenceInfo info = new Ab1SequenceInfo(file);
          ab1Sequences.add(info);
          addExtractId(extractIDs, info);
        } else if (isFastaFile(file)) {
          splitter.split(file).forEach(info -> {
            fastaSequences.add(info);
            addExtractId(extractIDs, info);
          });
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
   * 
   * @return
   */
  List<Ab1SequenceInfo> getAb1Files() {
    return ab1Sequences;
  }

  /**
   * Returns {@code FastaSequenceInfo} instances created from the files selected by the user.
   * 
   * @return
   */
  List<FastaSequenceInfo> getFastaFiles() {
    return fastaSequences;
  }

  /**
   * Returns the unique extractIDs of the selected files.
   * 
   * @return
   */
  Set<String> getExtractIDs() {
    return extractIDs;
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

  private static void addExtractId(Set<String> ids, SequenceInfo info) {
    try {
      ids.add(info.getNote().getExtractId());
    } catch (NotParsableException | IOException e) {
      // Will be dealt with later on (in AB1Importer/FastaImporter)
    }
  }

}
