package nl.naturalis.geneious.note;

import static nl.naturalis.geneious.note.NaturalisField.AMPLIFICATION_STAFF;
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
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

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
    AnnotatedPluginDocument.DocumentNotes notes = doc.getDocumentNotes(true);
    addNoteIfNotNull(notes, DOCUMENT_VERSION, documentVersion);
    addNoteIfNotNull(notes, PCR_PLATE_ID, pcrPlateId);
    addNoteIfNotNull(notes, MARKER, marker);
    addNoteIfNotNull(notes, EXTRACT_PLATE_ID, extractPlateId);
    addNoteIfNotNull(notes, EXTRACT_ID, extractId);
    addNoteIfNotNull(notes, SAMPLE_PLATE_ID, samplePlateId);
    addNoteIfNotNull(notes, PLATE_POSITION, platePosition);
    addNoteIfNotNull(notes, SCIENTIFIC_NAME, scientificName);
    addNoteIfNotNull(notes, REGISTRATION_NUMBER, registrationNumber);
    addNoteIfNotNull(notes, EXTRACTION_METHOD, extractionMethod);
    addNoteIfNotNull(notes, SEQUENCING_STAFF, sequencingStaff);
    addNoteIfNotNull(notes, AMPLIFICATION_STAFF, amplificationStaff);
    addNoteIfNotNull(notes, REGNO_PLUS_SCI_NAME, regnoPlusSciName);
    notes.saveNotes();
  }

  private static void addNoteIfNotNull(AnnotatedPluginDocument.DocumentNotes notes,
      NaturalisField nf, Object val) {
    if (val != null) {
      notes.setNote(nf.newNote(val));
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
