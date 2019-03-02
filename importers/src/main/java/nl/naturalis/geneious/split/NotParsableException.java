package nl.naturalis.geneious.split;

/**
 * Thrown when the name of an AB1 file or the header of a fasta sequence could not be broken up into meaningful segments.
 */
public class NotParsableException extends Exception {

  public static NotParsableException invalidNumberOfUnderscores(String fileName, int actual,
      int expected) {
    if (actual < expected) {
      String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
      return new NotParsableException(String.format(fmt, fileName, actual, expected));
    }
    String fmt = "Too many underscores in name \"%s\": %s (expected %s)";
    return new NotParsableException(String.format(fmt, fileName, actual, expected));
  }

  public static NotParsableException notEnoughUnderscores(String fileName, int actual,
      int expected) {
    String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
    return new NotParsableException(String.format(fmt, fileName, actual, expected));
  }

  public static NotParsableException missingHyphenInMarkerSegment(String fileName) {
    String fmt = "Missing hyphen in marker segment of name \"%s\"";
    return new NotParsableException(String.format(fmt, fileName));
  }

  @Deprecated
  public static NotParsableException unknownExtension(String fileName) {
    int i = fileName.lastIndexOf('.');
    if (i == -1 || i == fileName.length() - 1) {
      String fmt = "Canot parse file name: \"%s\". Missing extension";
      return new NotParsableException(String.format(fmt, fileName));
    }
    String fmt = "Canot parse file name \"%s\". Unknown file type: \"%s\"";
    return new NotParsableException(String.format(fmt, fileName, fileName.substring(i + 1)));
  }

  public NotParsableException(String arg0) {
    super(arg0);
  }

  public NotParsableException(Throwable arg0) {
    super(arg0);
  }

  public NotParsableException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
