package nl.naturalis.geneious;

import java.io.PrintStream;
import java.io.PrintWriter;

public class WrappedException extends RuntimeException {

  public WrappedException(Throwable cause) {
    this(cause.getMessage(), cause);
  }

  public WrappedException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public void printStackTrace(PrintWriter s) {
    getCause().printStackTrace(s);
  }

  @Override
  public String toString() {
    return getCause().toString();
  }

  @Override
  public void printStackTrace() {
    getCause().printStackTrace();
  }

  @Override
  public void printStackTrace(PrintStream s) {
    getCause().printStackTrace(s);
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return super.getStackTrace();
  }

}
