package nl.naturalis.geneious;

/**
 * Non-fatal exceptions cause the plugin to abort, but they do not warrant presenting the user with a "scary"
 * stack trace dump. Only the exception message is presented to the user. Therefore this type of exception
 * cannot wrap another exception.
 * 
 * @author Ayco Holleman
 *
 */
public class NonFatalException extends Exception {

    public NonFatalException(String message) {
        super(message);
    }

}
