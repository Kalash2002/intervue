package si.intervue.engine.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import si.intervue.engine.ai.AiClient;
import si.intervue.engine.ai.AiReply;
import si.intervue.engine.ai.AiRequest;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.ai.AiUnavailableException;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.PromptBuilder.Rules;
import si.intervue.engine.session.Sessions.Context;
import si.intervue.engine.session.edits.EditOutcome;
import si.intervue.engine.session.edits.EditReply;
import si.intervue.engine.store.Database;
import tools.jackson.databind.json.JsonMapper;

/**
 * The assistant in a session (PLAN.md 2.8): Ask, Write code, hint explanations and the written
 * review. Model replies for Write code go to the edit applier like any edit stream. Every call
 * leaves an {@code aiCall} event with its route and usage, never its text.
 */
public final class SessionAssistant {

  public static final String HELPFUL = "helpful";
  public static final String INTERVIEW = "interview";

  /** One chat turn; {@code role} is {@code user} or {@code assistant}. */
  public record Turn(String role, String text, long tMs) {}

  /** {@code status} is pending, ready (with text) or unavailable (with reason). */
  public record Review(String status, String text, String reason) {}

  private static final Logger LOG = Logger.getLogger(SessionAssistant.class.getName());
  private static final JsonMapper JSON = JsonMapper.builder().build();

  private final Sessions sessions;
  private final SessionStore store;
  private final AssistantStore records;
  private final AiClient ai;
  private final Executor reviews;
  private final Set<String> reviewing = ConcurrentHashMap.newKeySet();

  public SessionAssistant(Sessions sessions, Database db, AiClient ai, Executor reviews) {
    this.sessions = sessions;
    this.store = new SessionStore(db);
    this.records = new AssistantStore(db);
    this.ai = ai;
    this.reviews = reviews;
  }

  public AiStatus status() {
    return ai.status();
  }

  public List<Turn> chat(String id) {
    sessions.context(id);
    return records.turns(id);
  }

  public String ask(String id, String message, String rules, Consumer<String> deltas) {
    Context c = sessions.requireOpen(id);
    AiRequest request = PromptBuilder.ask(c, rules(c, rules), history(c), message);
    AiReply reply = call("chat", c, () -> ai.chat(request, deltas));
    remember(c, message, reply.text(), reply);
    return reply.text();
  }

  public EditReply write(String id, String message, String rules) {
    Context c = sessions.requireOpen(id);
    String replyId = "w" + (count(c, "edit") + 1);
    AiRequest request = PromptBuilder.write(c, rules(c, rules), history(c), message, replyId);
    AiReply reply = call("edit", c, () -> ai.edit(request, d -> {}));
    EditReply applied = sessions.applyEdits(id, protocolLines(replyId, reply.text()));
    remember(c, message, summary(applied), reply);
    return applied;
  }

  /** Practice only, and only for a level the candidate has already revealed. */
  public String explainHint(String id, int phase, Consumer<String> deltas) {
    Context c = sessions.requireOpen(id);
    if (c.session().type() != SessionType.PRACTICE) {
      throw new SessionException("Hints are for Practice sessions.");
    }
    List<String> revealed = sessions.revealedHints(id, phase);
    if (revealed.isEmpty()) {
      throw new SessionException("Reveal a hint for phase " + phase + " first.");
    }
    int level = revealed.size();
    AiRequest request = PromptBuilder.hint(c, history(c), phase, level, revealed.get(level - 1));
    AiReply reply = call("hint", c, () -> ai.hintExplain(request, deltas));
    remember(c, "Explain hint level " + level + " of phase " + phase + ".", reply.text(), reply);
    return reply.text();
  }

  /** Starts the written review in the background; at most one per session at a time. */
  public void requestReview(String id) {
    if (!reviewing.add(id)) {
      return;
    }
    reviews.execute(
        () -> {
          try {
            Context c = sessions.context(id);
            AiRequest request = PromptBuilder.feedback(c, history(c), store.events(id));
            AiReply reply = call("feedback", c, () -> ai.feedback(request, d -> {}));
            records.setReview(id, json(new Review("ready", reply.text(), null)));
          } catch (AiUnavailableException e) {
            records.setReview(id, json(new Review("unavailable", null, e.getMessage())));
          } finally {
            reviewing.remove(id);
          }
        });
  }

  public Review review(String id) {
    sessions.context(id);
    String stored = records.review(id);
    return stored == null
        ? new Review("pending", null, null)
        : JSON.readValue(stored, Review.class);
  }

  private static Rules rules(Context c, String rules) {
    // Interview sessions always get point-and-explain rules; only Practice may choose (PLAN 2.8).
    return c.session().type() == SessionType.INTERVIEW || INTERVIEW.equals(rules)
        ? Rules.INTERVIEW
        : Rules.HELPFUL;
  }

  private List<PromptBuilder.Entry> history(Context c) {
    List<PromptBuilder.Entry> history = new ArrayList<>();
    for (Turn t : records.turns(c.session().id())) {
      history.add(PromptBuilder.Entry.turn(t.role(), t.text(), t.tMs()));
    }
    for (StoredEvent e : store.events(c.session().id())) {
      if (e.type().equals("aiEdit") || e.type().equals("aiEditUndo")) {
        history.add(PromptBuilder.Entry.edit(e));
      }
    }
    return history;
  }

  private AiReply call(String kind, Context c, Supplier<AiReply> model) {
    try {
      AiReply reply = model.get();
      var u = reply.usage();
      Map<String, Object> data =
          Map.of(
              "kind", kind,
              "route", u.route(),
              "model", u.model(),
              "inputTokens", u.inputTokens(),
              "outputTokens", u.outputTokens(),
              "durationMs", u.durationMs(),
              "ok", true);
      sessions.event(c.session(), "aiCall", data);
      LOG.info(() -> "aiCall " + data);
      return reply;
    } catch (AiUnavailableException e) {
      sessions.event(c.session(), "aiCall", Map.of("kind", kind, "ok", false));
      LOG.info(() -> "aiCall kind=" + kind + " ok=false");
      throw e;
    }
  }

  private void remember(Context c, String message, String answer, AiReply reply) {
    long now = sessions.activeMs(c.session(), store.events(c.session().id()));
    records.appendTurn(c.session().id(), "user", message, null, now);
    records.appendTurn(c.session().id(), "assistant", answer, reply.usage().model(), now);
  }

  private int count(Context c, String kind) {
    return (int)
        store.events(c.session().id()).stream()
            .filter(e -> e.type().equals("aiCall") && kind.equals(e.data().get("kind")))
            .count();
  }

  /**
   * The model's JSON lines with our own start line, so reply ids stay unique in the session and a
   * fence or a missing start line from the model does not lose the edits.
   */
  static List<String> protocolLines(String replyId, String text) {
    List<String> lines = new ArrayList<>();
    lines.add("{\"protocol\":1,\"t\":\"start\",\"replyId\":\"" + replyId + "\"}");
    text.lines()
        .map(String::strip)
        .filter(l -> l.startsWith("{") && !l.replace(" ", "").contains("\"t\":\"start\""))
        .forEach(lines::add);
    return lines;
  }

  private static String summary(EditReply reply) {
    StringBuilder s = new StringBuilder(reply.note());
    for (EditOutcome o : reply.outcomes()) {
      s.append("\n(").append(o.status()).append(' ').append(o.op()).append(' ').append(o.path());
      s.append(')');
    }
    return s.toString();
  }

  private static String json(Review review) {
    return JSON.writeValueAsString(review);
  }
}
