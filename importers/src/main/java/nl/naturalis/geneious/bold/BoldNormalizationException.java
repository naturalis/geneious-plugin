package nl.naturalis.geneious.bold;

import nl.naturalis.geneious.NonFatalException;

/**
 * Thrown when something goes wrong while a normalizing the BOLD file.
 * @author Ayco Holleman
 *
 */
public class BoldNormalizationException extends NonFatalException {

  public BoldNormalizationException(String message) {
    super(message);
  }

}
