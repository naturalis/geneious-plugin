package nl.naturalis.geneious.bold;

public class BoldNote {
  
  private String boldId;
  private String projectId;
  private String fieldId;
  private String numberOfImages;
  private String binCode;
  private String boldUri;

  public String getBoldId() {
    return boldId;
  }

  public void setBoldId(String boldId) {
    this.boldId = boldId;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getFieldId() {
    return fieldId;
  }

  public void setFieldId(String fieldId) {
    this.fieldId = fieldId;
  }

  public String getNumberOfImages() {
    return numberOfImages;
  }

  public void setNumberOfImages(String numberOfImages) {
    this.numberOfImages = numberOfImages;
  }

  public String getBinCode() {
    return binCode;
  }

  public void setBinCode(String binCode) {
    this.binCode = binCode;
  }

  public String getBoldUri() {
    return boldUri;
  }

  public void setBoldUri(String boldUri) {
    this.boldUri = boldUri;
  }

}
