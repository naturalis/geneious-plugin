package nl.naturalis.geneious.note;

import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createTextNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.createNewNoteType;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.setNoteType;
import static java.util.Collections.emptyList;
import java.util.List;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import edu.emory.mathcs.backport.java.util.Arrays;
import nl.naturalis.geneious.util.RuntimeSettings;

public enum NaturalisField {

  DOCUMENT_VERSION("DocumentVersionCode_Seq", "Document version"),
  PCR_PLATE_ID("PCRplateIDCode_Seq", "PCR plate ID (Seq)"),
  MARKER("MarkerCode_Seq", "Marker (Seq)"),
  EXTRACT_PLATE_ID("ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)"),
  EXTRACT_ID("ExtractIDCode_Samples", "Extract ID (Samples)"),
  SAMPLE_PLATE_ID("ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)"),
  PLATE_POSITION("PlatePositionCode_Samples", "Position (Samples)"),
  SCIENTIFIC_NAME("TaxonName2Code_Samples", "[Scientific name] (Samples)"),
  REGISTRATION_NUMBER("RegistrationNumberCode_Samples", "Registr-nmbr (Samples)"),
  EXTRACTION_METHOD("SampleMethodCode_Samples", "Extraction method (Samples)"),
  SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Samples", "Seq-staff (Samples)"),
  AMPLIFICATION_STAFF("AmplicificationStaffCode_FixedValue_Samples", "Ampl-staff (Samples)"),
  REGNO_PLUS_SCI_NAME(
      "RegistrationNumberCode_TaxonName2Code_Samples",
      "Registr-nmbr_[Scientific_name] (Samples)"),
  /*
   * CRS fields:
   */
  
  /*
   * BOLD fields:
   */
  BOLD_ID("BOLDIDCode_Bold", "BOLD ID (Bold)"),
  BOLD_PROJECT_ID("BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)"),
  BOLD_FIELD_ID("FieldIDCode_Bold", "Field ID (Bold)"),
  BOLD_BIN_CODE("BOLDBINCode_Bold", "BOLD BIN (Bold)"),
  BOLD_NUM_IMAGES("NumberOfImagesCode_Bold", "N images (Bold)"),
  BOLD_URI("BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)");

  private static final String NOTE_TYPE_CODE_PREFIX = "DocumentNoteUtilities-";

  private final String code;
  private final String name;
  private final DocumentNoteType noteType;

  private NaturalisField(String code, String name) {
    this.code = code;
    this.name = name;
    this.noteType = myNoteType(name, code);
  }

  public Object getValue(AnnotatedPluginDocument doc) {
    String noteTypeCode = noteType.getCode();
    DocumentNotes notes = doc.getDocumentNotes(false);
    DocumentNote note = notes.getNote(noteTypeCode);
    return note == null ? null : note.getFieldValue(code);
  }

  public String getName() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public DocumentNoteType getNoteType() {
    return noteType;
  }

  public DocumentField createQueryField() {
    return DocumentField.createStringField("", "", noteType.getCode() + "." + code);
  }

  DocumentNote newNote(Object value) {
    DocumentNote note = noteType.createDocumentNote();
    note.setFieldValue(code, value);
    return note;
  }

  /*
   * N.B. The way note types and note fields are named and created here is odd and awkward, but it
   * is a legacy from the V1 plugins that we cannot change, because query logic depends on it. For
   * each field a separate note type is created. The name of the note type is the same as the name
   * of the (single) field within that note type. The code of the note type is NOT the same as the
   * code of the field. It is the same as the field name but prefixed with "DocumentNoteUtilities-".
   * The description of the note type and field is basically non-sensical, but we leave it as it
   * was.
   */
  private static DocumentNoteType myNoteType(String fieldName, String fieldCode) {
    String noteTypeCode = NOTE_TYPE_CODE_PREFIX + fieldName;
    DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteTypeCode);
    if (noteType == null) {
      String noteTypeName = fieldName;
      String noteTypeDescr = "Naturalis file " + fieldName + " note";
      String fieldDescr = noteTypeDescr;
      DocumentNoteField noteField =
          createTextNoteField(fieldName, fieldDescr, fieldCode, emptyList(), false);
      List<DocumentNoteField> noteFields = Arrays.asList(new DocumentNoteField[] {noteField});
      noteType = createNewNoteType(noteTypeName, noteTypeCode, noteTypeDescr, noteFields, true);
      setNoteType(noteType);
    } else if (RuntimeSettings.INSTANCE.regenerateNoteTypes()) {
      /*
       * Whether or not the note type must be regenerated even if it is already registered with
       * Geneious. In production this should never be the case, because it is wasteful. During
       * development though (in between Geneious sessions) the definition of the note type may
       * change and we must inform Geneious about this change.
       */
      List<DocumentNoteField> fields = noteType.getFields();
      for (DocumentNoteField field : fields) {
        noteType.removeField(field.getCode());
      }
      String fieldDescr = fieldCode;
      DocumentNoteField noteField =
          createTextNoteField(fieldCode, fieldDescr, fieldCode, emptyList(), false);
      noteType.setField(noteField);
      setNoteType(noteType);
    }
    return noteType;
  }

}
