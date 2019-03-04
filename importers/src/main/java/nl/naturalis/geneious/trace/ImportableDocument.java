package nl.naturalis.geneious.trace;

import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.DocumentResultSetInspector;
import nl.naturalis.geneious.util.ImportedDocument;

import static nl.naturalis.geneious.gui.log.GuiLogger.format;

/**
 * A simple combination of an {@link AnnotatedPluginDocument} class and a {@code SequenceInfo} object that will be used to supply the
 * annotations for it. The Geneious document has not yet been imported, but is about to be.
 * 
 * @see ImportedDocument
 */
class ImportableDocument {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(ImportableDocument.class);

  private final SequenceInfo sequenceInfo;
  private final AnnotatedPluginDocument document;

  ImportableDocument(AnnotatedPluginDocument doc, SequenceInfo info) {
    this.sequenceInfo = info;
    this.document = doc;
  }

  SequenceInfo getSequenceInfo() {
    return sequenceInfo;
  }

  AnnotatedPluginDocument getGeneiousDocument() {
    return document;
  }

  /**
   * Attaches the {@link NaturalisNote} within the {@code SequenceInfo} object to the {@code AnnotatedPluginDocument}. The provided
   * {@code DocumentResultSetInspector} will be used to look up a document (dummy or "real") with the same extract ID. If found, that
   * document's annotations will be merged into the {@code NaturalisNote}.
   * 
   * @param inspector
   */
  void annotate(DocumentResultSetInspector inspector) {
    guiLogger.debugf(() -> format("Annotating \"%s\"", sequenceInfo.getName()));
    NaturalisNote note = sequenceInfo.getNaturalisNote();
    Optional<ImportedDocument> opt = inspector.findLatestVersion(note.getExtractId(), sequenceInfo.getDocumentType());
    if (opt.isPresent()) {
      opt.get().getNaturalisNote().complete(note);
      note.setDocumentVersion(note.getDocumentVersion() + 1);
    } else {
      sequenceInfo.getNaturalisNote().setDocumentVersion(1);
      inspector.findDummy(note.getExtractId()).ifPresent(dummy -> dummy.getNaturalisNote().complete(note));
    }
    note.overwrite(document);
  }

}
