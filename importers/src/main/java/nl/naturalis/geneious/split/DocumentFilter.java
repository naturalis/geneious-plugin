package nl.naturalis.geneious.split;

import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.StoredDocument;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;
import nl.naturalis.geneious.util.DocumentUtils;

import static nl.naturalis.geneious.log.GuiLogger.format;

/**
 * Converts the Geneious documents selected by the user into a list of {@kink StorableDocument} instances while applying the filters in
 * {@link SplitNameConfig}.
 */
class DocumentFilter {

  private static final GuiLogger logger = GuiLogManager.getLogger(DocumentFilter.class);

  private final SplitNameConfig config;

  DocumentFilter(SplitNameConfig config) {
    this.config = config;
  }

  /**
   * Converts the Geneious documents selected by the user into a list of {@kink StorableDocument} instances while applying the filters in
   * {@link SplitNameConfig}.
   * 
   * @return
   */
  List<StoredDocument> applyFilters() {
    List<StoredDocument> filtered = new ArrayList<>(config.getSelectedDocuments().size());
    for (AnnotatedPluginDocument doc : config.getSelectedDocuments()) {
      String name = doc.getName();
      if (DocumentUtils.getDocumentType(doc) == DocumentType.UNKNOWN) {
        logger.warn("Ignoring document \"%s\". Unexpected document type: %s", name, doc.getDocumentClass());
      }
      if (DocumentUtils.getDocumentType(doc) == DocumentType.DUMMY) {
        logger.debugf(() -> format("Ignoring dummy document \"%s\".", name));
        continue;
      }
      StoredDocument sd = new StoredDocument(doc);
      if (config.isIgnoreDocsWithNaturalisNote() && !sd.getNaturalisNote().isEmpty()) {
        logger.debugf(() -> format("Ignoring document \"%s\". Already annotated by Naturalis plugin", name));
        continue;
      }
      filtered.add(sd);
    }
    return filtered;
  }

}
