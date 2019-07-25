package nl.naturalis.geneious.name;

/**
 * Thrown when the name of an AB1 file or the header of a fasta sequence could not be broken up into meaningful segments.
 */
public class NotParsableException extends Exception {

  static NotParsableException invalidNumberOfUnderscores(String fileName, int actual, int expected) {
    if (actual < expected) {
      String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
      return new NotParsableException(String.format(fmt, fileName, actual, expected));
    }
    String fmt = "Too many underscores in name \"%s\": %s (expected %s)";
    return new NotParsableException(String.format(fmt, fileName, actual, expected));
  }

  static NotParsableException notEnoughUnderscores(String fileName, int actual, int expected) {
    String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
    return new NotParsableException(String.format(fmt, fileName, actual, expected));
  }

  static NotParsableException missingHyphenInMarkerSegment(String name) {
    String fmt = "Missing hyphen in marker segment of name \"%s\"";
    return new NotParsableException(String.format(fmt, name));
  }

  static NotParsableException badExtractId(String name, String extractId, String pattern) {
    String fmt = "Invalid extract ID in \"%s\": \"%s\"";
    return new NotParsableException(String.format(fmt, name, extractId, pattern));
  }

  static NotParsableException badPcrPlateID(String name, String pcrPlateId, String pattern) {
    String fmt = "Invalid PCR plate ID in \"%s\": \"%s\"";
    return new NotParsableException(String.format(fmt, name, pcrPlateId, pattern));
  }

  static NotParsableException badMarkerSegment(String name, String marker, String pattern) {
    String fmt = "Invalid marker segment in \"%s\": \"%s\"";
    return new NotParsableException(String.format(fmt, name, marker, pattern));
  }

  private NotParsableException(String message) {
    super(message);
  }

}
