package nl.naturalis.geneious.note;

import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.google.common.annotations.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.PluginDataSource;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static java.util.Collections.emptyList;

import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createBooleanNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createDecimalNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createIntegerNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createTextNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.createNewNoteType;

import static nl.naturalis.geneious.PluginDataSource.AUTO;
import static nl.naturalis.geneious.PluginDataSource.BOLD;
import static nl.naturalis.geneious.PluginDataSource.CRS;
import static nl.naturalis.geneious.PluginDataSource.SAMPLE_SHEET;
import static nl.naturalis.geneious.PluginDataSource.SEQUENCE_NAME;

/**
 * Symbolic constants for all fields that can be included in a {@link NaturalisNote}.
 */
public enum NaturalisField {

  DOCUMENT_VERSION("DocumentVersionCode_Seq", "Document version", AUTO), // DocumentVersionCode_Seq

  SEQ_EXTRACT_ID("ExtractIDCode_Seq", "Extract ID (Seq)", SEQUENCE_NAME), // ExtractIDCode_Seq
  SEQ_MARKER("MarkerCode_Seq", "Marker (Seq)", SEQUENCE_NAME), // MarkerCode_Seq
  SEQ_PASS("ConsensusSeqPassCode_Seq", "Pass (Seq)", SEQUENCE_NAME), // ConsensusSeqPassCode_Seq
  SEQ_PCR_PLATE_ID("PCRplateIDCode_Seq", "PCR plate ID (Seq)", SEQUENCE_NAME), // PCRplateIDCode_Seq
  SEQ_SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)", SEQUENCE_NAME), // SequencingStaffCode_FixedValue_Seq

  SMPL_AMPLIFICATION_STAFF("AmplicificationStaffCode_FixedValue_Samples", "Ampl-staff (Samples)", SAMPLE_SHEET), // AmplicificationStaffCode_FixedValue_Samples
  SMPL_EXTRACT_ID("ExtractIDCode_Samples", "Extract ID (Samples)", SAMPLE_SHEET), // ExtractIDCode_Samples
  SMPL_EXTRACT_PLATE_ID("ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)", SAMPLE_SHEET), // ExtractPlateNumberCode_Samples
  SMPL_EXTRACTION_METHOD("SampleMethodCode_Samples", "Extraction method (Samples)", SAMPLE_SHEET), // SampleMethodCode_Samples
  SMPL_PLATE_POSITION("PlatePositionCode_Samples", "Position (Samples)", SAMPLE_SHEET), // PlatePositionCode_Samples
  SMPL_REGNO_PLUS_SCI_NAME("RegistrationNumberCode_TaxonName2Code_Samples", "Registr-nmbr_[Scientific_name] (Samples)", SAMPLE_SHEET), // RegistrationNumberCode_TaxonName2Code_Samples
  SMPL_REGISTRATION_NUMBER("RegistrationNumberCode_Samples", "Registr-nmbr (Samples)", SAMPLE_SHEET), // RegistrationNumberCode_Samples
  SMPL_SAMPLE_PLATE_ID("ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)", SAMPLE_SHEET), // ProjectPlateNumberCode_Samples
  SMPL_SCIENTIFIC_NAME("TaxonName2Code_Samples", "[Scientific name] (Samples)", SAMPLE_SHEET), // TaxonName2Code_Samples
  SMPL_SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Samples", "Seq-staff (Samples)", SAMPLE_SHEET), // ???????? Defined in V1 but never
                                                                                                        // actually used to annotate a
                                                                                                        // document

  CRS_ALTITUDE("HeightCode_CRS", "Altitude (CRS)", CRS), // HeightCode_CRS
  CRS_CLASS("ClassCode_CRS", "Class (CRS)", CRS), // ClassCode_CRS
  CRS_COLLECTOR("CollectorCode_CRS", "Leg (CRS)", CRS), // CollectorCode_CRS
  CRS_COUNTRY("CountryCode_CRS", "Country (CRS)", CRS), // CountryCode_CRS
  CRS_DATE("CollectingDateCode_CRS", "Date (CRS)", CRS), // CollectingDateCode_CRS
  CRS_FAMILY("FamilyCode_CRS", "Family (CRS)", CRS), // FamilyCode_CRS
  CRS_FLAG("CRSCode_CRS", "CRS (CRS)", Boolean.class, CRS), // CRSCode_CRS
  CRS_GENUS("GenusCode_CRS", "Genus (CRS)", CRS), // GenusCode_CRS
  CRS_IDENTIFIER("IdentifierCode_CRS", "Identifier (CRS)", CRS), // IdentifierCode_CRS
  CRS_LATITUDE("LatitudeDecimalCode_CRS", "Lat (CRS)", CRS), // LatitudeDecimalCode_CRS
  CRS_LOCALITY("LocalityCode_CRS", "Locality (CRS)", CRS), // LocalityCode_CRS
  CRS_LONGITUDE("LongitudeDecimalCode_CRS", "Long (CRS)", CRS), // LongitudeDecimalCode_CRS
  CRS_ORDER("OrderCode_CRS", "Order (CRS)", CRS), // OrderCode_CRS
  CRS_PHYLUM("PhylumCode_CRS", "Phylum (CRS)", CRS), // PhylumCode_CRS
  CRS_REGION("StateOrProvinceBioRegionCode_CRS", "Region (CRS)", CRS), // StateOrProvinceBioRegionCode_CRS
  CRS_SCIENTIFIC_NAME("TaxonName1Code_CRS", "Scientific name (CRS)", CRS), // TaxonName1Code_CRS
  CRS_SEX("SexCode_CRS", "Sex (CRS)", CRS), // SexCode_CRS
  CRS_STAGE("PhaseOrStageCode_CRS", "Stage (CRS)", CRS), // PhaseOrStageCode_CRS
  CRS_SUBFAMILY("SubFamilyCode_CRS", "Subfamily (CRS)", CRS), // SubFamilyCode_CRS

  BOLD_BIN_CODE("BOLDBINCode_Bold", "BOLD BIN (Bold)", BOLD), // BOLDBINCode_Bold
  BOLD_FIELD_ID("FieldIDCode_Bold", "Field ID (Bold)", BOLD), // FieldIDCode_Bold
  BOLD_GEN_BANK_ID("GenBankIDCode_Bold", "GenBank ID (Bold)", BOLD), // GenBankIDCode_Bold
  BOLD_GEN_BANK_URI("GenBankURICode_FixedValue_Bold", "GenBank URI (Bold)", BOLD), // GenBankURICode_FixedValue_Bold
  BOLD_ID("BOLDIDCode_Bold", "BOLD ID (Bold)", BOLD), // BOLDIDCode_Bold
  BOLD_NUCLEOTIDE_LENGTH("NucleotideLengthCode_Bold", "Nucl-length (Bold)", BOLD), // NucleotideLengthCode_Bold
  BOLD_NUM_IMAGES("NumberOfImagesCode_Bold", "N images (Bold)", BOLD), // NumberOfImagesCode_Bold
  BOLD_NUM_TRACES("TraceFilePresenceCode_Bold", "N traces (Bold)", BOLD), // TraceFilePresenceCode_Bold
  BOLD_PROJECT_ID("BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)", BOLD), // BOLDprojIDCode_Bold
  BOLD_URI("BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)", BOLD); // BOLDURICode_FixedValue_Bold

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
  private final PluginDataSource[] dataSources;

  private DocumentNoteType noteType;
  private DocumentField queryField;

  private NaturalisField(String code, String name, PluginDataSource... dataSources) {
    this(code, name, String.class, dataSources);
  }

  private NaturalisField(String code, String name, Class<?> dateType, PluginDataSource... dataSources) {
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
    this.dataSources = dataSources;
  }

  public String getName() {
    return name;
  }

  /**
   * Returns all data sources (sequence name, sample sheet, CRS, BOLD) containg the field. The first of these data sources
   * is the primaru data source, i.e. the one actually used to populate the field. However, there may be other data
   * sources that also contain this field.
   */
  public PluginDataSource[] getDataSources() {
    return dataSources;
  }

  @SuppressWarnings("unchecked")
  public <T> T parse(String str) {
    T t;
    if (dataType == Boolean.class) {
      t = (T) Boolean.valueOf(str);
    } else if (dataType == Integer.class) {
      t = (T) Integer.valueOf(str);
    } else if (dataType == Double.class) {
      t = (T) Double.valueOf(str);
    } else {
      t = (T) str;
    }
    return t;
  }

  @SuppressWarnings("unchecked")
  public <T> T cast(Object val) {
    try {
      return (T) dataType.cast(val);
    } catch (ClassCastException e) {
      guiLogger.error("Field: %s; Value: %s; MyType: %s; ValType: %s", name(), val, dataType, val.getClass());
      return null;
    }
  }

  public <T> T readFrom(AnnotatedPluginDocument document) {
    return readFrom(document.getDocumentNotes(false));
  }

  @SuppressWarnings("unused")
  public <T> T readFrom(DocumentNotes notes) {
    DocumentNote note = notes.getNote(getNoteType().getCode());
    if (note == null) {
      return null;
    }
    Object val = note.getFieldValue(code);
    return val == null ? null : cast(val);
  }

  public void parseAndwrite(DocumentNotes notes, String value) {
    DocumentNote note = notes.getNote(getNoteType().getCode());
    if (note == null) {
      if (value != null) {
        note = getNoteType().createDocumentNote();
        note.setFieldValue(code, parse(value));
        notes.setNote(note);
      }
    } else if (value == null) {
      notes.removeNote(getNoteType().getCode());
    } else {
      note.setFieldValue(code, parse(value));
      notes.setNote(note);
    }
  }

  public void castAndWrite(DocumentNotes notes, Object value) {
    DocumentNote note = notes.getNote(getNoteType().getCode());
    if (note == null) {
      if (value != null) {
        note = getNoteType().createDocumentNote();
        note.setFieldValue(code, cast(value));
        notes.setNote(note);
      }
    } else if (value == null) {
      notes.removeNote(getNoteType().getCode());
    } else {
      note.setFieldValue(code, cast(value));
      notes.setNote(note);
    }
  }

  /**
   * Returns a Geneious object that should you should use if you want to use this field in a query.
   * 
   * @return
   */
  public DocumentField createQueryField() {
    /*
     * Why a DocumentField can and should be created from a DocumentNoteField as shown below is not clear. Just got it from
     * Geneious support.
     */
    if (queryField == null) {
      if (dataType == Boolean.class) {
        queryField = DocumentField.createBooleanField(name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
      } else if (dataType == Integer.class) {
        queryField = DocumentField.createIntegerField(name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
      } else if (dataType == Double.class) {
        queryField = DocumentField.createDoubleField(name, NO_DESCRIPTION, noteTypeCode + "." + code, true, true);
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
    } else {
      noteField = createTextNoteField(name, NO_DESCRIPTION, code, emptyList(), false);
    }
    List<DocumentNoteField> noteFields = Arrays.asList(noteField);
    noteType = createNewNoteType(noteTypeName, noteTypeCode, NO_DESCRIPTION, noteFields, true);
    guiLogger.info("Saving definition of field \"%s\"", name);
    DocumentNoteUtilities.setNoteType(noteType);
  }

}
