package nl.naturalis.geneious.tracefile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
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
        if (f.getName().endsWith(".ab1")) {
          if (f.getName().contains("_")) {
            String[] chunks = StringUtils.split(f.getName(), '_');
            if (chunks.length < 5) {
              logger.error("Invalid file (not enough underscores in name): %s", f.getName());
              continue;
            }
            NaturalisNote note = new NaturalisNote();
            note.setExtractId(chunks[0]);
            note.setPcrPlateId(chunks[3]);
            int i = chunks[4].indexOf('-');
            String marker = i == -1 ? null : chunks[4].substring(0, i);
            note.setMarker(marker);
            note.attach(apds.get(0));
            enriched++;
          }
        }
        result.addAll(apds);
        good++;
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
