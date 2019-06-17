package nl.naturalis.geneious.note;

import java.io.File;
import java.nio.file.Paths;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

/**
 * Provides access to the Geneious-native "Imported from" annotation.
 * 
 * @author Ayco Holleman
 *
 */
public class ImportedFromNote implements Note {

  private static DocumentNoteType NOTE_TYPE = DocumentNoteUtilities.getNoteType("importedFrom");

  private final File file;

  /**
   * Creates a new {@code ImportedFromNote} with the provided file as the "imported from" file.
   * 
   * @param importedFrom
   */
  public ImportedFromNote(File importedFrom) {
    this.file = importedFrom;
  }

  /**
   * Creates a new {@code ImportedFromNote} and initializes it with the value found in the provided document.
   * 
   * @param doc
   */
  public ImportedFromNote(AnnotatedPluginDocument doc) {
    DocumentNotes notes = doc.getDocumentNotes(false);
    if (notes == null) {
      file = null;
    } else {
      DocumentNote note = notes.getNote(NOTE_TYPE.getCode());
      if (note == null) {
        this.file = null;
      } else {
        String path = (String) note.getFieldValue("path");
        String name = (String) note.getFieldValue("filename");
        this.file = Paths.get(path, name).toFile();
      }
    }
  }

  /**
   * Saves this note to the provided {@code DocumentNotes}.
   */
  public void copyTo(DocumentNotes notes) {
    DocumentNote note = notes.getNote(NOTE_TYPE.getCode());
    if (note == null) {
      note = NOTE_TYPE.createDocumentNote();
    }
    note.setFieldValue("path", file.getParentFile().getAbsolutePath());
    note.setFieldValue("filename", file.getName());
    notes.setNote(note);
  }

  /**
   * Returns the "imported from" file.
   * 
   * @return
   */
  public File getFile() {
    return file;
  }

}
