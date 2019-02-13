package nl.naturalis.geneious.tracefile;

public class TraceFileImportException extends Exception {

  public TraceFileImportException(String message) {
    super(message);
  }

  public TraceFileImportException(Throwable cause) {
    super(cause);
  }

  public TraceFileImportException(String message, Throwable cause) {
    super(message, cause);
  }

}
