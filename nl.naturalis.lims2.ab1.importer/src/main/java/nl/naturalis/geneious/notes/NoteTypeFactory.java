package nl.naturalis.geneious.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

public class NoteTypeFactory {

  public static final NoteTypeFactory INSTANCE = new NoteTypeFactory();

  /*
   * Whether or not the note type will be regenerated even if it is already registered with
   * Geneious. In production this should never be the case, because it is wasteful. During
   * development though (in between Geneious sessions) the definition of the note type may change
   * and we must inform Geneious about this change. However, even if we want to force Geneious to
   * update the note type, we still need to do it only once per session, because the note type
   * definition is hard-coded.
   */
  private static boolean regenerated = false;

  private DocumentNoteType naturalisSequenceNoteType;

  private NoteTypeFactory() {}

  public synchronized DocumentNoteType getNaturalisSequenceNoteType() {
    if (naturalisSequenceNoteType == null) {
      DocumentNoteType noteType = DocumentNoteUtilities.getNoteType("naturalis-sequence-note");
      if (noteType == null) {
        noteType = DocumentNoteUtilities.createNewNoteType("Naturalis sequence annotation",
            "naturalis-sequence-note", "Naturalis sequence annotation",
            createFields(), true);
      } else if (!regenerated && RuntimeSettings.INSTANCE.regenerateNoteTypes()) {
        List<DocumentNoteField> fields = noteType.getFields();
        for (DocumentNoteField field : fields) {
          noteType.removeField(field.getCode());
        }
        fields = createFields();
        for (DocumentNoteField field : fields) {
          noteType.setField(field);
        }
        regenerated = true;
      }
      naturalisSequenceNoteType = noteType;
    }
    return naturalisSequenceNoteType;
  }

  private static List<DocumentNoteField> createFields() {
    List<DocumentNoteField> result = new ArrayList<>(NaturalisField.values().length);
    for (NaturalisField f : NaturalisField.values()) {
      result.add(noteField(f));
    }
    return result;
  }

  private static DocumentNoteField noteField(NaturalisField field) {
    return DocumentNoteField.createTextNoteField(field.getName(), field.getDescription(),
        field.getCode(), Collections.emptyList(), false);
  }

}
