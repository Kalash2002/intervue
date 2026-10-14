package si.intervue.engine.ai;

/** The route cannot answer; the message is a mapped, user-facing reason, never raw CLI output. */
public class AiUnavailableException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public AiUnavailableException(String reason) {
    super(reason);
  }
}
