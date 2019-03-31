package nl.naturalis.geneious.csv;

public class InvalidRowException extends Exception {

  private static final String ERR_BASE = "Invalid row at line %s.";
  private static final String ERR_MISSING_VALUE = ERR_BASE + " Missing value for column %s";

  public static <T extends Enum<T>> InvalidRowException custom(NoteFactory<T> row, String msg, Object... msgArgs) {
    Object[] args = new Object[msgArgs.length + 1];
    args[0] = Integer.valueOf(row.getRownum());
    System.arraycopy(msgArgs, 0, args, 1, msgArgs.length);
    return new InvalidRowException(String.format(ERR_BASE, row, msg, args));
  }

  public static <T extends Enum<T>> InvalidRowException missingValue(NoteFactory<T> row, T column) {
    String msg = String.format(ERR_MISSING_VALUE, row.getRownum(), column.toString());
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
