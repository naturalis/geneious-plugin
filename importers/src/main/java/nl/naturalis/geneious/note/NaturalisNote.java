package nl.naturalis.geneious.note;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Objects;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentNote;

import nl.naturalis.geneious.PluginDataSource;
import nl.naturalis.geneious.gui.log.GuiLogManager;
import nl.naturalis.geneious.gui.log.GuiLogger;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import static nl.naturalis.geneious.PluginDataSource.AUTO;
import static nl.naturalis.geneious.PluginDataSource.BOLD;
import static nl.naturalis.geneious.PluginDataSource.SAMPLE_SHEET;
import static nl.naturalis.geneious.PluginDataSource.SEQUENCE_NAME;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_AMPLIFICATION_STAFF;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_BIN_CODE;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_FIELD_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_NUM_IMAGES;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_PROJECT_ID;
import static nl.naturalis.geneious.note.NaturalisField.BOLD_URI;
import static nl.naturalis.geneious.note.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACTION_METHOD;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_MARKER;
import static nl.naturalis.geneious.note.NaturalisField.SEQ_PCR_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_PLATE_POSITION;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGISTRATION_NUMBER;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_REGNO_PLUS_SCI_NAME;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SCIENTIFIC_NAME;
import static nl.naturalis.geneious.note.NaturalisField.SMPL_SEQUENCING_STAFF;

import static nl.naturalis.geneious.gui.log.GuiLogger.*;

/**
 * A container for all data that we enrich Geneious documents with through the various plugin actions. Different actions will populate
 * different fields of a NaturalisNote instance.
 */
public class NaturalisNote {

  private static final GuiLogger guiLogger = GuiLogManager.getLogger(NaturalisNote.class);

  private static final EnumMap<PluginDataSource, EnumSet<NaturalisField>> fieldsPerDataSource;

  static {
    fieldsPerDataSource = new EnumMap<>(PluginDataSource.class);
    fieldsPerDataSource.put(AUTO, EnumSet.of(DOCUMENT_VERSION));
    fieldsPerDataSource.put(SEQUENCE_NAME, EnumSet.of(SMPL_EXTRACT_ID, SEQ_PCR_PLATE_ID, SEQ_MARKER));
    fieldsPerDataSource.put(SAMPLE_SHEET, EnumSet.of(SMPL_EXTRACT_ID, SEQ_PCR_PLATE_ID, SEQ_MARKER, SMPL_EXTRACT_PLATE_ID, SMPL_SAMPLE_PLATE_ID, SMPL_PLATE_POSITION,
        SMPL_SCIENTIFIC_NAME, SMPL_REGISTRATION_NUMBER, SMPL_EXTRACTION_METHOD));
    fieldsPerDataSource.put(BOLD, EnumSet.of(BOLD_ID, BOLD_PROJECT_ID, BOLD_FIELD_ID, BOLD_BIN_CODE, BOLD_NUM_IMAGES, BOLD_URI));
  }

  private String documentVersion;
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
   * Copies the entire contents of this note to the provided document, overwriting any previous values. Empty fields within this note will
   * not be copied. In other words this method will never set fields to null in the provided document.
   * 
   * @param doc
   */
  public void overwrite(AnnotatedPluginDocument doc) {
    DocumentNotes notes = doc.getDocumentNotes(true);
    overwrite(notes, DOCUMENT_VERSION, documentVersion);
    overwrite(notes, SEQ_PCR_PLATE_ID, pcrPlateId);
    overwrite(notes, SEQ_MARKER, marker);
    overwrite(notes, SMPL_EXTRACT_PLATE_ID, extractPlateId);
    overwrite(notes, SMPL_EXTRACT_ID, extractId);
    overwrite(notes, SMPL_SAMPLE_PLATE_ID, samplePlateId);
    overwrite(notes, SMPL_PLATE_POSITION, platePosition);
    overwrite(notes, SMPL_SCIENTIFIC_NAME, scientificName);
    overwrite(notes, SMPL_REGISTRATION_NUMBER, registrationNumber);
    overwrite(notes, SMPL_EXTRACTION_METHOD, extractionMethod);
    overwrite(notes, SMPL_SEQUENCING_STAFF, sequencingStaff);
    overwrite(notes, SMPL_AMPLIFICATION_STAFF, amplificationStaff);
    overwrite(notes, SMPL_REGNO_PLUS_SCI_NAME, regnoPlusSciName);
    // TODO: CRS & BOLD
    notes.saveNotes();
  }

  /**
   * Copies all fields within this note that do not have a value yet in the provided document to that document.Empty fields within this note
   * will not be copied. In other words this method will never set fields to null in the provided document.
   * 
   * @param doc
   */
  public void complete(AnnotatedPluginDocument doc) {
    DocumentNotes notes = doc.getDocumentNotes(true);
    merge(notes, DOCUMENT_VERSION, documentVersion);
    merge(notes, SEQ_PCR_PLATE_ID, pcrPlateId);
    merge(notes, SEQ_MARKER, marker);
    merge(notes, SMPL_EXTRACT_PLATE_ID, extractPlateId);
    merge(notes, SMPL_EXTRACT_ID, extractId);
    merge(notes, SMPL_SAMPLE_PLATE_ID, samplePlateId);
    merge(notes, SMPL_PLATE_POSITION, platePosition);
    merge(notes, SMPL_SCIENTIFIC_NAME, scientificName);
    merge(notes, SMPL_REGISTRATION_NUMBER, registrationNumber);
    merge(notes, SMPL_EXTRACTION_METHOD, extractionMethod);
    merge(notes, SMPL_SEQUENCING_STAFF, sequencingStaff);
    merge(notes, SMPL_AMPLIFICATION_STAFF, amplificationStaff);
    merge(notes, SMPL_REGNO_PLUS_SCI_NAME, regnoPlusSciName);
    // TODO: CRS & BOLD
    notes.saveNotes();
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
   * Copies all non-empty values within this note that are empty in the other note to the other note.
   * 
   * @param other
   */
  public void complete(NaturalisNote other) {
    if (documentVersion != null && other.documentVersion == null) {
      copying("documentVersion", documentVersion);
      other.documentVersion = documentVersion;
    }
    if (isNotEmpty(pcrPlateId) && isEmpty(other.pcrPlateId)) {
      copying("pcrPlateId", pcrPlateId);
      other.pcrPlateId = pcrPlateId;
    }
    if (isNotEmpty(marker) && isEmpty(other.marker)) {
      copying("marker", marker);
      other.marker = marker;
    }
    if (isNotEmpty(extractPlateId) && isEmpty(other.extractPlateId)) {
      copying("extractPlateId", extractPlateId);
      other.extractPlateId = extractPlateId;
    }
    if (isNotEmpty(extractId) && isEmpty(other.extractId)) {
      copying("extractId", extractId);
      other.extractId = extractId;
    }
    if (isNotEmpty(samplePlateId) && isEmpty(other.samplePlateId)) {
      copying("samplePlateId", samplePlateId);
      other.samplePlateId = samplePlateId;
    }
    if (isNotEmpty(platePosition) && isEmpty(other.platePosition)) {
      copying("platePosition", platePosition);
      other.platePosition = platePosition;
    }
    if (isNotEmpty(scientificName) && isEmpty(other.scientificName)) {
      copying("scientificName", scientificName);
      other.scientificName = scientificName;
    }
    if (isNotEmpty(registrationNumber) && isEmpty(other.registrationNumber)) {
      copying("registrationNumber", registrationNumber);
      other.registrationNumber = registrationNumber;
    }
    if (isNotEmpty(extractionMethod) && isEmpty(other.extractionMethod)) {
      copying("extractionMethod", extractionMethod);
      other.extractionMethod = extractionMethod;
    }
    if (isNotEmpty(sequencingStaff) && isEmpty(other.sequencingStaff)) {
      copying("sequencingStaff", sequencingStaff);
      other.sequencingStaff = sequencingStaff;
    }
    if (isNotEmpty(amplificationStaff) && isEmpty(other.amplificationStaff)) {
      copying("amplificationStaff", amplificationStaff);
      other.amplificationStaff = amplificationStaff;
    }
    // TODO: CRS & BOLD
  }

  private static void copying(String field, Object value) {
    guiLogger.debugf(() -> format("Copying value of %s to new document: \"%s\"", field, value));
  }

  public void setValue(NaturalisField field, Object value) {
    if (value == null) {
      return;
    }
    String sval = value.toString();
    switch (field) {
      case SMPL_AMPLIFICATION_STAFF:
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
        documentVersion = sval;
        break;
      case SMPL_EXTRACTION_METHOD:
        extractionMethod = sval;
        break;
      case SMPL_EXTRACT_ID:
        extractId = sval;
        break;
      case SMPL_EXTRACT_PLATE_ID:
        extractPlateId = sval;
        break;
      case SEQ_MARKER:
        marker = sval;
        break;
      case SEQ_PCR_PLATE_ID:
        pcrPlateId = sval;
        break;
      case SMPL_PLATE_POSITION:
        platePosition = sval;
        break;
      case SMPL_REGISTRATION_NUMBER:
        registrationNumber = sval;
        break;
      case SMPL_REGNO_PLUS_SCI_NAME:
        regnoPlusSciName = sval;
        break;
      case SMPL_SAMPLE_PLATE_ID:
        samplePlateId = sval;
        break;
      case SMPL_SCIENTIFIC_NAME:
        scientificName = sval;
        break;
      case SMPL_SEQUENCING_STAFF:
        sequencingStaff = sval;
        break;
    }
  }

  public Object getValue(NaturalisField field) {
    switch (field) {
      case SMPL_AMPLIFICATION_STAFF:
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
        return documentVersion;
      case SMPL_EXTRACTION_METHOD:
        return extractionMethod;
      case SMPL_EXTRACT_ID:
        return extractId;
      case SMPL_EXTRACT_PLATE_ID:
        return extractPlateId;
      case SEQ_MARKER:
        return marker;
      case SEQ_PCR_PLATE_ID:
        return pcrPlateId;
      case SMPL_PLATE_POSITION:
        return platePosition;
      case SMPL_REGISTRATION_NUMBER:
        return registrationNumber;
      case SMPL_REGNO_PLUS_SCI_NAME:
        return regnoPlusSciName;
      case SMPL_SAMPLE_PLATE_ID:
        return samplePlateId;
      case SMPL_SCIENTIFIC_NAME:
        return scientificName;
      case SMPL_SEQUENCING_STAFF:
      default:
        return sequencingStaff;
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

  public String getDocumentVersion() {
    return documentVersion;
  }

  public void setDocumentVersion(String documentVersion) {
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

  public boolean isEmptyNote() {
    return documentVersion == null
        && isEmpty(pcrPlateId)
        && isEmpty(marker)
        && isEmpty(extractPlateId)
        && isEmpty(extractId)
        && isEmpty(samplePlateId)
        && isEmpty(platePosition)
        && isEmpty(scientificName)
        && isEmpty(registrationNumber)
        && isEmpty(extractionMethod)
        && isEmpty(sequencingStaff)
        && isEmpty(amplificationStaff)
        && isEmpty(regnoPlusSciName);
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
