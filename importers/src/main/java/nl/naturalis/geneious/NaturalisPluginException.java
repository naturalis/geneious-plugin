package nl.naturalis.geneious;

public class NaturalisPluginException extends RuntimeException {

  public NaturalisPluginException(String message) {
    super(message);
  }

  public NaturalisPluginException(Throwable cause) {
    super(cause);
  }

  public NaturalisPluginException(String message, Throwable cause) {
    super(message, cause);
  }

}
