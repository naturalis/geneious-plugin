package nl.naturalis.geneious.bold;

import java.util.Objects;

import nl.naturalis.geneious.smpl.DummySequence;

/**
 * The key used to store the documents selected by the user into an in-memory cache: the combination of the document's
 * CRS registration number and marker.
 *
 * @author Ayco Holleman
 */
final class BoldKey {

  static final String NO_MARKER = new String();

  private final String regno;
  private final String marker;
  private final int hash;

  /**
   * Creates a new {@code BoldKey} for the provided registration number and marker.
   * 
   * @param regno
   * @param marker
   */
  BoldKey(String regno, String marker) {
    Objects.requireNonNull(regno, () -> "Registration number must not be null");
    Objects.requireNonNull(marker, () -> "Marker must not be null");
    this.regno = regno;
    this.marker = marker;
    this.hash = getHashCode();
  }

  String getRegno() {
    return regno;
  }

  String getMarker() {
    return marker;
  }

  /**
   * 
   * The {@code equals} and {@code hashCode} methods in {@code BoldKey} have very non-standard implementations due to the
   * following logic:
   * <ul>
   * <li>A BOLD row with both regno and marker matches a non-dummy document if it has the same combination of regno and
   * marker.
   * <li>A BOLD row with both regno and marker matches dummy documents with the same regno, but the marker-related columns
   * in the BOLD row must not be set on the dummy document. (N.B. the marker of a dummy document will always be "Dum".)
   * <li>A BOLD row with only regno matches any non-dummy document if it has the same regno, whatever its marker.
   * Marker-related columns in the BOLD row must not be set on the document, but that's superfluous, since this case is
   * about a BOLD row only having a regno.
   * </ul>
   * 
   */
  @Override
  public boolean equals(Object obj) {
    BoldKey other = (BoldKey) obj;
    if(this.marker == NO_MARKER) {
      // This key comes from a BOLD row; the other key comes from a selected document
      return this.regno.equals(other.regno);
    }
    if(other.marker == NO_MARKER) {
      // This key comes from a selected document; the other key comes from a BOLD row
      return this.regno.equals(other.regno);
    }
    if(this.marker.equals(DummySequence.DUMMY_MARKER)) {
      // This key comes from a selected dummy document; the other key comes from a BOLD row
      return this.regno.equals(other.regno);
    }
    if(other.marker.equals(DummySequence.DUMMY_MARKER)) {
      // This key comes from a BOLD row; the other key comes from a selected dummy document
      return this.regno.equals(other.regno);
    }
    return regno.equals(other.regno) && marker.equals(other.marker);
  }

  @Override
  public int hashCode() {
    return hash;
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

  private int getHashCode() {
    int hash = regno.hashCode();
    if(marker == NO_MARKER || marker.equals(DummySequence.DUMMY_MARKER)) {
      return hash;
    }
    return (hash * 31) + marker.hashCode();
  }

}
