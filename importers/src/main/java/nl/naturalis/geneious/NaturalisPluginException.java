package nl.naturalis.geneious;

/**
 * Base class for all runtime exceptions thrown by the plugin.
 * 
 * @author Ayco Holleman
 *
 */
public class NaturalisPluginException extends RuntimeException {

  public NaturalisPluginException(String message) {
    super(message);
  }

  public NaturalisPluginException(String message, Object... args) {
    super(String.format(message, args));
  }

  public NaturalisPluginException(Throwable cause) {
    super(cause);
  }

  public NaturalisPluginException(String message, Throwable cause) {
    super(message, cause);
  }

}
