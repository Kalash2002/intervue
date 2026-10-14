package si.intervue.sections.codinground;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.ai.AiClient;
import si.intervue.engine.ai.AiReply;
import si.intervue.engine.ai.AiRequest;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.ai.AiUsage;
import si.intervue.engine.content.PlainFolderSource;
import si.intervue.engine.languages.LanguageRegistry;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.SectionRegistry;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.SessionAssistant;
import si.intervue.engine.session.SessionStore;
import si.intervue.engine.session.Sessions;
import si.intervue.engine.session.edits.EditApplier;
import si.intervue.engine.session.edits.EditReply;
import si.intervue.engine.session.edits.EditStatus;
import si.intervue.engine.session.edits.EditStore;
import si.intervue.engine.store.Database;
import si.intervue.engine.store.Settings;

/**
 * One recorded transcript per point-and-explain rule (PLAN.md 2.8), on the real Hold Exposure
 * problem: a fixed message and a recorded model reply go through the real engine. Each test checks
 * that the prompt the model receives carries the rule, and what the engine applies and shows. No
 * model is called.
 */
class PointAndExplainTranscriptTest {

  @TempDir Path root;
  private Database db;
  private Sessions sessions;
  private SessionAssistant assistant;
  private final List<AiRequest> sent = new ArrayList<>();
  private String recorded = "";

  /** Replays {@link #recorded} and keeps every request. */
  private final AiClient model =
      new AiClient() {
        private AiReply reply(AiRequest r, Consumer<String> deltas) {
          sent.add(r);
          deltas.accept(recorded);
          return new AiReply(recorded, new AiUsage("recorded", "m", 1, 1, 1));
        }

        @Override
        public AiReply chat(AiRequest r, Consumer<String> d) {
          return reply(r, d);
        }

        @Override
        public AiReply edit(AiRequest r, Consumer<String> d) {
          return reply(r, d);
        }

        @Override
        public AiReply hintExplain(AiRequest r, Consumer<String> d) {
          return reply(r, d);
        }

        @Override
        public AiReply feedback(AiRequest r, Consumer<String> d) {
          return reply(r, d);
        }

        @Override
        public AiStatus status() {
          return new AiStatus(true, true, null);
        }
      };

  @BeforeEach
  void open() {
    db = Database.open(root.resolve("intervue.db"));
    Path problems = Path.of(System.getProperty("intervue.content"), "problems");
    sessions =
        new Sessions(
            new SessionStore(db),
            new EditStore(db),
            new Settings(db),
            new PlainFolderSource(problems),
            LanguageRegistry.load(),
            SectionRegistry.load(),
            root.resolve("sessions"),
            Clock.systemUTC(),
            new ProcessControl());
    assistant = new SessionAssistant(sessions, db, model, Runnable::run, new ReentrantLock());
  }

  @AfterEach
  void close() {
    db.close();
  }

  private String interview() {
    return sessions.create("hold-exposure", SessionType.INTERVIEW).id();
  }

  private String rules() {
    return sent.get(sent.size() - 1).rules();
  }

  @Test
  void rule1AskedToFixItPointsAtTheLineInWords() {
    String id = interview();
    String before = sessions.readFile(id, "ExposureCalculator.java");
    recorded =
        "Look at ExposureCalculator.exposureAt, line 15: the running total is declared as an int"
            + " while the amounts are longs, so large totals wrap around. Make the total the same"
            + " type as the amounts.";

    String shown = assistant.ask(id, "fix it", SessionAssistant.HELPFUL, d -> {});

    assertThat(rules()).contains("name the exact place (the file, the method and the line)");
    assertThat(rules()).contains("Write no solution code");
    assertThat(shown).isEqualTo(recorded).doesNotContain("```");
    assertThat(sessions.readFile(id, "ExposureCalculator.java")).isEqualTo(before);
  }

  @Test
  void rule2AskedToExplainItExplainsWithTheCandidatesCode() {
    String id = interview();
    recorded =
        "HoldBook.holdsFor walks every hold in the book and keeps those whose account matches"
            + " and that were not released, so it costs one pass over the whole book per call.";

    String shown = assistant.ask(id, "explain holdsFor", SessionAssistant.HELPFUL, d -> {});

    assertThat(rules()).contains("explain it fully, using the candidate's own code");
    assertThat(shown).isEqualTo(recorded);
    assertThat(assistant.chat(id)).extracting(SessionAssistant.Turn::text).contains(recorded);
  }

  @Test
  void rule3ASpecificQuestionGetsAnHonestYesOrNo() {
    String id = interview();
    recorded = "No. The total in exposureAt is an int, and the amounts it adds are longs.";

    String shown =
        assistant.ask(
            id, "is the total in exposureAt the right type?", SessionAssistant.HELPFUL, d -> {});

    assertThat(rules()).contains("answer it honestly, starting with a plain yes or no");
    assertThat(shown).startsWith("No.");
  }

  @Test
  void rule4NothingIsVolunteeredAndLaterPhasesAreNotEvenSent() throws Exception {
    String id = interview();
    recorded = "The total in exposureAt is an int.";
    String phase2 =
        Files.readString(
            Path.of(
                System.getProperty("intervue.content"), "problems/hold-exposure/brief/phase2.md"));

    assistant.ask(id, "what type is the total?", SessionAssistant.HELPFUL, d -> {});

    assertThat(rules()).contains("Never volunteer work the candidate did not ask about");
    // The model cannot volunteer what it was never shown: locked phase briefs stay out.
    assertThat(sent.get(0).prompt()).doesNotContain(phase2.strip());
  }

  @Test
  void writeCodeOnABareFixItAppliesNothingAndShowsWhereToLook() {
    String id = interview();
    String before = sessions.readFile(id, "ExposureCalculator.java");
    recorded =
        "{\"t\":\"note\",\"text\":\"Look at ExposureCalculator.exposureAt, line 15: the total is an"
            + " int. Tell me your approach and which method to write.\"}\n{\"t\":\"done\",\"edits\":0}";

    EditReply reply = assistant.write(id, "fix it", SessionAssistant.HELPFUL);

    assertThat(rules()).contains("make no edits");
    assertThat(reply.outcomes()).isEmpty();
    assertThat(reply.note()).contains("line 15");
    assertThat(sessions.readFile(id, "ExposureCalculator.java")).isEqualTo(before);
  }

  @Test
  void writeCodeAfterAnApproachEditsOnlyTheNamedMethod() throws Exception {
    String id = interview();
    String before = sessions.readFile(id, "ExposureCalculator.java");
    String base =
        EditApplier.hash(
            Files.readAllBytes(
                root.resolve("sessions").resolve(id).resolve("work/ExposureCalculator.java")));
    recorded =
        "{\"t\":\"note\",\"text\":\"Widening the total in exposureAt to long.\"}\n"
            + "{\"t\":\"edit\",\"id\":\"e1\",\"path\":\"ExposureCalculator.java\",\"op\":\"replace\","
            + "\"base\":\""
            + base
            + "\",\"search\":\"        int total = 0;\",\"replace\":\"        long total = 0;\"}\n"
            + "{\"t\":\"done\",\"edits\":1}";

    EditReply reply =
        assistant.write(
            id,
            "My approach: the total overflows, so widen it to long. Write that change in exposureAt.",
            SessionAssistant.HELPFUL);

    assertThat(rules()).contains("write only the piece the candidate named");
    assertThat(reply.outcomes()).extracting(o -> o.status()).containsExactly(EditStatus.APPLIED);
    String after = sessions.readFile(id, "ExposureCalculator.java");
    assertThat(after).contains("long total = 0;");
    assertThat(after.replace("long total = 0;", "int total = 0;")).isEqualTo(before);
  }

  @Test
  void practiceIsHelpfulByDefaultWithInterviewRulesSelectable() {
    String id = sessions.create("hold-exposure", SessionType.PRACTICE).id();
    recorded = "ok";

    assistant.ask(id, "hi", SessionAssistant.HELPFUL, d -> {});
    String helpful = rules();
    assistant.ask(id, "hi", SessionAssistant.INTERVIEW, d -> {});

    assertThat(helpful).contains("Helpful mode").doesNotContain("Interview mode is ON");
    assertThat(rules()).contains("Interview mode is ON");
  }

  @Test
  void aHintExplanationGoesNoFurtherThanTheRevealedLevel() {
    String id = sessions.create("hold-exposure", SessionType.PRACTICE).id();
    sessions.hint(id, 1);
    List<String> levels = sessions.revealedHints(id, 1);
    recorded = "The wrong number shows only for big accounts, so look at the type of the total.";

    String shown = assistant.explainHint(id, 1, d -> {});

    AiRequest request = sent.get(0);
    assertThat(levels).hasSize(1);
    assertThat(request.rules())
        .contains(levels.get(0).strip())
        .contains("Go no further than the hint");
    assertThat(request.rules() + request.prompt())
        .doesNotContain("compound assignment")
        .doesNotContain("Change its type");
    assertThat(shown).isEqualTo(recorded);
  }
}
