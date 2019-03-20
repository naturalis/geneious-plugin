package nl.naturalis.geneious.trace;

import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.DocumentResultSetInspector;
import nl.naturalis.geneious.util.StoredDocument;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;
import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_EXTRACT_ID;

/**
 * A simple combination of an {@link AnnotatedPluginDocument} class and a {@code SequenceInfo} object that will be used to supply the
 * annotations for it. The Geneious document has not yet been imported, but is about to be.
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
   * Attaches the {@link NaturalisNote} to the Geneious document. The provided {@code DocumentResultSetInspector} is used to look up a
   * previous version of the Geneious document (dummy or "real"). If found, the {@code NaturalisNote} acquire the annotations from that
   * document before being attached. If there are multiple previous versions, the most recent one will be chosen for its annotations.
   * 
   * @param inspector
   * @return An {@code ImportedDocument} that contains the version immediately preceding the document inside this
   *         {@code ImportableDocument}, or null if this {@code ImportableDocument} contains a new Geneious document (based on the extract
   *         ID).
   */
  StoredDocument annotate(DocumentResultSetInspector inspector) {
    guiLogger.debugf(() -> format("Annotating \"%s\"", sequenceInfo.getName()));
    NaturalisNote note = sequenceInfo.getNaturalisNote();
    String extractID = note.get(SEQ_EXTRACT_ID);
    Optional<StoredDocument> opt = inspector.find(extractID, sequenceInfo.getDocumentType());
    StoredDocument previous = null;
    if (opt.isPresent()) {
      previous = opt.get();
      note.setDocumentVersion(DOCUMENT_VERSION.readFrom(previous.getGeneiousDocument()));
      note.incrementDocumentVersion(0);
    } else {
      note.setDocumentVersion(0);
      opt = inspector.findDummy(extractID);
      if (opt.isPresent()) {
        previous = opt.get();
        note.readFrom(previous.getGeneiousDocument());
      }
    }
    note.saveTo(document);
    return previous;
  }

}
