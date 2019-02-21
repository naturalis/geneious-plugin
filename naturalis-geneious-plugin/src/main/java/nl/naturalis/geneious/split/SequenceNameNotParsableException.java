package nl.naturalis.geneious.split;

public class SequenceNameNotParsableException extends Exception {

  public static SequenceNameNotParsableException invalidNumberOfUnderscores(String fileName, int actual,
      int expected) {
    if (actual < expected) {
      String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
      return new SequenceNameNotParsableException(String.format(fmt, fileName, actual, expected));
    }
    String fmt = "Too many underscores in name \"%s\": %s (expected %s)";
    return new SequenceNameNotParsableException(String.format(fmt, fileName, actual, expected));
  }

  public static SequenceNameNotParsableException notEnoughUnderscores(String fileName, int actual,
      int expected) {
    String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
    return new SequenceNameNotParsableException(String.format(fmt, fileName, actual, expected));
  }

  public static SequenceNameNotParsableException missingHyphenInMarkerSegment(String fileName) {
    String fmt = "Missing hyphen in marker segment of name \"%s\"";
    return new SequenceNameNotParsableException(String.format(fmt, fileName));
  }

  public static SequenceNameNotParsableException unknownExtension(String fileName) {
    int i = fileName.lastIndexOf('.');
    if (i == -1 || i == fileName.length() - 1) {
      String fmt = "Canot parse file name: \"%s\". Missing extension";
      return new SequenceNameNotParsableException(String.format(fmt, fileName));
    }
    String fmt = "Canot parse file name \"%s\". Unknown file type: \"%s\"";
    return new SequenceNameNotParsableException(String.format(fmt, fileName, fileName.substring(i + 1)));
  }

  public SequenceNameNotParsableException(String arg0) {
    super(arg0);
  }

  public SequenceNameNotParsableException(Throwable arg0) {
    super(arg0);
  }

  public SequenceNameNotParsableException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
