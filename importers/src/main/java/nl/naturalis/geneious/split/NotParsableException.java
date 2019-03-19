package nl.naturalis.geneious.split;

/**
 * Thrown when the name of an AB1 file or the header of a fasta sequence could not be broken up into meaningful segments.
 */
public class NotParsableException extends Exception {

  public static NotParsableException invalidNumberOfUnderscores(String fileName, int actual, int expected) {
    if (actual < expected) {
      String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
      return new NotParsableException(String.format(fmt, fileName, actual, expected));
    }
    String fmt = "Too many underscores in name \"%s\": %s (expected %s)";
    return new NotParsableException(String.format(fmt, fileName, actual, expected));
  }

  public static NotParsableException notEnoughUnderscores(String fileName, int actual, int expected) {
    String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
    return new NotParsableException(String.format(fmt, fileName, actual, expected));
  }

  public static NotParsableException missingHyphenInMarkerSegment(String name) {
    String fmt = "Missing hyphen in marker segment of name \"%s\"";
    return new NotParsableException(String.format(fmt, name));
  }

  public static NotParsableException badExtractId(String name, String extractId, String pattern) {
    String fmt = "Invalid extract ID in \"%s\": \"%s\" (must match pattern \"%s\")";
    return new NotParsableException(String.format(fmt, name, extractId, pattern));
  }

  public static NotParsableException badPcrPlateID(String name, String pcrPlateId, String pattern) {
    String fmt = "Invalid PCR plate ID in \"%s\": \"%s\" (must match pattern \"%s\")";
    return new NotParsableException(String.format(fmt, name, pcrPlateId, pattern));
  }

  public static NotParsableException badMarkerSegment(String name, String marker, String pattern) {
    String fmt = "Invalid marker segment in \"%s\": \"%s\" (must match pattern \"%s\")";
    return new NotParsableException(String.format(fmt, name, marker, pattern));
  }

  private NotParsableException(String message) {
    super(message);
  }

}
