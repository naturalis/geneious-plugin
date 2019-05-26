package nl.naturalis.geneious.split;

import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.StorableDocument;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.name.NameUtil.isDummy;

/**
 * Converts the user-selected Geneious documents into a list of {@kink StorableDocument} instances while applying the user-selected filters
 * specified in the {@link NameSplitterConfig} object.
 */
class DocumentFilter {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(DocumentFilter.class);

  private final NameSplitterConfig cfg;

  DocumentFilter(NameSplitterConfig config) {
    this.cfg = config;
  }

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
