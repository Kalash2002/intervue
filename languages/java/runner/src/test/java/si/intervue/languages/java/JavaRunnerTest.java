package si.intervue.languages.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.languages.CompileResult;
import si.intervue.engine.languages.LanguagePackage;
import si.intervue.engine.languages.RunLimits;
import si.intervue.engine.languages.RunOutput;
import si.intervue.engine.languages.RunRequest;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.problemformat.Variant;
import si.intervue.runprotocol.RunProtocolParser;
import si.intervue.runprotocol.Suite;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

/** Runs the Java package against the real JDK on a small fixture project. */
class JavaRunnerTest {

  private static final List<String> FIXTURES =
      List.of("Calc", "Phase1Tests", "Phase2Tests", "Main", "Spin", "Chatty");
  private static final RunLimits LIMITS = new RunLimits(30_000, 256, 400_000);

  @TempDir Path session;
  private Path work;
  private Path out;
  private final JavaLanguagePackage java = new JavaLanguagePackage();
  private final ProcessControl processes = new ProcessControl();

  @BeforeEach
  void copyFixtureAndInstallKit() throws IOException {
    work = Files.createDirectories(session.resolve("work"));
    out = session.resolve("out");
    for (String name : FIXTURES) {
      try (InputStream in = getClass().getResourceAsStream("/javawork/" + name + ".java")) {
        Files.copy(in, work.resolve(name + ".java"));
      }
    }
    java.testKit().install(work);
  }

  private static Variant variant(String main) {
    return new Variant(
        "java",
        "21",
        main,
        List.of("Phase1Tests.java", "Phase2Tests.java"),
        Map.of("1", "Phase1Tests", "2", "Phase2Tests"),
        null,
        null,
        null);
  }

  private RunOutput run(String target, String main, RunLimits limits) {
    assertThat(java.runner().compile(work, out).ok()).isTrue();
    return java.runner()
        .run(new RunRequest(work, out, target, variant(main), limits, 1.0), processes);
  }

  @Test
  void isFoundByServiceLoaderAndSpeaksTheCurrentSpi() {
    assertThat(ServiceLoader.load(LanguagePackage.class))
        .extracting(p -> p.definition().id(), p -> p.definition().spiVersion())
        .contains(tuple("java", LanguagePackage.SPI_VERSION));
    assertThat(java.definition().extensions()).containsExactly(".java");
  }

  @Test
  void installsTheKitIntoTheWorkFolder() {
    assertThat(java.testKit().files()).allMatch(f -> Files.isRegularFile(work.resolve(f)));
    assertThat(java.testKit().files()).contains("kit/TestRunner.java");
  }

  @Test
  void reportsACompileErrorWithFileAndLine() throws IOException {
    Files.writeString(
        work.resolve("Broken.java"), "class Broken {\n  void m() {\n    int x = ;\n  }\n}\n");

    CompileResult result = java.runner().compile(work, out);

    assertThat(result.ok()).isFalse();
    assertThat(result.output()).startsWith("Compilation failed.").contains("Broken.java:3");
    assertThat(result.output()).doesNotContain(work.toString());
    assertThat(result.diagnostics())
        .anySatisfy(
            d -> {
              assertThat(d.path()).isEqualTo("Broken.java");
              assertThat(d.line()).isEqualTo(3);
              assertThat(d.severity()).isEqualTo("error");
            });
  }

  @Test
  void aPhaseTargetRunsOnlyThatPhase() {
    RunOutput output = run("phase:1", "Main", LIMITS);

    var results = RunProtocolParser.parse(output.resultsFile());
    assertThat(results.suites()).containsExactly(new Suite("Phase1Tests", 1));
    assertThat(results.tests()).extracting(TestResult::status).containsExactly(TestStatus.PASS);
  }

  @Test
  void theAllTargetRunsEveryPhaseInOrder() {
    var results = RunProtocolParser.parse(run("all", "Main", LIMITS).resultsFile());

    assertThat(results.suites())
        .extracting(Suite::name)
        .containsExactly("Phase1Tests", "Phase2Tests");
    assertThat(results.tests())
        .extracting(TestResult::status)
        .containsExactly(TestStatus.PASS, TestStatus.FAIL);
  }

  @Test
  void relativeWorkAndOutFoldersStillWork() throws IOException {
    // Relative to the test's working directory (the project), which may be on another drive than
    // the temp folder.
    Path base = Path.of("build", "relative-run-" + System.nanoTime());
    work = Files.createDirectories(base.resolve("work"));
    out = base.resolve("out");
    for (String name : FIXTURES) {
      Files.copy(session.resolve("work").resolve(name + ".java"), work.resolve(name + ".java"));
    }
    java.testKit().install(work);

    RunOutput output = run("phase:1", "Main", LIMITS);

    assertThat(RunProtocolParser.parse(output.resultsFile()).tests()).hasSize(1);
  }

  @Test
  void theMainTargetShowsProgramOutputAndHasNoResults() {
    RunOutput output = run("main", "Main", LIMITS);

    assertThat(output.output()).contains("hello from main");
    assertThat(output.resultsFile()).isNull();
    assertThat(output.timedOut()).isFalse();
  }

  @Test
  void theWallLimitStopsTheRunAndItsChildProcesses() {
    long start = System.nanoTime();
    RunOutput output = run("main", "Spin", new RunLimits(2_000, 256, 400_000));

    assertThat(output.timedOut()).isTrue();
    assertThat(output.output()).contains("Stopped");
    assertThat(TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start)).isLessThan(20);
    assertThat(ProcessHandle.allProcesses())
        .noneMatch(p -> p.info().commandLine().orElse("").contains("Spin$Child"));
  }

  @Test
  void outputBeyondTheCapIsCutWithANote() {
    RunOutput output = run("main", "Chatty", new RunLimits(30_000, 256, 10_000));

    assertThat(output.output()).contains("output cut off");
    assertThat(output.output().length()).isLessThan(12_000);
  }

  @Test
  void refusesAnUnknownTarget() {
    assertThatThrownBy(() -> run("phase:9", "Main", LIMITS))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> run("everything", "Main", LIMITS))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void theBenchmarkMeasuresThisMachine() {
    long ms = java.benchmark().measureMs(processes);

    assertThat(ms).isPositive();
    assertThat(java.benchmark().referenceMs()).isPositive();
  }
}
