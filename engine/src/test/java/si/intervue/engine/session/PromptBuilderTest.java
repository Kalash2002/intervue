package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.ai.AiRequest;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.PromptBuilder.Rules;
import si.intervue.engine.session.edits.EditApplier;

/** Prompt assembly in the order of PLAN.md 2.5, from the section's prompt files. */
class PromptBuilderTest {

  @TempDir Path root;
  EngineFixture engine;
  Sessions sessions;

  @BeforeEach
  void setUp() {
    engine = new EngineFixture(root);
    sessions = engine.sessions;
  }

  @AfterEach
  void tearDown() {
    engine.close();
  }

  private Sessions.Context context(SessionType type) {
    return sessions.context(sessions.create("tiny", type).id());
  }

  private static String template(Sessions.Context c, String name) {
    return c.section().prompts().template(name);
  }

  @Test
  void theRulesStartWithTheSystemPromptForThisLanguage() {
    AiRequest r =
        PromptBuilder.ask(context(SessionType.INTERVIEW), Rules.INTERVIEW, List.of(), "hi");

    assertThat(r.rules()).contains("Plain Java 21").doesNotContain("{{");
  }

  @Test
  void interviewRulesOrHelpfulRulesAreChosen() {
    Sessions.Context c = context(SessionType.PRACTICE);

    String interview = PromptBuilder.ask(c, Rules.INTERVIEW, List.of(), "hi").rules();
    String helpful = PromptBuilder.ask(c, Rules.HELPFUL, List.of(), "hi").rules();

    assertThat(interview).contains(template(c, "interview-mode").strip());
    assertThat(interview).doesNotContain(template(c, "helpful-mode").strip());
    assertThat(helpful).contains(template(c, "helpful-mode").strip());
  }

  @Test
  void theUserTurnFollowsTheOrderOfPlan25() {
    Sessions.Context c = context(SessionType.INTERVIEW);

    String p = PromptBuilder.ask(c, Rules.INTERVIEW, List.of(), "MESSAGE-HERE").prompt();

    int brief = p.indexOf("# Brief");
    int readOnly = p.indexOf("# Read-only files");
    int editable = p.indexOf("# Editable files");
    int history = p.indexOf("# Conversation so far");
    int message = p.indexOf("MESSAGE-HERE");
    assertThat(List.of(brief, readOnly, editable, history, message)).isSorted().doesNotContain(-1);
    assertThat(p.substring(readOnly, editable)).contains("Phase1Tests.java");
    assertThat(p.substring(editable, history)).contains("Calc.java").doesNotContain("Phase1Tests");
  }

  @Test
  void editableFilesCarryTheHashTheApplierChecks() throws Exception {
    Sessions.Context c = context(SessionType.INTERVIEW);
    byte[] calc = Files.readAllBytes(c.work().root().resolve("Calc.java"));

    String p = PromptBuilder.write(c, Rules.INTERVIEW, List.of(), "add sum", "r1").prompt();

    assertThat(p).contains("Calc.java (base " + EditApplier.hash(calc) + ")");
  }

  @Test
  void lockedPhaseBriefsAreLeftOut() throws Exception {
    Sessions.Context c = context(SessionType.INTERVIEW);
    String phase2 =
        Files.readString(c.problem().dir().resolve("brief/phase2.md"), StandardCharsets.UTF_8);

    String p = PromptBuilder.ask(c, Rules.INTERVIEW, List.of(), "hi").prompt();

    assertThat(p).doesNotContain(phase2.strip());
  }

  @Test
  void historyKeepsTurnsAndSummarisesEachPastEditInOneLine() {
    Sessions.Context c = context(SessionType.INTERVIEW);
    List<PromptBuilder.Entry> history =
        List.of(
            PromptBuilder.Entry.turn("user", "explain twice", 1_000),
            PromptBuilder.Entry.edit(
                new StoredEvent(1, 2_000, "aiEdit", Map.of("id", "r1:e1", "path", "Calc.java"))),
            PromptBuilder.Entry.edit(
                new StoredEvent(2, 3_000, "aiEditUndo", Map.of("id", "r1:e1"))),
            PromptBuilder.Entry.turn("assistant", "It doubles n.", 4_000));

    String p = PromptBuilder.ask(c, Rules.INTERVIEW, history, "next").prompt();
    String h = p.substring(p.indexOf("# Conversation so far"), p.indexOf("next"));

    assertThat(h).contains("Candidate: explain twice").contains("Assistant: It doubles n.");
    assertThat(h.lines().filter(l -> l.contains("r1:e1"))).hasSize(2);
    assertThat(h.indexOf("explain twice")).isLessThan(h.indexOf("r1:e1"));
  }

  @Test
  void theWriteCodeChannelAddsItsProtocolAndTheReplyId() {
    Sessions.Context c = context(SessionType.INTERVIEW);

    AiRequest r = PromptBuilder.write(c, Rules.INTERVIEW, List.of(), "add sum", "reply-7");

    assertThat(r.rules()).contains("\"replyId\":\"reply-7\"").contains("\"t\":\"edit\"");
    assertThat(r.rules()).doesNotContain("{{");
  }

  @Test
  void aHintExplanationCarriesOnlyTheRevealedLevel() {
    Sessions.Context c = context(SessionType.PRACTICE);

    AiRequest r = PromptBuilder.hint(c, List.of(), 1, 1, "Phase 1 where.");

    assertThat(r.rules()).contains("Phase 1 where.").doesNotContain("{{");
    assertThat(r.rules() + r.prompt()).doesNotContain("Phase 1 how.").doesNotContain("steps.");
  }

  @Test
  void theReviewUsesTheFeedbackPromptAndSeesEditAndHintEvents() {
    Sessions.Context c = context(SessionType.PRACTICE);
    List<StoredEvent> events =
        List.of(
            new StoredEvent(1, 60_000, "hint", Map.of("phase", 1, "level", 2)),
            new StoredEvent(2, 90_000, "aiEdit", Map.of("id", "r1:e1", "path", "Calc.java")));

    AiRequest r = PromptBuilder.feedback(c, List.of(), events);

    assertThat(r.rules()).contains("## Verdict").contains("## Follow-up answers");
    assertThat(r.rules()).doesNotContain("{{");
    assertThat(r.prompt()).contains("1:00 hint").contains("1:30 aiEdit");
    // The task is in the section's feedback prompt; no request text is written in code.
    assertThat(r.prompt()).doesNotContain("# Message");
  }

  @Test
  void aHintExplanationTakesItsTaskFromTheHintPromptOnly() {
    AiRequest r = PromptBuilder.hint(context(SessionType.PRACTICE), List.of(), 1, 1, "Look here.");

    assertThat(r.rules()).contains("Look here.");
    assertThat(r.prompt()).doesNotContain("# Message");
  }
}
