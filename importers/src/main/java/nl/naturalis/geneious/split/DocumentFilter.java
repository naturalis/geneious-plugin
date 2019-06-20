package nl.naturalis.geneious.split;

import static nl.naturalis.geneious.log.GuiLogger.format;

import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.StorableDocument;
import nl.naturalis.geneious.util.DocumentUtils;

/**
 * Converts the Geneious documents selected by the user into a list of {@kink StorableDocument} instances while applying
 * the filters in {@link NameSplitterConfig}.
 */
class DocumentFilter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentFilter.class);

  private final NameSplitterConfig cfg;

  DocumentFilter(NameSplitterConfig config) {
    this.cfg = config;
  }

  /**
   * Converts the Geneious documents selected by the user into a list of {@kink StorableDocument} instances while applying
   * the filters in {@link NameSplitterConfig}.
   * 
   * @return
   */
  List<StorableDocument> filterAndConvert() {
    List<StorableDocument> filtered = new ArrayList<StorableDocument>(cfg.getSelectedDocuments().size());
    for (AnnotatedPluginDocument apd : cfg.getSelectedDocuments()) {
      String name = apd.getName();
      if(DocumentUtils.getDocumentType(apd) == DocumentType.UNKNOWN) {
        guiLogger.warn("Ignoring document \"%s\". Unexpected document type: %s", name, apd.getDocumentClass());
      }
      if(DocumentUtils.getDocumentType(apd) == DocumentType.DUMMY) {
        guiLogger.debugf(() -> format("Ignoring dummy document \"%s\".", name));
        continue;
      }
      StorableDocument sd = new StorableDocument(apd);
      if(cfg.isIgnoreDocsWithNaturalisNote() && !sd.getSequenceInfo().getNaturalisNote().isEmpty()) {
        guiLogger.debugf(() -> format("Ignoring document \"%s\". Already annotated by Naturalis plugin", name));
        continue;
      }
      filtered.add(sd);
    }
    return filtered;
  }

}
