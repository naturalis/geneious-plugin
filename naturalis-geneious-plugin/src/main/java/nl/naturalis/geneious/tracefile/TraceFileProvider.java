package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import nl.naturalis.geneious.NaturalisPreferencesOptions;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.util.DocumentUtils.isAb1File;
import static nl.naturalis.geneious.util.DocumentUtils.isFastaFile;

/**
 * Divides the files selected by the user in the file chooser dialog into AB1 files and fasta files, and calls the {@link FastaFileSplitter}
 * to split the fasta files into single-sequence fasta files. Note that this is an Autocloseable class; you should create instances of it
 * using a try-with-resources block. That will ensure that the temporary single-sequence fasta files will be deleted once the import
 * completes.
 * 
 * {@link FastaFileSplitter}.
 */
class TraceFileProvider implements AutoCloseable {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(TraceFileProvider.class);

  private final FastaFileSplitter splitter;
  private final List<Ab1FileInfo> ab1Files;
  private final List<FastaFileInfo> fastaFiles;

  TraceFileProvider(File[] files) {
    this.splitter = new FastaFileSplitter();
    this.ab1Files = new ArrayList<>();
    this.fastaFiles = new ArrayList<>();
    for (File file : files) {
      try {
        if (isAb1File(file)) {
          ab1Files.add(new Ab1FileInfo(file));
        } else if (isFastaFile(file)) {
          splitter.split(file).forEach(child -> fastaFiles.add(new FastaFileInfo(child, file)));
        } else {
          guiLogger.error("Cannot determine file type of %s (probably a bug)", file.getName());
        }
      } catch (IOException e) {
        guiLogger.error("Error processing file %s", e, file.getName());
      }
    }
  }

  public List<Ab1FileInfo> getAb1Files() {
    return ab1Files;
  }

  public List<FastaFileInfo> getFastaFiles() {
    return fastaFiles;
  }

  @Override
  public void close() throws IOException {
    File dir = splitter.getFastaTempDirectory();
    if (NaturalisPreferencesOptions.deleteTmpFastaFiles()) {
      guiLogger.debugf(() -> format("Deleting temporary fasta files in %s", dir.getAbsolutePath()));
      FileUtils.deleteDirectory(dir);
    } else {
      guiLogger.warnf(() -> format("Please make sure to manually delete temporary fasta file in %s", dir.getAbsolutePath()));
    }
  }

}
