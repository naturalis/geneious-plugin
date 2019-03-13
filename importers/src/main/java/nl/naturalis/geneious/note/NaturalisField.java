package nl.naturalis.geneious.note;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

import nl.naturalis.geneious.PluginDataSource;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;
import nl.naturalis.geneious.util.RuntimeSettings;

import static java.util.Collections.emptyList;

import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createBooleanNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteField.createTextNoteField;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.createNewNoteType;
import static com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities.setNoteType;

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
  SMPL_SEQUENCING_STAFF("SequencingStaffCode_FixedValue_Samples", "Seq-staff (Samples)", SAMPLE_SHEET), // ???????? Defined in V1 but never actually used to annotate a document

  CRS_ALTITUDE("HeightCode_CRS", "Altitude (CRS)", Double.class, CRS), // HeightCode_CRS
  CRS_CLASS("ClassCode_CRS", "Class (CRS)", CRS), // ClassCode_CRS
  CRS_COLLECTOR("CollectorCode_CRS", "Leg (CRS)", CRS), // CollectorCode_CRS
  CRS_COUNTRY("CountryCode_CRS", "Country (CRS)", CRS), // CountryCode_CRS
  CRS_DATE("CollectingDateCode_CRS", "Date (CRS)", LocalDateTime.class, CRS), // CollectingDateCode_CRS
  CRS_FAMILY("FamilyCode_CRS", "Family (CRS)", CRS), // FamilyCode_CRS
  CRS_FLAG("CRSCode_CRS", "CRS (CRS)", Boolean.class, CRS), // CRSCode_CRS
  CRS_GENUS("GenusCode_CRS", "Genus (CRS)", CRS), // GenusCode_CRS
  CRS_IDENTIFIER("IdentifierCode_CRS", "Identifier (CRS)", CRS), // IdentifierCode_CRS
  CRS_LATITUDE("LatitudeDecimalCode_CRS", "Lat (CRS)", Double.class, CRS), // LatitudeDecimalCode_CRS
  CRS_LOCALITY("LocalityCode_CRS", "Locality (CRS)", CRS), // LocalityCode_CRS
  CRS_LONGITUDE("LongitudeDecimalCode_CRS", "Long (CRS)", Double.class, CRS), // LongitudeDecimalCode_CRS
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
   * Returns all data sources (sequence name, sample sheet, CRS, BOLD) containg the field. The first of these data sources is the primaru
   * data source, i.e. the one actually used to populate the field. However, there may be other data sources that also contain this field.
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
      queryField = DocumentField.createStringField("", "", getNoteType().getCode() + "." + code);
    }
    return queryField;
  }

  DocumentNoteType getNoteType() {
    if (noteType == null) {
      noteType = myNoteType();
    }
    return noteType;
  }

  /**
   * Returns the value this field has in the provided note (most likely extracted from a stored Geneious document).
   * 
   * @param note
   * @return
   */
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
       * the case, but during development though (in between Geneious sessions) the definition of a note type may change and we must inform
       * Geneious about this change.
       */
      guiLogger.warn("Regenerating note type definition of \"%s\". This should not happen in production!", code);
      List<DocumentNoteField> fields = noteType.getFields();
      for (DocumentNoteField field : fields) { // In V1 plugin there's always just 1 field per note type, but let's iterate anyhow ...
        noteType.removeField(field.getCode());
      }
      String fieldDescr = name + " (Naturalis)";
      DocumentNoteField noteField;
      if (type == Boolean.class) {
        noteField = createBooleanNoteField(name, fieldDescr, code, false);
      } else { // TODO: Create appropriate type of field (V1 legacy is to create a text field in all other cases)
        noteField = createTextNoteField(name, fieldDescr, code, emptyList(), false);
      }
      noteType.setField(noteField);
      setNoteType(noteType);
    }
    return noteType;
  }

}
