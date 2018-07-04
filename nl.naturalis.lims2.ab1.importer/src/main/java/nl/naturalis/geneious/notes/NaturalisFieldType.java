package nl.naturalis.geneious.notes;

public enum NaturalisFieldType {

  EXTRACT_ID("ExtractIDCode_Seq"),
  PLATE_ID("PCRplateIDCode_Seq"),
  MARKER("MarkerCode_Seq");

  private final String code;

  private NaturalisFieldType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
