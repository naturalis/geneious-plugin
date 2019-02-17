package nl.naturalis.geneious.split;

public class BadFileNameException extends Exception {

  public static BadFileNameException invalidNumberOfUnderscores(String fileName, int actual,
      int expected) {
    if (actual < expected) {
      String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
      return new BadFileNameException(String.format(fmt, fileName, actual, expected));
    }
    String fmt = "Too many underscores in name \"%s\": %s (expected %s)";
    return new BadFileNameException(String.format(fmt, fileName, actual, expected));
  }

  public static BadFileNameException notEnoughUnderscores(String fileName, int actual,
      int expected) {
    String fmt = "Not enough underscores in name \"%s\": %s (expected %s)";
    return new BadFileNameException(String.format(fmt, fileName, actual, expected));
  }

  public static BadFileNameException missingHyphenInMarkerSegment(String fileName) {
    String fmt = "Missing hyphen in marker segment of name \"%s\"";
    return new BadFileNameException(String.format(fmt, fileName));
  }

  public static BadFileNameException unknownExtension(String fileName) {
    int i = fileName.lastIndexOf('.');
    if (i == -1 || i == fileName.length() - 1) {
      String fmt = "Canot parse file name: \"%s\". Missing extension";
      return new BadFileNameException(String.format(fmt, fileName));
    }
    String fmt = "Canot parse file name \"%s\". Unknown file type: \"%s\"";
    return new BadFileNameException(String.format(fmt, fileName, fileName.substring(i + 1)));
  }

  public BadFileNameException(String arg0) {
    super(arg0);
  }

  public BadFileNameException(Throwable arg0) {
    super(arg0);
  }

  public BadFileNameException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
