package si.intervue.engine.session;

/** Something the user asked for that the session rules refuse; the message is shown as is. */
public class SessionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public SessionException(String message) {
    super(message);
  }
}
