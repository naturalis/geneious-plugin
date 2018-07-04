package nl.naturalis.geneious.notes;

import static nl.naturalis.geneious.notes.NaturalisFieldType.EXTRACT_ID;
import static nl.naturalis.geneious.notes.NaturalisFieldType.MARKER;
import static nl.naturalis.geneious.notes.NaturalisFieldType.PLATE_ID;
import java.util.Collections;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import edu.emory.mathcs.backport.java.util.Arrays;
import nl.naturalis.geneious.util.RuntimeSettings;

public class NoteTypeFactory {

  public static final NoteTypeFactory INSTANCE = new NoteTypeFactory();

  /*
   * Whether or not the note type will be regenerated even if it already known to Geneious. During
   * development (in between Geneious sessions) the definition of the note type may change and we
   * must inform Geneious about this change. However, even if we want to force Geneious to update
   * the note type, we still need to do it only once per session, because the changes are made in
   * the Java code.
   */
  private static boolean regenerated = false;

  private DocumentNoteType naturalisSequenceNoteType;

  private NoteTypeFactory() {}

  public DocumentNoteType getNaturalisSequenceNoteType() {
    if (naturalisSequenceNoteType == null) {
      DocumentNoteType noteType = DocumentNoteUtilities.getNoteType("naturalis-sequence-note");
      if (noteType == null) {
        noteType = DocumentNoteUtilities.createNewNoteType("Naturalis sequence annotation",
            "naturalis-sequence-note", "Naturalis sequence annotation",
            createNaturalisSequenceNoteFields(), true);
      } else if (!regenerated && RuntimeSettings.INSTANCE.regenerateNoteTypes()) {
        List<DocumentNoteField> fields = noteType.getFields();
        for (DocumentNoteField field : fields) {
          noteType.removeField(field.getCode());
        }
        fields = createNaturalisSequenceNoteFields();
        for (DocumentNoteField field : fields) {
          noteType.setField(field);
        }
        regenerated = true;
      }
      naturalisSequenceNoteType = noteType;
    }
    return naturalisSequenceNoteType;
  }

  private static List<DocumentNoteField> createNaturalisSequenceNoteFields() {
    DocumentNoteField extractID = noteField("Extract ID", "Extract ID", EXTRACT_ID.getCode());
    DocumentNoteField plateID = noteField("PCR Plate ID", "PCR Plate ID", PLATE_ID.getCode());
    DocumentNoteField marker = noteField("Marker", "Marker", MARKER.getCode());
    return Arrays.asList(new DocumentNoteField[] {extractID, plateID, marker});
  }

  private static DocumentNoteField noteField(String name, String descr, String code) {
    return DocumentNoteField.createTextNoteField(name, descr, code, Collections.emptyList(), false);
  }

}
