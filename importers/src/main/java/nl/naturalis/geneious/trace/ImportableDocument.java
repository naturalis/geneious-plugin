package nl.naturalis.geneious.trace;

import java.util.Optional;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.note.NaturalisNote;
import nl.naturalis.geneious.util.DocumentResultSetInspector;
import nl.naturalis.geneious.util.ImportedDocument;

/**
 * A simple combination of an instance of Geneious's own {@code AnnotatedPluginDocument} class and a{@code SequenceInfo} instance that will
 * be used to provide the Geneious document with Naturalis-specific annotations. The Geneious document has not yet been imported, but is
 * about to be.
 * 
 * @see ImportedDocument
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

  SequenceInfo getSequenceInfo() {
    return sequenceInfo;
  }

  AnnotatedPluginDocument getGeneiousDocument() {
    return document;
  }

  /**
   * Attach the {@link NaturalisNote} contained within this instance to the Geneious document that is also contained within it. Before doing
   * so, it will ask the {@code DocumentResultSetInspector} if the database already contained a document with the same extract ID and type
   * as the importable document.
   * 
   * @param dm
   */
  void annotate(DocumentResultSetInspector dm) {
    NaturalisNote note = sequenceInfo.getNaturalisNote();
    Optional<ImportedDocument> opt = dm.getLatestVersion(note.getExtractId(), sequenceInfo.getDocumentType());
    if (opt.isPresent()) {
      incrementDocumentVersion(opt.get());
    } else {
      dm.getDummy(note.getExtractId()).ifPresent(dummy -> {
        dummy.getNaturalisNote().complete(note);
      });
    }
    note.overwrite(document);
  }

  private void incrementDocumentVersion(ImportedDocument oldDocument) {
    Integer version = oldDocument.getNaturalisNote().getDocumentVersion();
    if (version == null) { // Document must have been imported through Geneious own import facility (not with plugin)
      sequenceInfo.getNaturalisNote().setDocumentVersion(1);
    } else {
      sequenceInfo.getNaturalisNote().setDocumentVersion(version + 1);
    }
  }

}
