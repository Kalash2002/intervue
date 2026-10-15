package si.intervue.sections.codinground;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;
import si.intervue.engine.sections.AssistantChannel;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.engine.sections.SectionPackage;
import si.intervue.problemformat.Phase;
import si.intervue.problemformat.Problem;
import si.intervue.problemformat.ProblemFolder;
import si.intervue.problemformat.Variant;
import si.intervue.runprotocol.RunResults;
import si.intervue.runprotocol.Suite;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

class CodingRoundSectionTest {

  private final SectionPackage section = new CodingRoundSection();

  private static Problem problem(String sectionId) {
    List<Phase> phases =
        List.of(
            new Phase(1, "Fix", "brief/phase1.md", "hints/phase1.md", 20, true, 30_000),
            new Phase(2, "Build", "brief/phase2.md", "hints/phase2.md", 50, false, 30_000));
    return new Problem(
        1,
        "p",
        1,
        sectionId,
        "P",
        "s",
        "Easy",
        List.of("x"),
        List.of(),
        "d",
        60,
        phases,
        List.of(),
        List.of("java"));
  }

  private static final Variant VARIANT =
      new Variant(
          "java",
          "21",
          "Main",
          List.of(),
          Map.of("1", "Phase1Tests", "2", "Phase2Tests"),
          null,
          null,
          null);

  private static TestResult test(String suite, String name, TestStatus status) {
    return new TestResult(suite, name, status, 1, null, null, 1000L);
  }

  @Test
  void isFoundByServiceLoaderAndSpeaksTheCurrentSpi() {
    assertThat(ServiceLoader.load(SectionPackage.class))
        .extracting(s -> s.definition().id(), s -> s.definition().spiVersion())
        .contains(tuple("coding-round", SectionPackage.SPI_VERSION));
  }

  @Test
  void offersBothChannelsAndRequiresHints() {
    assertThat(section.channels())
        .containsExactlyInAnyOrder(AssistantChannel.ASK, AssistantChannel.WRITE_CODE);
    assertThat(section.supportsHints()).isTrue();
  }

  @Test
  void shipsEveryPromptTemplate() {
    for (String name :
        List.of(
            "system", "interview-mode", "helpful-mode", "write-code", "hint-explain", "feedback")) {
      assertThat(section.prompts().template(name)).as(name).isNotBlank();
    }
    assertThat(section.prompts().template("write-code")).contains("\"protocol\":1");
  }

  @Test
  void theSchemaRefusesAProblemOfAnotherSection() {
    ProblemFolder other = new ProblemFolder(Path.of("p"), problem("system-design"), Map.of());
    ProblemFolder own = new ProblemFolder(Path.of("p"), problem("coding-round"), Map.of());

    assertThat(section.schema().validate(other)).isNotEmpty();
    assertThat(section.schema().validate(own)).isEmpty();
  }

  @Test
  void gradesEachPhaseFromItsTestClass() {
    RunResults results =
        new RunResults(
            "java-kit/2.0",
            "java",
            List.of(new Suite("Phase1Tests", 2), new Suite("Phase2Tests", 4)),
            List.of(
                test("Phase1Tests", "a", TestStatus.PASS),
                test("Phase1Tests", "b", TestStatus.PASS),
                test("Phase2Tests", "c", TestStatus.FAIL),
                test("Phase2Tests", "d", TestStatus.ERROR),
                test("Phase2Tests", "e", TestStatus.TIMEOUT)),
            Optional.empty());

    List<PhaseResult> graded = section.grader().grade(problem("coding-round"), VARIANT, results);

    assertThat(graded)
        .containsExactly(
            new PhaseResult(1, "Fix", 20, true, 2, 0, 0, 0),
            new PhaseResult(2, "Build", 50, false, 0, 2, 1, 1));
    assertThat(graded.get(0).green()).isTrue();
  }

  @Test
  void aRunThatDidNotCompileGivesNoPasses() {
    List<PhaseResult> graded = section.grader().grade(problem("coding-round"), VARIANT, null);

    assertThat(graded).allMatch(p -> p.total() == 0 && !p.green());
  }

  @Test
  void listsTheResultPageInOrder() {
    assertThat(section.resultParts())
        .extracting(p -> p.id())
        .containsExactly("score", "phases", "hints", "edits", "walkthrough", "follow-ups", "notes");
  }
}
