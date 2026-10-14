package si.intervue.engine.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import si.intervue.engine.ai.AiClient;
import si.intervue.engine.ai.AiReply;
import si.intervue.engine.ai.AiRequest;
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

  static final String REVIEW_FAILED = "The review could not be written. See the engine log.";

  private static final Logger LOG = Logger.getLogger(SessionAssistant.class.getName());
  private static final JsonMapper JSON = JsonMapper.builder().build();

  private final Sessions sessions;
  private final SessionStore store;
  private final AssistantStore records;
  private final AiClient ai;
  private final Executor reviews;
  private final Set<String> reviewing = ConcurrentHashMap.newKeySet();
  private final Lock lock;

  /**
   * {@code lock} is the engine's store lock: it is held to read and write the session, and released
   * while the model answers, so a slow reply never blocks the rest of the engine.
   */
  public SessionAssistant(
      Sessions sessions, Database db, AiClient ai, Executor reviews, Lock lock) {
    this.sessions = sessions;
    this.store = new SessionStore(db);
    this.records = new AssistantStore(db);
    this.ai = ai;
    this.reviews = reviews;
    this.lock = lock;
  }

  private <T> T locked(Supplier<T> work) {
    lock.lock();
    try {
      return work.get();
    } finally {
      lock.unlock();
    }
  }

  private void locked(Runnable work) {
    lock.lock();
    try {
      work.run();
    } finally {
      lock.unlock();
    }
  }

  public List<Turn> chat(String id) {
    return locked(
        () -> {
          sessions.context(id);
          return records.turns(id);
        });
  }

  public String ask(String id, String message, String rules, Consumer<String> deltas) {
    Context c = locked(() -> sessions.requireOpen(id));
    AiRequest request =
        locked(() -> PromptBuilder.ask(c, rules(c, rules), history(c, events(c)), message));
    AiReply reply = call("chat", c, () -> ai.chat(request, deltas));
    locked(() -> remember(c, message, reply.text(), reply));
    return reply.text();
  }

  public EditReply write(String id, String message, String rules) {
    record Prepared(Context c, String replyId, AiRequest request) {}
    Prepared p =
        locked(
            () -> {
              Context c = sessions.requireOpen(id);
              List<StoredEvent> events = events(c);
              String replyId = "w" + (count(events, "edit") + 1);
              return new Prepared(
                  c,
                  replyId,
                  PromptBuilder.write(c, rules(c, rules), history(c, events), message, replyId));
            });
    Context c = p.c();
    String replyId = p.replyId();
    AiReply reply = call("edit", c, () -> ai.edit(p.request(), d -> {}));
    return locked(
        () -> {
          EditReply applied = sessions.applyEdits(id, protocolLines(replyId, reply.text()));
          remember(c, message, summary(applied), reply);
          return applied;
        });
  }

  /** Practice only, and only for a level the candidate has already revealed. */
  public String explainHint(String id, int phase, Consumer<String> deltas) {
    Context c = locked(() -> sessions.requireOpen(id));
    if (c.session().type() != SessionType.PRACTICE) {
      throw new SessionException("Hints are for Practice sessions.");
    }
    List<String> revealed = locked(() -> sessions.revealedHints(id, phase));
    if (revealed.isEmpty()) {
      throw new SessionException("Reveal a hint for phase " + phase + " first.");
    }
    int level = revealed.size();
    AiRequest request =
        locked(
            () ->
                PromptBuilder.hint(
                    c, history(c, events(c)), phase, level, revealed.get(level - 1)));
    AiReply reply = call("hint", c, () -> ai.hintExplain(request, deltas));
    String asked = "Explain hint level " + level + " of phase " + phase + ".";
    locked(() -> remember(c, asked, reply.text(), reply));
    return reply.text();
  }

  /** Ends the session; the first finish starts the written review. */
  public SessionView finish(String id, boolean early) {
    return locked(
        () -> {
          boolean already = sessions.context(id).session().finished();
          SessionView view = sessions.finish(id, early);
          if (!already) {
            requestReview(id);
          }
          return view;
        });
  }

  /** Starts the written review in the background; at most one per session at a time. */
  public void requestReview(String id) {
    if (!reviewing.add(id)) {
      return;
    }
    reviews.execute(
        () -> {
          try {
            Context c = locked(() -> sessions.context(id));
            AiRequest request =
                locked(
                    () -> {
                      List<StoredEvent> events = events(c);
                      return PromptBuilder.feedback(c, history(c, events), events);
                    });
            AiReply reply = call("feedback", c, () -> ai.feedback(request, d -> {}));
            locked(() -> store(id, new Review("ready", reply.text(), null)));
          } catch (AiUnavailableException e) {
            locked(() -> store(id, new Review("unavailable", null, e.getMessage())));
          } catch (RuntimeException e) {
            // Stored, so the result page stops polling instead of starting a call every few
            // seconds.
            LOG.warning(() -> "review failed: " + e.getClass().getSimpleName());
            locked(() -> store(id, new Review("unavailable", null, REVIEW_FAILED)));
          } finally {
            reviewing.remove(id);
          }
        });
  }

  /**
   * The stored review. A finished session without one (an engine restart mid-review, or finished
   * before reviews existed) gets one requested here, so pending always means a review is running.
   */
  public Review review(String id) {
    boolean finished = locked(() -> sessions.context(id).session().finished());
    if (finished && locked(() -> records.review(id)) == null) {
      requestReview(id);
    }
    String stored = locked(() -> records.review(id));
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

  private List<StoredEvent> events(Context c) {
    return store.events(c.session().id());
  }

  private List<PromptBuilder.Entry> history(Context c, List<StoredEvent> events) {
    List<PromptBuilder.Entry> history = new ArrayList<>();
    for (Turn t : records.turns(c.session().id())) {
      history.add(PromptBuilder.Entry.turn(t.role(), t.text(), t.tMs()));
    }
    for (StoredEvent e : events) {
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
      locked(() -> event(c, data));
      LOG.info(() -> "aiCall " + data);
      return reply;
    } catch (RuntimeException e) {
      // Any failure, the route's or a closed client stream's, is still a call to account for.
      locked(() -> event(c, Map.of("kind", kind, "ok", false)));
      LOG.info(() -> "aiCall kind=" + kind + " ok=false");
      throw e;
    }
  }

  private void event(Context c, Map<String, Object> data) {
    sessions.event(c.session(), "aiCall", data);
  }

  private void store(String id, Review review) {
    records.setReview(id, JSON.writeValueAsString(review));
  }

  private void remember(Context c, String message, String answer, AiReply reply) {
    long now = sessions.activeMs(c.session(), events(c));
    records.appendTurn(c.session().id(), "user", message, null, now);
    records.appendTurn(c.session().id(), "assistant", answer, reply.usage().model(), now);
  }

  private static int count(List<StoredEvent> events, String kind) {
    return (int)
        events.stream()
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
}
