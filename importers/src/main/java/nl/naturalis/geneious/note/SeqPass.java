package nl.naturalis.geneious.note;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Symbolic constants for the valid values for the "Pass (Seq)" annotation.
 * 
 * @author Ayco Holleman
 *
 */
public enum SeqPass {

  NOT_DETERMINED("not determined"),
  OK("OK"), MEDIUM("medium"),
  LOW("low"),
  CONTAMINATION("contamination"),
  ENDO_CONTAMINATION("endo-contamination"),
  EXO_CONTAMINATION("exo-contamination");

  private final String value;

  private SeqPass(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return value;
  }

}
