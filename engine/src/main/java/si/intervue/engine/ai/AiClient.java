package si.intervue.engine.ai;

import java.util.function.Consumer;

/**
 * The assistant's four calls (PLAN.md 2.5). Phase 1ab has the developer-only Claude CLI route and
 * an unavailable route; Phase 1b adds the backend. Each call streams text to {@code deltas} as it
 * arrives and returns the whole reply with its usage. Implementations throw {@link
 * AiUnavailableException} with a user-facing reason when the route cannot be used.
 */
public interface AiClient {

  /** Ask channel: a text answer under the session's rules. */
  AiReply chat(AiRequest request, Consumer<String> deltas);

  /** Write code channel: the reply is edit-protocol lines (docs/contracts/edit-protocol.md). */
  AiReply edit(AiRequest request, Consumer<String> deltas);

  /** Practice only: explains a revealed hint without going past it. */
  AiReply hintExplain(AiRequest request, Consumer<String> deltas);

  /** The written review of a finished session. */
  AiReply feedback(AiRequest request, Consumer<String> deltas);

  /** Whether calls can be made now, and why not; never spends tokens. */
  AiStatus status();
}
