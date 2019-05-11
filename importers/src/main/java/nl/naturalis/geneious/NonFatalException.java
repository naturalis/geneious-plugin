package nl.naturalis.geneious;

/**
 * Non fatal exceptions cause the operation to abort for reasons that do not warrant a "scary" stack trace dump. Only the message of the
 * exception is presented to the user. There this type of exception cannot wrap another exception.
 * 
 * @author Ayco Holleman
 *
 */
public class NonFatalException extends Exception {

  public NonFatalException(String message) {
    super(message);
  }

}
