package nl.naturalis.geneious;

/**
 * Base class for runtime exceptions thrown by the plugin.
 * 
 * @author Ayco Holleman
 *
 */
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
