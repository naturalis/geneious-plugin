package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.geneious.util.DocumentUtils.isAb1File;
import static nl.naturalis.geneious.util.DocumentUtils.isFastaFile;

/**
 * The TraceFilePreprocessor divides the files selected by the user in the file chooser dialog into AB1 files and fasta files.
 * 
 * {@link FastaFileSplitter}.
 */
class TraceFilePreprocessor {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(TraceFileDocumentOperation.class);

  private final File[] files;
  private final FastaFileSplitter splitter;
  private final List<Ab1FileInfo> ab1Files;
  private final List<FastaFileInfo> fastaFiles;

  TraceFilePreprocessor(File[] files) {
    this.files = files;
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

}
