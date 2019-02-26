package nl.naturalis.geneious.note;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

import nl.naturalis.geneious.PluginDataSource;
import nl.naturalis.geneious.util.RuntimeSettings;

import static java.util.Collections.emptyList;

import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createTextNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.createNewNoteType;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.setNoteType;

import static nl.naturalis.geneious.PluginDataSource.AUTO;
import static nl.naturalis.geneious.PluginDataSource.BOLD;
import static nl.naturalis.geneious.PluginDataSource.SAMPLE_SHEET;
import static nl.naturalis.geneious.PluginDataSource.SEQUENCE_NAME;

/**
 * An enumeration of all fields that can be included in a {@link NaturalisNote}.
 */
public enum NaturalisField {

  // Auto-generated
  DOCUMENT_VERSION("DocumentVersionCode_Seq", "Document version", Integer.class, AUTO),

  // Provided by sequence name or sample sheet
  EXTRACT_ID("ExtractIDCode_Samples", "Extract ID (Samples)", SEQUENCE_NAME, SAMPLE_SHEET),
  PCR_PLATE_ID("PCRplateIDCode_Seq", "PCR plate ID (Seq)", SEQUENCE_NAME, SAMPLE_SHEET),
  MARKER("MarkerCode_Seq", "Marker (Seq)", SEQUENCE_NAME, SAMPLE_SHEET),

  // Provided by sample sheet
  EXTRACT_PLATE_ID("ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)", SAMPLE_SHEET),
  SAMPLE_PLATE_ID("ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)", SAMPLE_SHEET),
  PLATE_POSITION("PlatePositionCode_Samples", "Position (Samples)", SAMPLE_SHEET),
  SCIENTIFIC_NAME("TaxonName2Code_Samples", "[Scientific name] (Samples)", SAMPLE_SHEET),
  REGISTRATION_NUMBER("RegistrationNumberCode_Samples", "Registr-nmbr (Samples)", SAMPLE_SHEET),
  EXTRACTION_METHOD("SampleMethodCode_Samples", "Extraction method (Samples)", SAMPLE_SHEET),

  // Don't know yet, probably AUTO
  SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Samples", "Seq-staff (Samples)"),
  AMPLIFICATION_STAFF("AmplicificationStaffCode_FixedValue_Samples", "Ampl-staff (Samples)"),
  REGNO_PLUS_SCI_NAME("RegistrationNumberCode_TaxonName2Code_Samples", "Registr-nmbr_[Scientific_name] (Samples)"),

  // Provided by CRS

  // Provided by BOLD
  BOLD_ID("BOLDIDCode_Bold", "BOLD ID (Bold)", BOLD),
  BOLD_PROJECT_ID("BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)", BOLD),
  BOLD_FIELD_ID("FieldIDCode_Bold", "Field ID (Bold)", BOLD),
  BOLD_BIN_CODE("BOLDBINCode_Bold", "BOLD BIN (Bold)", BOLD),
  BOLD_NUM_IMAGES("NumberOfImagesCode_Bold", "N images (Bold)", BOLD),
  BOLD_URI("BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)", BOLD);

  private static final String NOTE_TYPE_CODE_PREFIX = "DocumentNoteUtilities-";

  private final String code;
  private final String name;
  private final Class<?> type;
  private final PluginDataSource[] dataSources;

  private DocumentNoteType noteType;
  private DocumentField queryField;

  private NaturalisField(String code, String name, PluginDataSource... dataSources) {
    this(code, name, String.class, dataSources);
  }

  private NaturalisField(String code, String name, Class<?> type, PluginDataSource... dataSources) {
    this.code = code;
    this.name = name;
    this.type = type;
    this.dataSources = dataSources;
  }

  /**
   * Returns the value of this field within the specified document.
   * 
   * @param doc
   * @return
   */
  public Object getValue(AnnotatedPluginDocument doc) {
    String noteTypeCode = getNoteType().getCode();
    DocumentNotes notes = doc.getDocumentNotes(false);
    DocumentNote note = notes.getNote(noteTypeCode);
    return note == null ? null : note.getFieldValue(code);
  }

  /**
   * Returns the code for this field.
   * 
   * @return
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns the name of this field.
   * 
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the data type of this field.
   * 
   * @return
   */
  public Class<?> getDataType() {
    return type;
  }

  /**
   * Returns all data sources from this field can possibly be populated. Each field usually has just one data source. However, because trace
   * files and sample sheets can be imported in arbitrary order, some fields may be populated from either of these data sources. data
   * sources. Note though that we only list the data sources that are actually used by the plugin to set the value of this field.
   */
  public PluginDataSource[] getDataSources() {
    return dataSources;
  }

  /**
   * Returns a Geneious object that should you should use if you want to use this field in a query.
   * 
   * @return
   */
  public DocumentField createQueryField() {
    if (queryField == null) {
      queryField = DocumentField.createStringField("", "", noteType.getCode() + "." + code);
    }
    return queryField;
  }

  DocumentNoteType getNoteType() {
    if (noteType == null) {
      noteType = myNoteType();
    }
    return noteType;
  }

  DocumentNote newNote(Object value) {
    DocumentNote note = getNoteType().createDocumentNote();
    note.setFieldValue(code, value);
    return note;
  }

  Object valueIn(DocumentNote note) {
    return note.getFieldValue(getCode());
  }

  /*
   * N.B. The way note types and note fields are named and created here is odd and awkward, but it is a legacy from the V1 plugins that we
   * cannot change. Query logic depends on it. For each field a separate note type is created. The name of the note type is the same as the
   * name of the (single) field within that note type. The code of the note type is NOT the same as the code of the field. It is the same as
   * the field name but prefixed with "DocumentNoteUtilities-". The description of the note type and field is basically non-sensical, but we
   * leave it as it was.
   */
  private DocumentNoteType myNoteType() {
    String noteTypeCode = NOTE_TYPE_CODE_PREFIX + name;
    DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteTypeCode);
    if (noteType == null) {
      String noteTypeName = name;
      String fieldDescr = name + " (Naturalis)";
      String noteTypeDescr = name + " (Naturalis note)";
      DocumentNoteField noteField = createTextNoteField(name, fieldDescr, code, emptyList(), false);
      List<DocumentNoteField> noteFields = Arrays.asList(noteField);
      noteType = createNewNoteType(noteTypeName, noteTypeCode, noteTypeDescr, noteFields, true);
      setNoteType(noteType);
    } else if (RuntimeSettings.INSTANCE.regenerateNoteTypes()) {
      /*
       * Whether or not the note type must be regenerated even if it is already registered with Geneious. In production this should never be
       * the case, because it is wasteful. During development though (in between Geneious sessions) the definition of the note type may
       * change and we must inform Geneious about this change.
       */
      List<DocumentNoteField> fields = noteType.getFields();
      for (DocumentNoteField field : fields) {
        noteType.removeField(field.getCode());
      }
      String fieldDescr = name + " (Naturalis)";
      DocumentNoteField noteField = createTextNoteField(name, fieldDescr, code, emptyList(), false);
      noteType.setField(noteField);
      setNoteType(noteType);
    }
    return noteType;
  }

}
