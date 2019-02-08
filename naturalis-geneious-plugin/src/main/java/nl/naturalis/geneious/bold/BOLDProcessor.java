package nl.naturalis.geneious.bold;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_ID;

class BOLDProcessor {

  private final GuiLogger logger;
  private final File boldFile;
  private final List<AnnotatedPluginDocument> selectedDocuments;
  private final boolean skipHeader;

  BOLDProcessor(File sampleSheet, List<AnnotatedPluginDocument> selectedDocuments,
      boolean createDummies) {
    this.logger = GuiLogManager.getLogger(BOLDProcessor.class);
    this.boldFile = sampleSheet;
    this.selectedDocuments = selectedDocuments;
    this.skipHeader = createDummies;
  }

  /**
   * Enriches the selected documents with data from the sample sheet. Documents and sample sheet records are linked using their extract ID.
   * In addition, if requested by the user, this routine will create dummy documents from sample sheet records IF their extract ID does not
   * exist yet in the database.
   */
  void process() {
    List<String[]> rows = loadBoldFile();
  }

  private List<String[]> loadBoldFile() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * Create a lookup table that maps the extract IDs of the selected documents to the selected documents themselves.
   */
  private Map<String, AnnotatedPluginDocument> makeLookupTable(List<AnnotatedPluginDocument> docs) {
    logger.debug("Creating lookup table for selected documents");
    Map<String, AnnotatedPluginDocument> map = new HashMap<>(docs.size() + 1, 1.0F);
    for (AnnotatedPluginDocument doc : docs) {
      String val = (String) EXTRACT_ID.getValue(doc);
      if (val != null) {
        map.put(val, doc);
      }
    }
    return map;
  }

}
