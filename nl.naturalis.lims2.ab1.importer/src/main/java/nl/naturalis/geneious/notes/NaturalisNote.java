package nl.naturalis.geneious.notes;

import static nl.naturalis.geneious.notes.NaturalisField.AMPLIFICATION_STAFF;
import static nl.naturalis.geneious.notes.NaturalisField.DOCUMENT_VERSION;
import static nl.naturalis.geneious.notes.NaturalisField.EXTRACTION_METHOD;
import static nl.naturalis.geneious.notes.NaturalisField.EXTRACT_ID;
import static nl.naturalis.geneious.notes.NaturalisField.EXTRACT_PLATE_ID;
import static nl.naturalis.geneious.notes.NaturalisField.MARKER;
import static nl.naturalis.geneious.notes.NaturalisField.PCR_PLATE_ID;
import static nl.naturalis.geneious.notes.NaturalisField.PLATE_POSITION;
import static nl.naturalis.geneious.notes.NaturalisField.SAMPLE_PLATE_ID;
import static nl.naturalis.geneious.notes.NaturalisField.REGISTRATION_NUMBER;
import static nl.naturalis.geneious.notes.NaturalisField.REGNO_PLUS_SCI_NAME;
import static nl.naturalis.geneious.notes.NaturalisField.SCIENTIFIC_NAME;
import static nl.naturalis.geneious.notes.NaturalisField.SEQUENCING_STAFF;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;

public class NaturalisNote {

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

  public NaturalisNote() {}

  public void attach(AnnotatedPluginDocument doc) {
    DocumentNoteType noteType = NoteTypeFactory.INSTANCE.getNaturalisSequenceNoteType();
    DocumentNote note = noteType.createDocumentNote();
    setValueIfNotNull(note, DOCUMENT_VERSION, documentVersion);
    setValueIfNotNull(note, PCR_PLATE_ID, pcrPlateId);
    setValueIfNotNull(note, MARKER, marker);
    setValueIfNotNull(note, EXTRACT_PLATE_ID, extractPlateId);
    setValueIfNotNull(note, EXTRACT_ID, extractId);
    setValueIfNotNull(note, SAMPLE_PLATE_ID, samplePlateId);
    setValueIfNotNull(note, PLATE_POSITION, platePosition);
    setValueIfNotNull(note, SCIENTIFIC_NAME, scientificName);
    setValueIfNotNull(note, REGISTRATION_NUMBER, registrationNumber);
    setValueIfNotNull(note, EXTRACTION_METHOD, extractionMethod);
    setValueIfNotNull(note, SEQUENCING_STAFF, sequencingStaff);
    setValueIfNotNull(note, AMPLIFICATION_STAFF, amplificationStaff);
    setValueIfNotNull(note, REGNO_PLUS_SCI_NAME, regnoPlusSciName);
    AnnotatedPluginDocument.DocumentNotes notes = doc.getDocumentNotes(true);
    notes.setNote(note);
    notes.saveNotes();
  }

  private static void setValueIfNotNull(DocumentNote note, NaturalisField fieldType, Object val) {
    if (val != null) {
      note.setFieldValue(fieldType.getCode(), val);
    }
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

}
