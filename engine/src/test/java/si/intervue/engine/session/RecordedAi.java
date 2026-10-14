package si.intervue.engine.session;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import si.intervue.engine.ai.AiClient;
import si.intervue.engine.ai.AiReply;
import si.intervue.engine.ai.AiRequest;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.ai.AiUnavailableException;
import si.intervue.engine.ai.AiUsage;

/** A model double: replays queued replies word by word and keeps every request it was sent. */
final class RecordedAi implements AiClient {

  record Call(String kind, AiRequest request) {}

  final List<Call> calls = new ArrayList<>();
  private final Deque<String> replies = new ArrayDeque<>();
  private String unavailable;

  RecordedAi reply(String text) {
    replies.add(text);
    return this;
  }

  private RuntimeException crash;

  /** The next calls throw this, as a broken database or a closed client stream would. */
  RecordedAi crash(RuntimeException e) {
    this.crash = e;
    return this;
  }

  RecordedAi unavailable(String reason) {
    this.unavailable = reason;
    return this;
  }

  Call last() {
    return calls.get(calls.size() - 1);
  }

  private AiReply answer(String kind, AiRequest request, Consumer<String> deltas) {
    if (unavailable != null) {
      throw new AiUnavailableException(unavailable);
    }
    if (crash != null) {
      throw crash;
    }
    calls.add(new Call(kind, request));
    String text = replies.isEmpty() ? "" : replies.poll();
    for (String word : text.split("(?<= )")) {
      deltas.accept(word);
    }
    return new AiReply(text, new AiUsage("recorded", "test-model", 100, 20, 5));
  }

  @Override
  public AiReply chat(AiRequest request, Consumer<String> deltas) {
    return answer("chat", request, deltas);
  }

  @Override
  public AiReply edit(AiRequest request, Consumer<String> deltas) {
    return answer("edit", request, deltas);
  }

  @Override
  public AiReply hintExplain(AiRequest request, Consumer<String> deltas) {
    return answer("hint", request, deltas);
  }

  @Override
  public AiReply feedback(AiRequest request, Consumer<String> deltas) {
    return answer("feedback", request, deltas);
  }

  @Override
  public AiStatus status() {
    return unavailable == null
        ? new AiStatus(true, true, null)
        : new AiStatus(true, false, unavailable);
  }
}
