package nl.naturalis.geneious.notes;

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
  REGNO_PLUS_SCI_NAME("RegistrationNumberCode_TaxonName2Code_Samples",
      "Registr-nmbr_[Scientific_name] (Samples)");

  private final String code;
  private final String name;
  private final String descr;

  private NaturalisField(String code, String name) {
    this.code = code;
    this.name = name;
    this.descr = name;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return descr;
  }

}
