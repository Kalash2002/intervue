package si.intervue.engine.ai;

/**
 * Whether the assistant can be used now, for the room's controls. {@code developer} is the engine's
 * developer mode; {@code reason} says why the assistant is unavailable, or is null when it is.
 */
public record AiStatus(boolean developer, boolean available, String reason) {

  /**
   * End-user builds before Phase 1b: no backend route exists yet, and the CLI route is dev-only.
   */
  public static final String NO_ROUTE = "Assistant unavailable: sign-in arrives in Phase 1b";

  public static AiStatus off() {
    return new AiStatus(false, false, NO_ROUTE);
  }
}
