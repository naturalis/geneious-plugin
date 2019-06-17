package nl.naturalis.geneious.split;

import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.name.StorableDocument;

import static nl.naturalis.geneious.log.GuiLogger.format;
import static nl.naturalis.geneious.name.NameUtil.isDummy;

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
      if (isDummy(apd)) {
        guiLogger.debugf(() -> format("Ignoring dummy document \"%s\".", name));
        continue;
      }
      StorableDocument sd = new StorableDocument(apd);
      if (cfg.isIgnoreDocsWithNaturalisNote() && !sd.getSequenceInfo().getNaturalisNote().isEmpty()) {
        guiLogger.debugf(() -> format("Ignoring document \"%s\". Already has Naturalis annotations.", name));
        continue;
      }
      filtered.add(sd);
    }
    return filtered;
  }

}
