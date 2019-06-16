package nl.naturalis.geneious.bold;

import java.util.Objects;

/**
 * The key used to store the documents slect by the user into an in-memory cache: the combination of the
 * document's CRS registration number and marker.
 *
 * @author Ayco Holleman
 */
final class BoldKey {

  private final String regno;
  private final String marker;
  private final int hash;

  BoldKey(String regno, String marker) {
    Objects.requireNonNull(regno, () -> "Registration number must not be null");
    Objects.requireNonNull(marker, () -> "Marker must not be null");
    this.regno = regno;
    this.marker = marker;
    this.hash = (regno.hashCode() * 31) + marker.hashCode();
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    BoldKey other = (BoldKey) obj;
    return regno.endsWith(other.regno) && marker.equals(other.marker);
  }

  public String toString() {
    return new StringBuilder()
        .append("[regno=\"")
        .append(regno)
        .append("\"; marker=\"")
        .append(marker)
        .append("\"]")
        .toString();
  }

}
