package nl.naturalis.geneious.seq;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.StoredDocument;

/**
 * A simple combination of an {@link AnnotatedPluginDocument} and a {@code SequenceInfo} object that will be used to
 * supply the annotations for it. The Geneious document has not yet been saved to the database, but is about to be.
 * 
 * @see StoredDocument
 */
class ImportableDocument {

  @SuppressWarnings("unused")
  private static final GuiLogger guiLogger = GuiLogManager.getLogger(ImportableDocument.class);

  private final SequenceInfo sequenceInfo;
  private final AnnotatedPluginDocument document;

  ImportableDocument(AnnotatedPluginDocument doc, SequenceInfo info) {
    this.sequenceInfo = info;
    this.document = doc;
  }

  /**
   * Returns the {@code SequenceInfo} object containing the annotations for the Geneious document.
   * 
   * @return
   */
  SequenceInfo getSequenceInfo() {
    return sequenceInfo;
  }

  /**
   * Returns the Geneious document.
   * 
   * @return
   */
  AnnotatedPluginDocument getGeneiousDocument() {
    return document;
  }

  /**
   * Attaches the {@link NaturalisNote} to the Geneious document, and then saves the document to the database.
   */
  void saveAnnotations() {
    sequenceInfo.getNaturalisNote().saveTo(document);
  }

}
