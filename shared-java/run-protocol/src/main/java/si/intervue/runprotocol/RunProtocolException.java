package si.intervue.runprotocol;

/** A results file the engine must not grade: wrong version, no start line, corrupt or missing. */
public class RunProtocolException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public RunProtocolException(String message) {
    super(message);
  }

  public RunProtocolException(String message, Throwable cause) {
    super(message, cause);
  }
}
