package si.intervue.engine.ai;

import java.util.function.Consumer;

/** The route when none exists: every call fails with the same reason and nothing is started. */
public final class UnavailableAiClient implements AiClient {

  private final AiStatus status;

  public UnavailableAiClient(AiStatus status) {
    this.status = status;
  }

  @Override
  public AiReply chat(AiRequest request, Consumer<String> deltas) {
    throw new AiUnavailableException(status.reason());
  }

  @Override
  public AiReply edit(AiRequest request, Consumer<String> deltas) {
    throw new AiUnavailableException(status.reason());
  }

  @Override
  public AiReply hintExplain(AiRequest request, Consumer<String> deltas) {
    throw new AiUnavailableException(status.reason());
  }

  @Override
  public AiReply feedback(AiRequest request, Consumer<String> deltas) {
    throw new AiUnavailableException(status.reason());
  }

  @Override
  public AiStatus status() {
    return status;
  }
}
