package nl.naturalis.geneious;

/**
 * A {@code NonFatalException} is an exception that prevents the plugin from proceeding any further, but it does not
 * warrant scaring the user with a stack trace dump. Only a regular ERROR message is presented to the user. Therefore
 * this type of exception cannot wrap another exception.
 * 
 * @author Ayco Holleman
 *
 */
public class NonFatalException extends Exception {

  public NonFatalException(String message) {
    super(message);
  }

}
