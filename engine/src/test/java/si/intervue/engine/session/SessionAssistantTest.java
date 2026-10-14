package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.ai.AiUnavailableException;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.edits.EditApplier;
import si.intervue.engine.session.edits.EditStatus;

class SessionAssistantTest {

  @TempDir Path root;
  EngineFixture engine;
  Sessions sessions;
  RecordedAi ai;
  SessionAssistant assistant;

  @BeforeEach
  void setUp() {
    engine = new EngineFixture(root);
    sessions = engine.sessions;
    ai = new RecordedAi();
    assistant = new SessionAssistant(sessions, engine.db, ai, Runnable::run, new ReentrantLock());
  }

  @AfterEach
  void tearDown() {
    engine.close();
  }

  private String start(SessionType type) {
    return sessions.create("tiny", type).id();
  }

  private String calcHash(String id) throws Exception {
    return EditApplier.hash(Files.readAllBytes(engine.work(id).resolve("Calc.java")));
  }

  @Test
  void askStreamsTheReplyAndKeepsBothTurns() {
    String id = start(SessionType.INTERVIEW);
    ai.reply("Look at twice in Calc.java line 3.");
    List<String> deltas = new ArrayList<>();

    String reply = assistant.ask(id, "fix it", SessionAssistant.HELPFUL, deltas::add);

    assertThat(String.join("", deltas))
        .isEqualTo(reply)
        .isEqualTo("Look at twice in Calc.java line 3.");
    assertThat(assistant.chat(id))
        .extracting(SessionAssistant.Turn::role, SessionAssistant.Turn::text)
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple("user", "fix it"),
            org.assertj.core.groups.Tuple.tuple("assistant", reply));
  }

  @Test
  void anInterviewAlwaysUsesInterviewRules() {
    String id = start(SessionType.INTERVIEW);
    ai.reply("ok");

    assistant.ask(id, "hi", SessionAssistant.HELPFUL, d -> {});

    assertThat(ai.last().request().rules()).contains("Interview mode");
  }

  @Test
  void practiceIsHelpfulByDefaultAndInterviewRulesAreSelectable() {
    String id = start(SessionType.PRACTICE);
    ai.reply("a").reply("b");

    assistant.ask(id, "hi", SessionAssistant.HELPFUL, d -> {});
    String helpful = ai.last().request().rules();
    assistant.ask(id, "hi", SessionAssistant.INTERVIEW, d -> {});

    assertThat(helpful).contains("Helpful mode").doesNotContain("Interview mode");
    assertThat(ai.last().request().rules()).contains("Interview mode");
  }

  @Test
  void everyCallLeavesAnAiCallEventWithUsageButNoText() {
    String id = start(SessionType.INTERVIEW);
    ai.reply("secret reply text");

    assistant.ask(id, "secret question", SessionAssistant.HELPFUL, d -> {});

    var event =
        sessions.view(id).events().stream().filter(e -> e.type().equals("aiCall")).findFirst();
    assertThat(event).isPresent();
    assertThat(event.get().data())
        .containsEntry("kind", "chat")
        .containsEntry("route", "recorded")
        .containsEntry("inputTokens", 100)
        .containsEntry("ok", true);
    assertThat(event.get().data().toString()).doesNotContain("secret");
  }

  @Test
  void writeCodeAppliesTheModelsEditsThroughTheApplier() throws Exception {
    String id = start(SessionType.INTERVIEW);
    ai.reply(
        "```json\n"
            + "{\"t\":\"note\",\"text\":\"Twice should not add one.\"}\n"
            + "{\"t\":\"edit\",\"id\":\"e1\",\"path\":\"Calc.java\",\"op\":\"replace\",\"base\":\""
            + calcHash(id)
            + "\",\"search\":\"return n + n + 1;\",\"replace\":\"return n + n;\"}\n"
            + "{\"t\":\"done\",\"edits\":1}\n```");

    var reply =
        assistant.write(id, "I'll drop the + 1 in twice. Write it.", SessionAssistant.HELPFUL);

    assertThat(reply.note()).isEqualTo("Twice should not add one.");
    assertThat(reply.outcomes()).extracting(o -> o.status()).containsExactly(EditStatus.APPLIED);
    assertThat(sessions.readFile(id, "Calc.java")).contains("return n + n;");
    assertThat(ai.last().request().rules()).contains("\"t\":\"edit\"");
  }

  @Test
  void aReplyWithNoEditsChangesNothingAndShowsItsNote() {
    String id = start(SessionType.INTERVIEW);
    String before = sessions.readFile(id, "Calc.java");
    ai.reply(
        "{\"t\":\"note\",\"text\":\"In Calc.java, twice adds one too many.\"}\n{\"t\":\"done\",\"edits\":0}");

    var reply = assistant.write(id, "fix it", SessionAssistant.HELPFUL);

    assertThat(reply.outcomes()).isEmpty();
    assertThat(reply.note()).contains("twice adds one too many");
    assertThat(sessions.readFile(id, "Calc.java")).isEqualTo(before);
    assertThat(assistant.chat(id).get(1).text()).contains("twice adds one too many");
  }

  @Test
  void hintExplanationsArePracticeOnlyAndForRevealedLevels() {
    String interview = start(SessionType.INTERVIEW);
    String practice = start(SessionType.PRACTICE);

    assertThatThrownBy(() -> assistant.explainHint(interview, 1, d -> {}))
        .isInstanceOf(SessionException.class);
    assertThatThrownBy(() -> assistant.explainHint(practice, 1, d -> {}))
        .isInstanceOf(SessionException.class)
        .hasMessageContaining("Reveal");

    sessions.hint(practice, 1);
    ai.reply("Look where the total is built.");
    assertThat(assistant.explainHint(practice, 1, d -> {}))
        .isEqualTo("Look where the total is built.");
    assertThat(ai.last().request().rules())
        .contains("Phase 1 where.")
        .doesNotContain("Phase 1 how.");
  }

  @Test
  void anUnavailableRouteStartsNothingAndKeepsNoTurns() {
    String id = start(SessionType.INTERVIEW);
    ai.unavailable("The Claude CLI is signed out.");

    assertThatThrownBy(() -> assistant.ask(id, "hi", SessionAssistant.HELPFUL, d -> {}))
        .isInstanceOf(AiUnavailableException.class)
        .hasMessageContaining("signed out");
    assertThat(assistant.chat(id)).isEmpty();
  }

  @Test
  void finishingRequestsAReviewBuiltFromTheFeedbackPromptAndTheTimeline() {
    String id = start(SessionType.PRACTICE);
    sessions.hint(id, 1);
    ai.reply("## Verdict\nHire.");

    sessions.finish(id);
    assistant.requestReview(id);

    assertThat(assistant.review(id).status()).isEqualTo("ready");
    assertThat(assistant.review(id).text()).startsWith("## Verdict");
    assertThat(ai.last().kind()).isEqualTo("feedback");
    assertThat(ai.last().request().rules()).contains("## Two changes for next time");
    assertThat(ai.last().request().prompt()).contains(" hint ");
  }

  @Test
  void finishingTwiceAsksForOneReview() {
    String id = start(SessionType.INTERVIEW);
    ai.reply("## Verdict\nHire.");

    assistant.finish(id, false);
    assistant.finish(id, false);

    assertThat(ai.calls).hasSize(1);
    assertThat(assistant.review(id).status()).isEqualTo("ready");
  }

  @Test
  void aFinishedSessionWithNoReviewGetsOneWhenTheReviewIsRead() {
    String id = start(SessionType.INTERVIEW);
    sessions.finish(id);
    ai.reply("## Verdict\nHire.");

    assertThat(assistant.review(id).status()).isEqualTo("ready");
    assertThat(ai.last().kind()).isEqualTo("feedback");
  }

  @Test
  void anOpenSessionHasNoReviewAndAsksForNone() {
    String id = start(SessionType.INTERVIEW);

    assertThat(assistant.review(id).status()).isEqualTo("pending");
    assertThat(ai.calls).isEmpty();
  }

  @Test
  void aReviewWithNoRouteSaysWhy() {
    String id = start(SessionType.INTERVIEW);
    ai.unavailable("Assistant unavailable: sign-in arrives in Phase 1b");
    sessions.finish(id);

    assistant.requestReview(id);

    assertThat(assistant.review(id).status()).isEqualTo("unavailable");
    assertThat(assistant.review(id).reason()).contains("Phase 1b");
  }
}
