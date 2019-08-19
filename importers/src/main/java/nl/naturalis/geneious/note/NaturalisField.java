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

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.csv.NoteFactory;
import nl.naturalis.geneious.log.GuiLogManager;
import nl.naturalis.geneious.log.GuiLogger;

import static java.util.Collections.emptyList;

import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createBooleanNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createDecimalNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createEnumeratedNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createIntegerNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createTextNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.createNewNoteType;

/**
 * Symbolic constants for all fields that can be included in a {@link NaturalisNote}. Each field represents a single
 * annotation in a Geneious document, which again corresponds to a single column in the GUI's document table.
 */
public enum NaturalisField {

  /**
   * Corresponds to the DocumentVersionCode_Seq column in the GUI's document table. 
   */
  DOCUMENT_VERSION("DocumentVersionCode_Seq", "Document version"),

  /**
   * Corresponds to the ExtractIDCode_Seq column in the GUI's document table. 
   */
  SEQ_EXTRACT_ID("ExtractIDCode_Seq", "Extract ID (Seq)"),
  /**
   * Corresponds to the MarkerCode_Seq column in the GUI's document table. 
   */
  SEQ_MARKER("MarkerCode_Seq", "Marker (Seq)"),
  /**
   * Corresponds to the ConsensusSeqPassCode_Seq column in the GUI's document table. 
   */
  SEQ_PASS("ConsensusSeqPassCode_Seq", "Pass (Seq)", SeqPass.class),
  /**
   * Corresponds to the PCRplateIDCode_Seq column in the GUI's document table. 
   */
  SEQ_PCR_PLATE_ID("PCRplateIDCode_Seq", "PCR plate ID (Seq)"),
  /**
   * Corresponds to the SequencingStaffCode_FixedValue_Seq column in the GUI's document table. 
   */
  SEQ_SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)"),

  /**
   * Corresponds to the AmplicificationStaffCode_FixedValue_Samples column in the GUI's document table. 
   */
  SMPL_AMPLIFICATION_STAFF("AmplicificationStaffCode_FixedValue_Samples", "Ampl-staff (Samples)"),
  /**
   * Corresponds to the ExtractIDCode_Samples column in the GUI's document table. 
   */
  SMPL_EXTRACT_ID("ExtractIDCode_Samples", "Extract ID (Samples)"),
  /**
   * Corresponds to the ExtractPlateNumberCode_Samples column in the GUI's document table. 
   */
  SMPL_EXTRACT_PLATE_ID("ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)"),
  /**
   * Corresponds to the SampleMethodCode_Samples column in the GUI's document table. 
   */
  SMPL_EXTRACTION_METHOD("SampleMethodCode_Samples", "Extraction method (Samples)"),
  /**
   * Corresponds to the PlatePositionCode_Samples column in the GUI's document table. 
   */
  SMPL_PLATE_POSITION("PlatePositionCode_Samples", "Position (Samples)"),
  /**
   * Corresponds to the RegistrationNumberCode_TaxonName2Code_Samples column in the GUI's document table. 
   */
  SMPL_REGNO_PLUS_SCI_NAME("RegistrationNumberCode_TaxonName2Code_Samples", "Registr-nmbr_[Scientific_name] (Samples)"),
  /**
   * Corresponds to the RegistrationNumberCode_Samples column in the GUI's document table. 
   */
  SMPL_REGISTRATION_NUMBER("RegistrationNumberCode_Samples", "Registr-nmbr (Samples)"),
  /**
   * Corresponds to the ProjectPlateNumberCode_Samples column in the GUI's document table. 
   */
  SMPL_SAMPLE_PLATE_ID("ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)"),
  /**
   * Corresponds to the TaxonName2Code_Samples column in the GUI's document table. 
   */
  SMPL_SCIENTIFIC_NAME("TaxonName2Code_Samples", "[Scientific name] (Samples)"),
  /**
   * Corresponds to the SequencingStaffCode_FixedValue_Samples column in the GUI's document table. 
   */
  SMPL_SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Samples", "Seq-staff (Samples)"),

  /**
   * Corresponds to the HeightCode_CRS column in the GUI's document table. 
   */
  CRS_ALTITUDE("HeightCode_CRS", "Altitude (CRS)"),
  /**
   * Corresponds to the ClassCode_CRS column in the GUI's document table. 
   */
  CRS_CLASS("ClassCode_CRS", "Class (CRS)"),
  /**
   * Corresponds to the CollectorCode_CRS column in the GUI's document table. 
   */
  CRS_COLLECTOR("CollectorCode_CRS", "Leg (CRS)"),
  /**
   * Corresponds to the CountryCode_CRS column in the GUI's document table. 
   */
  CRS_COUNTRY("CountryCode_CRS", "Country (CRS)"),
  /**
   * Corresponds to the CollectingDateCode_CRS column in the GUI's document table. 
   */
  CRS_DATE("CollectingDateCode_CRS", "Date (CRS)"),
  /**
   * Corresponds to the FamilyCode_CRS column in the GUI's document table. 
   */
  CRS_FAMILY("FamilyCode_CRS", "Family (CRS)"),
  /**
   * Corresponds to the CRSCode_CRS column in the GUI's document table. 
   */
  CRS_CRS("CRSCode_CRS", "CRS (CRS)", Boolean.class),
  /**
   * Corresponds to the GenusCode_CRS column in the GUI's document table. 
   */
  CRS_GENUS("GenusCode_CRS", "Genus (CRS)"),
  /**
   * Corresponds to the IdentifierCode_CRS column in the GUI's document table. 
   */
  CRS_IDENTIFIER("IdentifierCode_CRS", "Identifier (CRS)"),
  /**
   * Corresponds to the LatitudeDecimalCode_CRS column in the GUI's document table. 
   */
  CRS_LATITUDE("LatitudeDecimalCode_CRS", "Lat (CRS)"),
  /**
   * Corresponds to the LocalityCode_CRS column in the GUI's document table. 
   */
  CRS_LOCALITY("LocalityCode_CRS", "Locality (CRS)"),
  /**
   * Corresponds to the LongitudeDecimalCode_CRS column in the GUI's document table. 
   */
  CRS_LONGITUDE("LongitudeDecimalCode_CRS", "Long (CRS)"),
  /**
   * Corresponds to the OrderCode_CRS column in the GUI's document table. 
   */
  CRS_ORDER("OrderCode_CRS", "Order (CRS)"),
  /**
   * Corresponds to the PhylumCode_CRS column in the GUI's document table. 
   */
  CRS_PHYLUM("PhylumCode_CRS", "Phylum (CRS)"),
  /**
   * Corresponds to the StateOrProvinceBioRegionCode_CRS column in the GUI's document table. 
   */
  CRS_REGION("StateOrProvinceBioRegionCode_CRS", "Region (CRS)"),
  /**
   * Corresponds to the TaxonName1Code_CRS column in the GUI's document table. 
   */
  CRS_SCIENTIFIC_NAME("TaxonName1Code_CRS", "Scientific name (CRS)"),
  /**
   * Corresponds to the SexCode_CRS column in the GUI's document table. 
   */
  CRS_SEX("SexCode_CRS", "Sex (CRS)"),
  /**
   * Corresponds to the PhaseOrStageCode_CRS column in the GUI's document table. 
   */
  CRS_STAGE("PhaseOrStageCode_CRS", "Stage (CRS)"),
  /**
   * Corresponds to the SubFamilyCode_CRS column in the GUI's document table. 
   */
  CRS_SUBFAMILY("SubFamilyCode_CRS", "Subfamily (CRS)"),

  /**
   * Corresponds to the BOLDBINCode_Bold column in the GUI's document table. 
   */
  BOLD_BIN_CODE("BOLDBINCode_Bold", "BOLD BIN (Bold)"),
  /**
   * Corresponds to the FieldIDCode_Bold column in the GUI's document table. 
   */
  BOLD_FIELD_ID("FieldIDCode_Bold", "Field ID (Bold)"),
  /**
   * Corresponds to the GenBankIDCode_Bold column in the GUI's document table. 
   */
  BOLD_GEN_BANK_ID("GenBankIDCode_Bold", "GenBank ID (Bold)"),
  /**
   * Corresponds to the GenBankURICode_FixedValue_Bold column in the GUI's document table. 
   */
  BOLD_GEN_BANK_URI("GenBankURICode_FixedValue_Bold", "GenBank URI (Bold)"),
  /**
   * Corresponds to the BOLDIDCode_Bold column in the GUI's document table. 
   */
  BOLD_ID("BOLDIDCode_Bold", "BOLD ID (Bold)"),
  /**
   * Corresponds to the NucleotideLengthCode_Bold column in the GUI's document table. 
   */
  BOLD_NUCLEOTIDE_LENGTH("NucleotideLengthCode_Bold", "Nucl-length (Bold)"),
  /**
   * Corresponds to the NumberOfImagesCode_Bold column in the GUI's document table. 
   */
  BOLD_NUM_IMAGES("NumberOfImagesCode_Bold", "N images (Bold)"),
  /**
   * Corresponds to the TraceFilePresenceCode_Bold column in the GUI's document table. 
   */
  BOLD_NUM_TRACES("TraceFilePresenceCode_Bold", "N traces (Bold)"),
  /**
   * Corresponds to the BOLDprojIDCode_Bold column in the GUI's document table. 
   */
  BOLD_PROJECT_ID("BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)"),
  /**
   * Corresponds to the BOLDURICode_FixedValue_Bold column in the GUI's document table. 
   */
  BOLD_URI("BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)");

  private static final GuiLogger logger = GuiLogManager.getLogger(NaturalisField.class);

  private static final String NOTE_TYPE_CODE_PREFIX = "DocumentNoteUtilities-";
  // V1 legacy: descriptions are so illogical & non-descript we might as well do without them
  private static final String NO_DESCRIPTION = StringUtils.EMPTY;

  private final String code;
  private final String name;

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

  /**
   * Updates and saves the definition (e&#34;g&#34; its datatype) of this field.
   */
  public void saveOrUpdateNoteType() {
    DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteTypeCode);
    if (noteType != null) {
      logger.info("Deleting old definition of field \"%s\"", name);
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
        logger.error("Cannot cast %s (%s) to %s for field %s", val, val.getClass(), dataType, this);
      }
    }
    return null;
  }

  /**
   * Reads the value of this field from the provided {@code DocumentNotes}.
   * 
   * @param notes
   * @return
   */
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

  /**
   * Writes the provided value to the provided {@code DocumentNotes}, parsing it into the proper datatype.
   * 
   * @param notes
   * @param value
   */
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

  /**
   * Writes the provided value to the provided {@code DocumentNotes}, casting it to the proper datatype.
   * 
   * @param notes
   * @param value
   */
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
    logger.info("Saving definition of field \"%s\"", name);
    DocumentNoteUtilities.setNoteType(noteType);
  }

  @JsonValue
  public String toString() {
    return name;
  }

}
