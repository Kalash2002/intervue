package si.intervue.sections.dsaquick;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;
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

class DsaQuickSectionTest {

  private final SectionPackage section = new DsaQuickSection();

  private static Problem problem(String sectionId, int phaseCount) {
    List<Phase> phases =
        phaseCount == 1
            ? List.of(new Phase(1, "Solve", "statement.md", "hints/phase1.md", 80, true, 30_000))
            : List.of(
                new Phase(1, "Solve", "statement.md", "hints/phase1.md", 40, true, 30_000),
                new Phase(2, "More", "statement.md", "hints/phase2.md", 40, true, 30_000));
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
        20,
        phases,
        List.of(),
        List.of("java"),
        List.of());
  }

  private static final Variant VARIANT =
      new Variant("java", "21", "Main", List.of(), Map.of("1", "Phase1Tests"), null, null, null);

  private static TestResult test(String name, TestStatus status) {
    return new TestResult("Phase1Tests", name, status, 1, null, null, 1000L);
  }

  private static ProblemFolder folder(String sectionId, int phases) {
    return new ProblemFolder(Path.of("p"), problem(sectionId, phases), Map.of());
  }

  @Test
  void isFoundByServiceLoader() {
    assertThat(ServiceLoader.load(SectionPackage.class))
        .extracting(s -> s.definition().id(), s -> s.definition().spiVersion())
        .contains(tuple("dsa-quick", SectionPackage.SPI_VERSION));
    assertThat(section.definition().name()).isEqualTo("DSA");
  }

  @Test
  void offersNoChannelsAndHintsInInterview() {
    assertThat(section.channels()).isEmpty();
    assertThat(section.hintsInInterview()).isTrue();
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
  void theSchemaRefusesAnotherSectionAndMorePhases() {
    assertThat(section.schema().validate(folder("dsa-quick", 1))).isEmpty();
    assertThat(section.schema().validate(folder("coding-round", 1)))
        .anyMatch(m -> m.contains("dsa-quick"));
    assertThat(section.schema().validate(folder("dsa-quick", 2)))
        .anyMatch(m -> m.contains("one phase"));
  }

  @Test
  void theSchemaRequiresMainToBeMain() {
    Variant bad = new Variant("java", "21", "Solution", List.of(), Map.of(), null, null, null);
    ProblemFolder f = folder("dsa-quick", 1);

    assertThat(
            section.schema().validate(new ProblemFolder(f.dir(), f.problem(), Map.of("java", bad))))
        .anyMatch(m -> m.contains("\"main\": \"Main\""));
  }

  @Test
  void gradesTheSinglePhaseFromItsTestClass() {
    RunResults results =
        new RunResults(
            "java-kit/2.0",
            "java",
            List.of(new Suite("Phase1Tests", 4)),
            List.of(
                test("a", TestStatus.PASS),
                test("b", TestStatus.FAIL),
                test("c", TestStatus.TIMEOUT)),
            Optional.empty());

    assertThat(section.grader().grade(problem("dsa-quick", 1), VARIANT, results))
        .containsExactly(new PhaseResult(1, "Solve", 80, true, 1, 1, 1, 1));
  }

  @Test
  void aRunThatDidNotCompileGivesNoPasses() {
    assertThat(section.grader().grade(problem("dsa-quick", 1), VARIANT, null))
        .allMatch(p -> p.total() == 0 && !p.green());
  }

  @Test
  void listsTheResultPageInOrder() {
    assertThat(section.resultParts())
        .extracting(p -> p.id())
        .containsExactly("score", "tests", "time", "walkthrough");
  }
}
