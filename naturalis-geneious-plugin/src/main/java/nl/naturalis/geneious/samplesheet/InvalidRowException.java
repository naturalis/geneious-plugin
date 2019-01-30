package nl.naturalis.geneious.samplesheet;

public class InvalidRowException extends Exception {

  public InvalidRowException(String message) {
    super(message);
  }

  public InvalidRowException(Throwable cause) {
    super(cause);
  }

  public InvalidRowException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
