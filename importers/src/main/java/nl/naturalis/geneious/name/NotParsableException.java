package nl.naturalis.geneious.name;

/**
 * Thrown when the name of an AB1 file or the header of a fasta sequence could not be broken up into meaningful segments.
 */
public class NotParsableException extends Exception {

  static NotParsableException notEnoughUnderscores(String fileName, int actual, int expected) {
    String fmt = "Not enough underscores in \"%s\": %d (expected %d)";
    return new NotParsableException(String.format(fmt, fileName, actual, expected));
  }

  static NotParsableException missingHyphenInMarkerSegment(String name) {
    String fmt = "Missing hyphen in marker segment in \"%s\"";
    return new NotParsableException(String.format(fmt, name));
  }

  static NotParsableException badExtractId(String name, String extractId, @SuppressWarnings("unused") String pattern) {
    String fmt = "Invalid extract ID in \"%s\": \"%s\"";
    return new NotParsableException(String.format(fmt, name, extractId));
  }

  static NotParsableException badPcrPlateID(String name, String pcrPlateId, @SuppressWarnings("unused") String pattern) {
    String fmt = "Invalid PCR plate ID in \"%s\": \"%s\"";
    return new NotParsableException(String.format(fmt, name, pcrPlateId));
  }

  static NotParsableException badMarkerSegment(String name, String marker, @SuppressWarnings("unused") String pattern) {
    String fmt = "Invalid marker segment in \"%s\": \"%s\"";
    return new NotParsableException(String.format(fmt, name, marker));
  }

  private NotParsableException(String message) {
    super(message);
  }

}
