package nl.naturalis.geneious;

/**
 * A {@code NonFatalException} is an exception that prevents an operation from proceeding any further, but does not
 * warrant presenting the user with a "scary" stack trace. Instead a regular ERROR message is logged and the operation
 * terminates in a seemingly graceful manner. Because logging the error message is its only intended purpose, a
 * {@code NonFatalException} cannot wrap another exception.
 * 
 * @author Ayco Holleman
 *
 */
public class NonFatalException extends Exception {

  public NonFatalException(String message) {
    super(message);
  }

}
