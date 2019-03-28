package nl.naturalis.geneious.seq;

import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.DocumentType;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.QueryResultManager;
import nl.naturalis.geneious.util.StoredDocument;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * A simple combination of an {@link AnnotatedPluginDocument} and a {@code SequenceInfo} object that will be used to
 * supply the annotations for it. The Geneious document has not yet been saved to the database, but is about to be.
 * 
 * @see StoredDocument
 */
class ImportableDocument {

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
   * Attaches the {@link NaturalisNote} to the Geneious document. The provided {@code QueryResultManager} is used to look
   * up older documents with the same extract ID (dummy or real). In case the QueryResultManager yields a regular
   * (AB1/fasta) document, its document version is used to determine the document version of this document. If the
   * QueryResultManager yields a dummy document, its annotations will be copied over to this document AND the dummy
   * document will be pased on (returned) to the caller, who may then proceed to delete the dummy. Otherwise an empty
   * {@code Optional} is returned to the caller.
   * 
   * @param queryResultManager
   * @return
   */
  Optional<StoredDocument> annotate(QueryResultManager queryResultManager) {
    DocumentType type = sequenceInfo.getDocumentType();
    guiLogger.debugf(() -> format("Splitting \"%s\"", sequenceInfo.getName()));
    NaturalisNote note = sequenceInfo.getNaturalisNote();
    String extractId = note.getExtractId();
    guiLogger.debugf(() -> format("Searching query cache for %s document(s) with extract ID %s", type, extractId));
    Optional<StoredDocument> optional = queryResultManager.find(extractId, type);
    if (optional.isPresent()) {
      String version = optional.get().getNaturalisNote().getDocumentVersion();
      guiLogger.debugf(() -> format("Found. Document version: %s", version));
      note.incrementDocumentVersion(version);
      note.saveTo(document);
      return Optional.empty();
    }
    guiLogger.debugf(() -> format("Not found. Searching query cache for dummy document with extract ID %s", extractId));
    optional = queryResultManager.findDummy(extractId);
    if (optional.isPresent()) {
      guiLogger.debug(() -> "Found. Copying annotations from dummy document");
      optional.get().getNaturalisNote().copyTo(note, false);
    } else {
      guiLogger.debug(() -> "Not found");
    }
    guiLogger.debugf(() -> format("Saving %s document", type));
    note.setDocumentVersion(1);
    note.saveTo(document);
    return optional;
  }

}
