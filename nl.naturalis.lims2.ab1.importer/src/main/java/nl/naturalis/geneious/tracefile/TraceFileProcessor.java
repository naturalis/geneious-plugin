package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.split.BadFileNameException;
import nl.naturalis.geneious.split.FileNameParser;
import nl.naturalis.geneious.util.RuntimeSettings;

public class TraceFileProcessor {

  private final GuiLogger logger;
  private final File[] files;

  public TraceFileProcessor(File[] traceFiles) {
    this.logger = new GuiLogger(RuntimeSettings.INSTANCE.getLogLevel());
    this.files = traceFiles;
  }

  public List<AnnotatedPluginDocument> process() {
    List<AnnotatedPluginDocument> result = new ArrayList<>(files.length);
    int good = 0;
    int enriched = 0;
    int bad = 0;
    try {
      for (File f : files) {
        logger.debug("Processing file: %s", f.getName());
        List<AnnotatedPluginDocument> apds;
        try {
          apds = PluginUtilities.importDocuments(f, null);
        } catch (IOException | DocumentImportException e) {
          logger.error("Error processing file %s", e, f.getAbsolutePath());
          bad++;
          continue;
        }
        assert (apds.size() == 1);
        try {
          NaturalisNote note = new FileNameParser(f.getName()).parse();
          if (note != null) {
            note.attach(apds.get(0));
            enriched++;
          }
        } catch (BadFileNameException e) {
          logger.error(e.getMessage());
          continue;
        }
        good++;
        result.addAll(apds);
      }
      logger.info("Number of files selected: %s", files.length);
      logger.info("Number of files successfully imported: %s", good);
      logger.info("Number of files with parsable file names: %s", enriched);
      logger.info("Number of files not imported: %s", bad);
    } finally {
      logger.showLog("Fasta/AB1 import log");
    }
    return result;
  }

}
