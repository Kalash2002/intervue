package si.intervue.problemformat;

/** A problem folder that cannot be read at all: missing or unparseable JSON. */
public class ProblemFormatException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ProblemFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
