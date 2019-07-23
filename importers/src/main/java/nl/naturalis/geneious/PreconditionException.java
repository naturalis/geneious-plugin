package nl.naturalis.geneious;

import nl.naturalis.geneious.util.PreconditionValidator;

/**
 * Thrown by the {@link PreconditionValidator} when a precondition is not met.
 * 
 * @author Ayco Holleman
 *
 */
public class PreconditionException extends NonFatalException {

  public PreconditionException(String message) {
    super(message);
  }

}
