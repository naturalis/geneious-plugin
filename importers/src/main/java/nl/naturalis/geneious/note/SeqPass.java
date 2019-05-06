package nl.naturalis.geneious.note;

public enum SeqPass {

  OK, MEDIUM, LOW, CONTAMINATION, ENDO_CONTAMINATION, EXO_CONTAMINATION;

  private final String value;

  private SeqPass() {
    value = name().equals("OK") ? name() : name().toLowerCase().replace('_', '-');
  }

  @Override
  public String toString() {
    return value;
  }

}
