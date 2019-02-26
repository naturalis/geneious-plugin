package nl.naturalis.geneious.note;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Objects;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentNote;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.geneious.PluginDataSource;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import static nl.naturalis.geneious.PluginDataSource.AUTO;
import static nl.naturalis.geneious.PluginDataSource.BOLD;
import static nl.naturalis.geneious.PluginDataSource.SAMPLE_SHEET;
import static nl.naturalis.geneious.PluginDataSource.SEQUENCE_NAME;
import static nl.naturalis.geneious.note.NaturalisField.AMPLIFICATION_STAFF;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_BIN_CODE;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_FIELD_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_NUM_IMAGES;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_PROJECT_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_URI;
import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.note.NaturalisField.EXTRACTION_METHOD;
import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.MARKER;
import static nl.naturalis.geneious.note.NaturalisField.PCR_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.PLATE_POSITION;
import static nl.naturalis.geneious.note.NaturalisField.REGISTRATION_NUMBER;
import static nl.naturalis.geneious.note.NaturalisField.REGNO_PLUS_SCI_NAME;
import static nl.naturalis.geneious.note.NaturalisField.SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SCIENTIFIC_NAME;
import static nl.naturalis.geneious.note.NaturalisField.SEQUENCING_STAFF;

/**
 * A container for all data that we enrich Geneious documents with through the various plugins. Different plugins will populate different
 * fields of a NaturalisNote instance. This class contains a method for adding all (and only) non-empty fields to a Geneious document.
 */
public class NaturalisNote {

  private static final EnumMap<PluginDataSource, EnumSet<NaturalisField>> fieldsPerDataSource;

  static {
    fieldsPerDataSource = new EnumMap<>(PluginDataSource.class);
    fieldsPerDataSource.put(AUTO, EnumSet.of(DOCUMENT_VERSION));
    fieldsPerDataSource.put(SEQUENCE_NAME, EnumSet.of(EXTRACT_ID, PCR_PLATE_ID, MARKER));
    fieldsPerDataSource.put(SAMPLE_SHEET, EnumSet.of(EXTRACT_ID, PCR_PLATE_ID, MARKER, EXTRACT_PLATE_ID, SAMPLE_PLATE_ID, PLATE_POSITION,
        SCIENTIFIC_NAME, REGISTRATION_NUMBER, EXTRACTION_METHOD));
    fieldsPerDataSource.put(BOLD, EnumSet.of(BOLD_ID, BOLD_PROJECT_ID, BOLD_FIELD_ID, BOLD_BIN_CODE, BOLD_NUM_IMAGES, BOLD_URI));
  }

  private Integer documentVersion;
  private String pcrPlateId;
  private String marker;
  private String extractPlateId;
  private String extractId;
  private String samplePlateId;
  private String platePosition;
  private String scientificName;
  private String registrationNumber;
  private String extractionMethod;
  private String sequencingStaff;
  private String amplificationStaff;
  private String regnoPlusSciName;

  /**
   * Creates a new empty note.
   */
  public NaturalisNote() {}

  /**
   * Creates a new note and initializes it with the values found in the specified document.
   * 
   * @param doc
   */
  public NaturalisNote(AnnotatedPluginDocument doc) {
    DocumentNotes notes = doc.getDocumentNotes(false);
    DocumentNote note;
    Object value;
    for (NaturalisField nf : NaturalisField.values()) {
      note = notes.getNote(nf.getNoteType().getCode());
      if (note != null && (value = nf.valueIn(note)) != null) {
        setValue(nf, value);
      }
    }
  }

  /**
   * Copies the entire content of this note to the provided document, overwriting any previous values. Fields within this note that have
   * null values willbe ignored though.
   * 
   * @param doc
   */
  public void overwrite(AnnotatedPluginDocument doc) {
    DocumentNotes notes = doc.getDocumentNotes(true);
    boolean modified = overwrite(notes, DOCUMENT_VERSION, documentVersion);
    modified = modified || overwrite(notes, PCR_PLATE_ID, pcrPlateId);
    modified = modified || overwrite(notes, MARKER, marker);
    modified = modified || overwrite(notes, EXTRACT_PLATE_ID, extractPlateId);
    modified = modified || overwrite(notes, EXTRACT_ID, extractId);
    modified = modified || overwrite(notes, SAMPLE_PLATE_ID, samplePlateId);
    modified = modified || overwrite(notes, PLATE_POSITION, platePosition);
    modified = modified || overwrite(notes, SCIENTIFIC_NAME, scientificName);
    modified = modified || overwrite(notes, REGISTRATION_NUMBER, registrationNumber);
    modified = modified || overwrite(notes, EXTRACTION_METHOD, extractionMethod);
    modified = modified || overwrite(notes, SEQUENCING_STAFF, sequencingStaff);
    modified = modified || overwrite(notes, AMPLIFICATION_STAFF, amplificationStaff);
    modified = modified || overwrite(notes, REGNO_PLUS_SCI_NAME, regnoPlusSciName);
    // TODO: CRS & BOLD
    if (modified) {
      notes.saveNotes();
    }
  }

  /**
   * Copies only the content of this note that came from the specified datasource to the provided document, overwriting any previous values.
   * Fields within this note that have null values willbe ignored though.
   * 
   * @param doc
   * @param src
   */
  public void overwrite(AnnotatedPluginDocument doc, PluginDataSource src) {
    DocumentNotes notes = doc.getDocumentNotes(true);
    boolean modified = false;
    for (NaturalisField field : fieldsPerDataSource.get(src)) {
      modified = modified || merge(notes, field, getValue(field));
    }
    if (modified) {
      notes.saveNotes();
    }
  }

  /**
   * Copies all non-empty values within this note to the other note.
   * 
   * @param other
   */
  public void overwrite(NaturalisNote other) {
    if (documentVersion != null) {
      other.documentVersion = documentVersion;
    }
    if (isNotEmpty(pcrPlateId)) {
      other.pcrPlateId = pcrPlateId;
    }
    if (isNotEmpty(marker)) {
      other.marker = marker;
    }
    if (isNotEmpty(extractPlateId)) {
      other.extractPlateId = extractPlateId;
    }
    if (isNotEmpty(extractId)) {
      other.extractId = extractId;
    }
    if (isNotEmpty(samplePlateId)) {
      other.samplePlateId = samplePlateId;
    }
    if (isNotEmpty(platePosition)) {
      other.platePosition = platePosition;
    }
    if (isNotEmpty(scientificName)) {
      other.scientificName = scientificName;
    }
    if (isNotEmpty(registrationNumber)) {
      other.registrationNumber = registrationNumber;
    }
    if (isNotEmpty(extractionMethod)) {
      other.extractionMethod = extractionMethod;
    }
    if (isNotEmpty(sequencingStaff)) {
      other.sequencingStaff = sequencingStaff;
    }
    if (isNotEmpty(amplificationStaff)) {
      other.amplificationStaff = amplificationStaff;
    }
    // TODO: CRS & BOLD
  }

  /**
   * Copies the entire content of this note to the provided document, without overwriting previous values. Fields within this note that have
   * null values willbe ignored though.
   * 
   * @param doc
   */
  public void merge(AnnotatedPluginDocument doc) {
    DocumentNotes notes = doc.getDocumentNotes(true);
    boolean modified = merge(notes, DOCUMENT_VERSION, documentVersion);
    modified = modified || merge(notes, PCR_PLATE_ID, pcrPlateId);
    modified = modified || merge(notes, MARKER, marker);
    modified = modified || merge(notes, EXTRACT_PLATE_ID, extractPlateId);
    modified = modified || merge(notes, EXTRACT_ID, extractId);
    modified = modified || merge(notes, SAMPLE_PLATE_ID, samplePlateId);
    modified = modified || merge(notes, PLATE_POSITION, platePosition);
    modified = modified || merge(notes, SCIENTIFIC_NAME, scientificName);
    modified = modified || merge(notes, REGISTRATION_NUMBER, registrationNumber);
    modified = modified || merge(notes, EXTRACTION_METHOD, extractionMethod);
    modified = modified || merge(notes, SEQUENCING_STAFF, sequencingStaff);
    modified = modified || merge(notes, AMPLIFICATION_STAFF, amplificationStaff);
    modified = modified || merge(notes, REGNO_PLUS_SCI_NAME, regnoPlusSciName);
    // TODO: CRS & BOLD
    if (modified) {
      notes.saveNotes();
    }
  }

  /**
   * Copies only the content of this note that came from the specified datasource to the provided document, without overwriting previous
   * values. Fields within this note that have null values willbe ignored though.
   * 
   * @param doc
   * @param src
   */
  public void merge(AnnotatedPluginDocument doc, PluginDataSource src) {
    DocumentNotes notes = doc.getDocumentNotes(true);
    boolean modified = false;
    for (NaturalisField field : fieldsPerDataSource.get(src)) {
      modified = modified || merge(notes, field, getValue(field));
    }
    notes.saveNotes();
  }

  /**
   * Copies all non-empty values within this note that are empty in the other note to the other note.
   * 
   * @param other
   */
  public void merge(NaturalisNote other) {
    if (documentVersion != null && other.documentVersion == null) {
      other.documentVersion = documentVersion;
    }
    if (isNotEmpty(pcrPlateId) && isEmpty(other.pcrPlateId)) {
      other.pcrPlateId = pcrPlateId;
    }
    if (isNotEmpty(marker) && isEmpty(other.marker)) {
      other.marker = marker;
    }
    if (isNotEmpty(extractPlateId) && isEmpty(other.extractPlateId)) {
      other.extractPlateId = extractPlateId;
    }
    if (isNotEmpty(extractId) && isEmpty(other.extractId)) {
      other.extractId = extractId;
    }
    if (isNotEmpty(samplePlateId) && isEmpty(other.samplePlateId)) {
      other.samplePlateId = samplePlateId;
    }
    if (isNotEmpty(platePosition) && isEmpty(other.platePosition)) {
      other.platePosition = platePosition;
    }
    if (isNotEmpty(scientificName) && isEmpty(other.scientificName)) {
      other.scientificName = scientificName;
    }
    if (isNotEmpty(registrationNumber) && isEmpty(other.registrationNumber)) {
      other.registrationNumber = registrationNumber;
    }
    if (isNotEmpty(extractionMethod) && isEmpty(other.extractionMethod)) {
      other.extractionMethod = extractionMethod;
    }
    if (isNotEmpty(sequencingStaff) && isEmpty(other.sequencingStaff)) {
      other.sequencingStaff = sequencingStaff;
    }
    if (isNotEmpty(amplificationStaff) && isEmpty(other.amplificationStaff)) {
      other.amplificationStaff = amplificationStaff;
    }
    // TODO: CRS & BOLD
  }

  public void setValue(NaturalisField field, Object value) {
    String sval = value.toString();
    switch (field) {
      case AMPLIFICATION_STAFF:
        setAmplificationStaff(sval);
        break;
      case BOLD_BIN_CODE:
        break;
      case BOLD_FIELD_ID:
        break;
      case BOLD_ID:
        break;
      case BOLD_NUM_IMAGES:
        break;
      case BOLD_PROJECT_ID:
        break;
      case BOLD_URI:
        break;
      case DOCUMENT_VERSION:
        setDocumentVersion(Integer.valueOf(sval));
        break;
      case EXTRACTION_METHOD:
        setExtractionMethod(sval);
        break;
      case EXTRACT_ID:
        setExtractId(sval);
        break;
      case EXTRACT_PLATE_ID:
        setExtractPlateId(sval);
        break;
      case MARKER:
        setMarker(sval);
        break;
      case PCR_PLATE_ID:
        setPcrPlateId(sval);
        break;
      case PLATE_POSITION:
        setPlatePosition(sval);
        break;
      case REGISTRATION_NUMBER:
        setRegistrationNumber(sval);
        break;
      case REGNO_PLUS_SCI_NAME:
        setRegnoPlusSciName(sval);
        break;
      case SAMPLE_PLATE_ID:
        setSamplePlateId(sval);
        break;
      case SCIENTIFIC_NAME:
        setScientificName(sval);
        break;
      case SEQUENCING_STAFF:
        setSequencingStaff(sval);
        break;
    }
  }

  public Object getValue(NaturalisField field) {
    switch (field) {
      case AMPLIFICATION_STAFF:
        return getAmplificationStaff();
      case BOLD_BIN_CODE:
        return "TO DO";
      case BOLD_FIELD_ID:
        return "TO DO";
      case BOLD_ID:
        return "TO DO";
      case BOLD_NUM_IMAGES:
        return "TO DO";
      case BOLD_PROJECT_ID:
        return "TO DO";
      case BOLD_URI:
        return "TO DO";
      case DOCUMENT_VERSION:
        return getDocumentVersion();
      case EXTRACTION_METHOD:
        return getExtractionMethod();
      case EXTRACT_ID:
        return getExtractId();
      case EXTRACT_PLATE_ID:
        return getExtractPlateId();
      case MARKER:
        return getMarker();
      case PCR_PLATE_ID:
        return getPcrPlateId();
      case PLATE_POSITION:
        return getPlatePosition();
      case REGISTRATION_NUMBER:
        return getRegistrationNumber();
      case REGNO_PLUS_SCI_NAME:
        return getRegnoPlusSciName();
      case SAMPLE_PLATE_ID:
        return getSamplePlateId();
      case SCIENTIFIC_NAME:
        return getSamplePlateId();
      case SEQUENCING_STAFF:
      default:
        return getSequencingStaff();
    }
  }

  private static boolean overwrite(DocumentNotes notes, NaturalisField field, Object value) {
    if (value != null) {
      DocumentNote note = notes.getNote(field.getNoteType().getCode());
      if (note == null) {
        note = field.getNoteType().createDocumentNote();
        note.setFieldValue(field.getCode(), value);
        notes.setNote(note);
        return true;
      } else if (field.valueIn(note) == null || !field.valueIn(note).equals(value)) {
        note.setFieldValue(field.getCode(), value);
        notes.setNote(note);
        return true;
      }
    }
    return false;
  }

  private static boolean merge(DocumentNotes notes, NaturalisField field, Object value) {
    if (value != null) {
      DocumentNote note = notes.getNote(field.getNoteType().getCode());
      if (note == null) {
        note = field.getNoteType().createDocumentNote();
        note.setFieldValue(field.getCode(), value);
        notes.setNote(note);
        return true;
      } else if (field.valueIn(note) == null) {
        note.setFieldValue(field.getCode(), value);
        notes.setNote(note);
        return true;
      }
    }
    return false;
  }

  public Integer getDocumentVersion() {
    return documentVersion;
  }

  public void setDocumentVersion(Integer documentVersion) {
    this.documentVersion = documentVersion;
  }

  public String getPcrPlateId() {
    return pcrPlateId;
  }

  public void setPcrPlateId(String pcrPlateId) {
    this.pcrPlateId = pcrPlateId;
  }

  public String getMarker() {
    return marker;
  }

  public void setMarker(String marker) {
    this.marker = marker;
  }

  public String getExtractPlateId() {
    return extractPlateId;
  }

  public void setExtractPlateId(String extractPlateId) {
    this.extractPlateId = extractPlateId;
  }

  public String getExtractId() {
    return extractId;
  }

  public void setExtractId(String extractId) {
    this.extractId = extractId;
  }

  public String getSamplePlateId() {
    return samplePlateId;
  }

  public void setSamplePlateId(String samplePlateId) {
    this.samplePlateId = samplePlateId;
  }

  public String getPlatePosition() {
    return platePosition;
  }

  public void setPlatePosition(String platePosition) {
    this.platePosition = platePosition;
  }

  public String getScientificName() {
    return scientificName;
  }

  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  public String getRegistrationNumber() {
    return registrationNumber;
  }

  public void setRegistrationNumber(String registrationNumber) {
    this.registrationNumber = registrationNumber;
  }

  public String getExtractionMethod() {
    return extractionMethod;
  }

  public void setExtractionMethod(String extractionMethod) {
    this.extractionMethod = extractionMethod;
  }

  public String getSequencingStaff() {
    return sequencingStaff;
  }

  public void setSequencingStaff(String sequencingStaff) {
    this.sequencingStaff = sequencingStaff;
  }

  public String getAmplificationStaff() {
    return amplificationStaff;
  }

  public void setAmplificationStaff(String amplificationStaff) {
    this.amplificationStaff = amplificationStaff;
  }

  public String getRegnoPlusSciName() {
    return regnoPlusSciName;
  }

  public void setRegnoPlusSciName(String regnoPlusSciName) {
    this.regnoPlusSciName = regnoPlusSciName;
  }

  public boolean isEmpty() {
    return documentVersion == null
        && StringUtils.isEmpty(pcrPlateId)
        && StringUtils.isEmpty(marker)
        && StringUtils.isEmpty(extractPlateId)
        && StringUtils.isEmpty(extractId)
        && StringUtils.isEmpty(samplePlateId)
        && StringUtils.isEmpty(platePosition)
        && StringUtils.isEmpty(scientificName)
        && StringUtils.isEmpty(registrationNumber)
        && StringUtils.isEmpty(extractionMethod)
        && StringUtils.isEmpty(sequencingStaff)
        && StringUtils.isEmpty(amplificationStaff)
        && StringUtils.isEmpty(regnoPlusSciName);
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != NaturalisNote.class) {
      return false;
    }
    NaturalisNote other = (NaturalisNote) obj;
    return Objects.equals(documentVersion, other.documentVersion)
        && Objects.equals(pcrPlateId, other.pcrPlateId)
        && Objects.equals(marker, other.marker)
        && Objects.equals(extractPlateId, other.extractPlateId)
        && Objects.equals(extractId, other.extractId)
        && Objects.equals(samplePlateId, other.samplePlateId)
        && Objects.equals(platePosition, other.platePosition)
        && Objects.equals(scientificName, other.scientificName)
        && Objects.equals(registrationNumber, other.registrationNumber)
        && Objects.equals(extractionMethod, other.extractionMethod)
        && Objects.equals(sequencingStaff, other.sequencingStaff)
        && Objects.equals(amplificationStaff, other.amplificationStaff)
        && Objects.equals(regnoPlusSciName, other.regnoPlusSciName);
  }

  public int hashCode() {
    return Objects.hash(documentVersion, pcrPlateId, marker, extractPlateId, extractPlateId, samplePlateId, platePosition, scientificName,
        registrationNumber, extractionMethod, sequencingStaff, amplificationStaff, regnoPlusSciName);
  }

}
