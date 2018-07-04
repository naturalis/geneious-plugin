package nl.naturalis.geneious.notes;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import static nl.naturalis.geneious.notes.NaturalisFieldType.*;

public class NaturalisNote {

  private String extractId;
  private String plateId;
  private String marker;

  public NaturalisNote() {}

  public void saveToDocument(AnnotatedPluginDocument doc) {
    DocumentNoteType noteType = NoteTypeFactory.INSTANCE.getNaturalisSequenceNoteType();
    DocumentNote note = noteType.createDocumentNote();
    note.setFieldValue(EXTRACT_ID.getCode(), extractId);
    note.setFieldValue(PLATE_ID.getCode(), plateId);
    note.setFieldValue(MARKER.getCode(), marker);
    AnnotatedPluginDocument.DocumentNotes notes = doc.getDocumentNotes(true);
    notes.setNote(note);
    notes.saveNotes();
  }

  public void setExtractId(String extractId) {
    this.extractId = extractId;
  }

  public void setPlateId(String plateId) {
    this.plateId = plateId;
  }

  public void setMarker(String marker) {
    this.marker = marker;
  }

}
