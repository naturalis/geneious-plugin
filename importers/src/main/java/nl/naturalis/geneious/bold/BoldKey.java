package nl.naturalis.geneious.bold;

import java.util.Objects;

import nl.naturalis.geneious.util.JsonUtil;

/**
 * The key used to store and find the selected documents in an in-memory lookup table
 * 
 * @author Ayco Holleman
 */
class BoldKey {

  private final String regno;
  private final String marker;
  private final int hash;

  /**
   * Creates a new {@code BoldKey} for the provided registration number.
   * 
   * @param regno
   * @param marker
   */
  BoldKey(String regno) {
    this(regno, null);
  }

  /**
   * Creates a new {@code BoldKey} for the provided registration number and marker.
   * 
   * @param regno
   * @param marker
   */
  BoldKey(String regno, String marker) {
    Objects.requireNonNull(regno, () -> "Registration number must not be null");
    this.regno = regno;
    this.marker = marker;
    this.hash = getHashCode();
  }

  /**
   * Returns the CRS registration number.
   * 
   * @return
   */
  public String getRegno() {
    return regno;
  }

  /**
   * Returns the marker.
   * 
   * @return
   */
  public String getMarker() {
    return marker == null ? "<any>" : marker;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    BoldKey other = (BoldKey) obj;
    return regno.equals(other.regno) && Objects.equals(marker, other.marker);
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public String toString() {
    return JsonUtil.toJson(this);
  }

  private int getHashCode() {
    return Objects.hash(regno, marker);
  }

}
