package nl.naturalis.geneious.note;

import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.annotations.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static java.util.Collections.emptyList;

import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createBooleanNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createDecimalNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createEnumeratedNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createIntegerNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createTextNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.createNewNoteType;

/**
 * Symbolic constants for all fields that can be included in a {@link NaturalisNote}.
 */
public enum NaturalisField {

  DOCUMENT_VERSION("DocumentVersionCode_Seq", "Document version"),

  SEQ_EXTRACT_ID("ExtractIDCode_Seq", "Extract ID (Seq)"),
  SEQ_MARKER("MarkerCode_Seq", "Marker (Seq)"),
  SEQ_PASS("ConsensusSeqPassCode_Seq", "Pass (Seq)", SeqPass.class),
  SEQ_PCR_PLATE_ID("PCRplateIDCode_Seq", "PCR plate ID (Seq)"),
  SEQ_SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)"),

  SMPL_AMPLIFICATION_STAFF("AmplicificationStaffCode_FixedValue_Samples", "Ampl-staff (Samples)"),
  SMPL_EXTRACT_ID("ExtractIDCode_Samples", "Extract ID (Samples)"),
  SMPL_EXTRACT_PLATE_ID("ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)"),
  SMPL_EXTRACTION_METHOD("SampleMethodCode_Samples", "Extraction method (Samples)"),
  SMPL_PLATE_POSITION("PlatePositionCode_Samples", "Position (Samples)"),
  SMPL_REGNO_PLUS_SCI_NAME("RegistrationNumberCode_TaxonName2Code_Samples", "Registr-nmbr_[Scientific_name] (Samples)"),
  SMPL_REGISTRATION_NUMBER("RegistrationNumberCode_Samples", "Registr-nmbr (Samples)"),
  SMPL_SAMPLE_PLATE_ID("ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)"),
  SMPL_SCIENTIFIC_NAME("TaxonName2Code_Samples", "[Scientific name] (Samples)"),
  SMPL_SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Samples", "Seq-staff (Samples)"),

  CRS_ALTITUDE("HeightCode_CRS", "Altitude (CRS)"),
  CRS_CLASS("ClassCode_CRS", "Class (CRS)"),
  CRS_COLLECTOR("CollectorCode_CRS", "Leg (CRS)"),
  CRS_COUNTRY("CountryCode_CRS", "Country (CRS)"),
  CRS_DATE("CollectingDateCode_CRS", "Date (CRS)"),
  CRS_FAMILY("FamilyCode_CRS", "Family (CRS)"),
  CRS_CRS("CRSCode_CRS", "CRS (CRS)", Boolean.class),
  CRS_GENUS("GenusCode_CRS", "Genus (CRS)"),
  CRS_IDENTIFIER("IdentifierCode_CRS", "Identifier (CRS)"),
  CRS_LATITUDE("LatitudeDecimalCode_CRS", "Lat (CRS)"),
  CRS_LOCALITY("LocalityCode_CRS", "Locality (CRS)"),
  CRS_LONGITUDE("LongitudeDecimalCode_CRS", "Long (CRS)"),
  CRS_ORDER("OrderCode_CRS", "Order (CRS)"),
  CRS_PHYLUM("PhylumCode_CRS", "Phylum (CRS)"),
  CRS_REGION("StateOrProvinceBioRegionCode_CRS", "Region (CRS)"),
  CRS_SCIENTIFIC_NAME("TaxonName1Code_CRS", "Scientific name (CRS)"),
  CRS_SEX("SexCode_CRS", "Sex (CRS)"),
  CRS_STAGE("PhaseOrStageCode_CRS", "Stage (CRS)"),
  CRS_SUBFAMILY("SubFamilyCode_CRS", "Subfamily (CRS)"),

  BOLD_BIN_CODE("BOLDBINCode_Bold", "BOLD BIN (Bold)"),
  BOLD_FIELD_ID("FieldIDCode_Bold", "Field ID (Bold)"),
  BOLD_GEN_BANK_ID("GenBankIDCode_Bold", "GenBank ID (Bold)"),
  BOLD_GEN_BANK_URI("GenBankURICode_FixedValue_Bold", "GenBank URI (Bold)"),
  BOLD_ID("BOLDIDCode_Bold", "BOLD ID (Bold)"),
  BOLD_NUCLEOTIDE_LENGTH("NucleotideLengthCode_Bold", "Nucl-length (Bold)"),
  BOLD_NUM_IMAGES("NumberOfImagesCode_Bold", "N images (Bold)"),
  BOLD_NUM_TRACES("TraceFilePresenceCode_Bold", "N traces (Bold)"),
  BOLD_PROJECT_ID("BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)"),
  BOLD_URI("BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)");

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NaturalisField.class);

  private static final String NOTE_TYPE_CODE_PREFIX = "DocumentNoteUtilities-";
  // V1 legacy: descriptions are so illogical & non-descript we might as well do without them
  private static final String NO_DESCRIPTION = StringUtils.EMPTY;

  @VisibleForTesting
  final String code;
  @VisibleForTesting
  final String name;

  private final String noteTypeCode;
  private final String noteTypeName;
  private final Class<?> dataType;

  private DocumentNoteType noteType;
  private DocumentField queryField;

  private NaturalisField(String code, String name) {
    this(code, name, String.class);
  }

  private NaturalisField(String code, String name, Class<?> dateType) {
    this.code = code;
    this.name = name;
    /*
     * V1 legacy: the name of the note type is the sanme as the name of the one and only field it contains, and the code of
     * the note type is also derived from the name (NOT the code) of the field.
     */
    this.noteTypeCode = NOTE_TYPE_CODE_PREFIX + name;
    this.noteTypeName = name;
    /*
     * V1 legacy, except for the CRS_FLAG field, all fields are defined as string fields, including for example
     * DOCUMENT_VERSION, which could better have been defined as an integer field.
     */
    this.dataType = dateType;
  }

  /**
   * Returns the name of the field (which will appear as the header in the GUI.
   * 
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a Geneious object that should you should use if you want to use this field in a query.
   * 
   * @return
   */
  public DocumentField createQueryField() {
    // Why a DocumentField should be created this way is mysterious. Just got it from Geneious support.
    if (queryField == null) {
      if (dataType == Boolean.class) {
        queryField = DocumentField.createBooleanField(name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
      } else if (dataType == Integer.class) {
        queryField = DocumentField.createIntegerField(name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
      } else if (dataType == Double.class) {
        queryField = DocumentField.createDoubleField(name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
      } else if (dataType.isEnum()) {
        String[] values = Arrays.stream(dataType.getEnumConstants()).map(Object::toString).toArray(String[]::new);
        queryField = DocumentField.createEnumeratedField(values, name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
      } else {
        queryField = DocumentField.createStringField(name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
      }
    }
    return queryField;
  }

  public void saveOrUpdateNoteType() {
    DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteTypeCode);
    if (noteType != null) {
      guiLogger.info("Deleting old definition of field \"%s\"", name);
      List<DocumentNoteField> fields = noteType.getFields();
      for (DocumentNoteField field : fields) { // In V1 there's always just 1 field per note type, but let's iterate anyhow ...
        noteType.removeField(field.getCode());
      }
    }
    saveNoteType();
    // Make sure the query fields also gets re-generated the first time it is requested.
    this.queryField = null;
  }

  /**
   * Parses the provided string value (supposedly coming from a file-to-be-imported) into an object of this field's
   * datatype. Throws an {@code IllegalArgumentException} if the string could not be parsed as such.
   * 
   * @param str
   * @return
   */
  @SuppressWarnings("unchecked")
  <T> T parse(String str) {
    T t;
    if (dataType == Boolean.class) {
      t = (T) Boolean.valueOf(str);
    } else if (dataType == Integer.class) {
      t = (T) Integer.valueOf(str);
    } else if (dataType == Double.class) {
      t = (T) Double.valueOf(str);
    } else if (dataType.isEnum()) {
      t = (T) new EnumParser(dataType).parse(str);
    } else {
      t = (T) str;
    }
    return t;
  }

  /**
   * Casts the provided object (presumably already processed by a {@link NoteFactory} to an object of this field's
   * datatype. Since the note factory probably already returned the right datatype, this is just an extra type check. If
   * the casting throws a {@code ClassCastException}, an error is logged and this method returns null.
   * 
   * @param val
   * @return
   */
  @SuppressWarnings("unchecked")
  <T> T cast(Object val) {
    if (val != null) {
      try {
        return (T) dataType.cast(val);
      } catch (ClassCastException e) {
        guiLogger.error("Cannot cast %s (%s) to %s for field %s", val, val.getClass(), dataType, this);
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  <T> T readFrom(DocumentNotes notes) {
    DocumentNote note = notes.getNote(getNoteType().getCode());
    if (note == null) {
      return null;
    }
    Object val = note.getFieldValue(code);
    if (val == null) {
      return null;
    }
    if (dataType.isEnum()) {
      // So-called "Enumerated fields" are actually just strings in Geneious
      return (T) new EnumParser(dataType).parse(val.toString());
    }
    return cast(val);
  }

  void parseAndwrite(DocumentNotes notes, String value) {
    DocumentNote note = notes.getNote(getNoteType().getCode());
    if (note == null) {
      if (value != null) {
        note = getNoteType().createDocumentNote();
        // So-called "Enumerated fields" are actually just strings in Geneious
        Object val = dataType.isEnum() ? parse(value).toString() : parse(value);
        note.setFieldValue(code, val);
        notes.setNote(note);
      }
    } else if (value == null) {
      notes.removeNote(getNoteType().getCode());
    } else {
      Object val = dataType.isEnum() ? parse(value).toString() : parse(value);
      note.setFieldValue(code, val);
      notes.setNote(note);
    }
  }

  void castAndWrite(DocumentNotes notes, Object value) {
    DocumentNote note = notes.getNote(getNoteType().getCode());
    if (note == null) {
      if (value != null) {
        note = getNoteType().createDocumentNote();
        Object val = dataType.isEnum() ? cast(value).toString() : cast(value);
        note.setFieldValue(code, val);
        notes.setNote(note);
      }
    } else if (value == null) {
      notes.removeNote(getNoteType().getCode());
    } else {
      Object val = dataType.isEnum() ? cast(value).toString() : cast(value);
      note.setFieldValue(code, val);
      notes.setNote(note);
    }
  }

  private DocumentNoteType getNoteType() {
    noteType = DocumentNoteUtilities.getNoteType(noteTypeCode);
    if (noteType == null) {
      saveOrUpdateNoteType();
    }
    return noteType;
  }

  private void saveNoteType() {
    DocumentNoteField noteField;
    if (dataType == Boolean.class) {
      noteField = createBooleanNoteField(name, NO_DESCRIPTION, code, false);
    } else if (dataType == Integer.class) {
      noteField = createIntegerNoteField(name, NO_DESCRIPTION, code, emptyList(), false);
    } else if (dataType == Double.class) {
      noteField = createDecimalNoteField(name, NO_DESCRIPTION, code, emptyList(), false);
    } else if (dataType.isEnum()) {
      String[] values = Arrays.stream(dataType.getEnumConstants()).map(Object::toString).toArray(String[]::new);
      noteField = createEnumeratedNoteField(values, name, NO_DESCRIPTION, code, false);
    } else {
      noteField = createTextNoteField(name, NO_DESCRIPTION, code, emptyList(), false);
    }
    List<DocumentNoteField> noteFields = Arrays.asList(noteField);
    noteType = createNewNoteType(noteTypeName, noteTypeCode, NO_DESCRIPTION, noteFields, true);
    guiLogger.info("Saving definition of field \"%s\"", name);
    DocumentNoteUtilities.setNoteType(noteType);
  }

  @JsonValue
  public String toString() {
    return name;
  }

}
