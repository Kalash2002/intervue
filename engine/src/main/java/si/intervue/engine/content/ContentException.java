package si.intervue.engine.content;

/** A problem that is unknown, unreadable or invalid; the message is shown as is. */
public class ContentException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ContentException(String message) {
    super(message);
  }
}
