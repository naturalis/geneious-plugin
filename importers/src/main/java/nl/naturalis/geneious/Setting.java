package nl.naturalis.geneious;

public enum Setting {

  OPERATION_FINISHED("nl.naturalis.geneious.operation.finished");

  private final String name;

  private Setting(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
