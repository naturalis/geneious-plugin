package nl.naturalis.geneious.note;

import java.io.File;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

public class ImportedFromNote implements Note {

  private static DocumentNoteType NOTE_TYPE = DocumentNoteUtilities.getNoteType("importedFrom");

  private final File file;

  public ImportedFromNote(File importedFrom) {
    this.file = importedFrom;
  }

  public void copyTo(DocumentNotes notes) {
    DocumentNote note = notes.getNote(NOTE_TYPE.getCode());
    if (note == null) {
      note = NOTE_TYPE.createDocumentNote();
    }
    note.setFieldValue("path", file.getParentFile().getAbsolutePath());
    note.setFieldValue("filename", file.getName());
    notes.setNote(note);
  }

}
