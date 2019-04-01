package nl.naturalis.geneious.csv;

public class InvalidRowException extends Exception {

  public static final String MSG_BASE = "Invalid row at line %s.";
  public static final String MSG_MISSING_VALUE = MSG_BASE + " Missing value for column %s";

  public static <T extends Enum<T>> InvalidRowException custom(NoteFactory<T> row, String msg, Object... msgArgs) {
    Object[] args = new Object[msgArgs.length + 1];
    args[0] = Integer.valueOf(row.getRownum());
    System.arraycopy(msgArgs, 0, args, 1, msgArgs.length);
    return new InvalidRowException(String.format(MSG_BASE, row, msg, args));
  }

  public static <T extends Enum<T>> InvalidRowException missingValue(NoteFactory<T> row, T column) {
    String msg = String.format(MSG_MISSING_VALUE, row.getRownum(), column.toString());
    return new InvalidRowException(msg);
  }

  public InvalidRowException(String message) {
    super(message);
  }

  public InvalidRowException(Throwable cause) {
    super(cause);
  }

  public InvalidRowException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

}
