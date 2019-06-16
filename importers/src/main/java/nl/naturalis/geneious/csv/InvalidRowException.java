package nl.naturalis.geneious.csv;

/**
 * Thrown when a row in a CSV-like file contains invalid values.
 * 
 * @author Ayco Holleman
 *
 */
public class InvalidRowException extends Exception {

  public static final String MSG_BASE = "Invalid row at line %s.";
  public static final String MSG_MISSING_VALUE = MSG_BASE + " Missing value for column %s";

  /**
   * Returns an {@code InvalidRowException} with a custom message, prefixed with "Invalid row at line x".
   * 
   * @param factory
   * @param msg
   * @param msgArgs
   * @return
   */
  public static <T extends Enum<T>> InvalidRowException custom(NoteFactory<T> factory, String msg, Object... msgArgs) {
    if (msgArgs.length == 0) {
      String fmt = MSG_BASE + msg;
      return new InvalidRowException(String.format(fmt, factory.getRownum()));
    }
    Object[] args = new Object[msgArgs.length + 1];
    args[0] = Integer.valueOf(factory.getRownum());
    System.arraycopy(msgArgs, 0, args, 1, msgArgs.length);
    return new InvalidRowException(String.format(MSG_BASE + msg, args));
  }

  /**
   * Returns an {@code InvalidRowException} with a message indicating that a required value is missing.
   * 
   * @param factory
   * @param column
   * @return
   */
  public static <T extends Enum<T>> InvalidRowException missingValue(NoteFactory<T> factory, T column) {
    String msg = String.format(MSG_MISSING_VALUE, factory.getRownum(), column.toString());
    return new InvalidRowException(msg);
  }

  private InvalidRowException(String message) {
    super(message);
  }

}
