package nl.naturalis.geneious.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import nl.naturalis.geneious.util.RuntimeSettings;

public class NoteTypeFactory {

//  public static final NoteTypeFactory INSTANCE = new NoteTypeFactory();
//
//  /*
//   * Whether or not the note type will be regenerated even if it is already registered with
//   * Geneious. In production this should never be the case, because it is wasteful. During
//   * development though (in between Geneious sessions) the definition of the note type may change
//   * and we must inform Geneious about this change. However, even if we want to force Geneious to
//   * update the note type, we still need to do it only once per session, because the note type
//   * definition is hard-coded.
//   */
//  private static boolean regenerated = false;
//
//  private DocumentNoteType noteType;
//  private EnumMap<NaturalisField, DocumentNoteField> fieldCache;
//
//  private NoteTypeFactory() {}
//
//  public synchronized DocumentNoteType getNaturalisSequenceNoteType() {
//    if (this.noteType == null) {
//      DocumentNoteType noteType = DocumentNoteUtilities.getNoteType("naturalis-sequence-note");
//      if (noteType == null) {
//        noteType = DocumentNoteUtilities.createNewNoteType("Naturalis sequence annotation",
//            "naturalis-sequence-note", "Naturalis sequence annotation", createFields(), true);
//      }
//      else if (!regenerated && RuntimeSettings.INSTANCE.regenerateNoteTypes()) {
//        List<DocumentNoteField> fields = noteType.getFields();
//        for (DocumentNoteField field : fields) {
//          noteType.removeField(field.getCode());
//        }
//        fields = createFields();
//        for (DocumentNoteField field : fields) {
//          noteType.setField(field);
//        }
//        regenerated = true;
//      }
//      this.noteType = noteType;
//    }
//    return noteType;
//  }
//
//  public DocumentNoteField getDocumentNoteField(NaturalisField nf) {
//    if (fieldCache == null) {
//      EnumMap<NaturalisField, DocumentNoteField> cache = new EnumMap<>(NaturalisField.class);
//      for (DocumentNoteField dnf : getNaturalisSequenceNoteType().getFields()) {
//        cache.put(naturalisField(dnf), dnf);
//      }
//      this.fieldCache = cache;
//    }
//    return fieldCache.get(nf);
//  }
//
//  private static List<DocumentNoteField> createFields() {
//    List<DocumentNoteField> result = new ArrayList<>(NaturalisField.values().length);
//    for (NaturalisField f : NaturalisField.values()) {
//      result.add(noteField(f));
//    }
//    return result;
//  }
//
//  private static DocumentNoteField noteField(NaturalisField nf) {
//    return DocumentNoteField.createTextNoteField(nf.getName(), nf.getDescription(), nf.getCode(),
//        Collections.emptyList(), false);
//  }
//
//  private static NaturalisField naturalisField(DocumentNoteField dnf) {
//    for (NaturalisField nf : NaturalisField.values()) {
//      if (nf.getCode().equals(dnf.getCode())) {
//        return nf;
//      }
//    }
//    return null;
//  }

}
