package si.intervue.languages.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.runprotocol.RunProtocolParser;
import si.intervue.runprotocol.RunResults;
import si.intervue.runprotocol.Summary;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

/** Runs the real kit in real JVMs against fixture test classes and reads its results file. */
class TestKitTest {

  private static final List<String> FIXTURES =
      List.of(
          "MixedTests", "SlowTests", "ScaledTests", "ExitTests", "NoisyTests", "BrokenInitTests");

  @TempDir static Path work;
  static Path classes;

  @BeforeAll
  static void compileKitAndFixtures() throws IOException {
    classes = Files.createDirectories(work.resolve("out"));
    List<String> sources = new ArrayList<>();
    try (Stream<Path> kit = Files.list(Path.of(System.getProperty("intervue.testkit"), "kit"))) {
      kit.forEach(p -> sources.add(p.toString()));
    }
    for (String fixture : FIXTURES) {
      Path source = work.resolve(fixture + ".java");
      try (InputStream in =
          TestKitTest.class.getResourceAsStream("/kit-fixtures/" + source.getFileName())) {
        Files.copy(in, source);
      }
      sources.add(source.toString());
    }
    List<String> args = new ArrayList<>(List.of("-encoding", "UTF-8", "-d", classes.toString()));
    args.addAll(sources);
    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    assertThat(javac.run(null, null, null, args.toArray(String[]::new))).isZero();
  }

  private static RunResults run(String factor, String... testClasses) throws Exception {
    Path results = work.resolve("results-" + System.nanoTime() + ".jsonl");
    List<String> command =
        new ArrayList<>(
            List.of(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Dfile.encoding=UTF-8",
                "-cp",
                classes.toString(),
                "kit.TestRunner",
                "--results",
                results.toString(),
                "--factor",
                factor));
    command.addAll(List.of(testClasses));
    Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
    process.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
    assertThat(process.waitFor(60, TimeUnit.SECONDS)).as("kit finished").isTrue();
    return RunProtocolParser.parse(results);
  }

  @Test
  void reportsPassAndFailuresWithDetailWhileIgnoringUserOutput() throws Exception {
    RunResults results = run("1.0", "MixedTests");

    assertThat(results.runner()).startsWith("java-kit/");
    assertThat(results.language()).isEqualTo("java");
    assertThat(results.tests())
        .extracting(TestResult::name, TestResult::status)
        .containsExactly(
            tuple("passes", TestStatus.PASS),
            tuple("failsAnAssertion", TestStatus.FAIL),
            tuple("throwsFromCandidateCode", TestStatus.FAIL));
    assertThat(results.tests().get(1).detail()).isEqualTo("total: expected <5> but was <4>");
    TestResult thrown = results.tests().get(2);
    assertThat(thrown.detail()).startsWith("NullPointerException");
    assertThat(thrown.at()).contains("MixedTests.throwsFromCandidateCode");
    assertThat(results.summary())
        .contains(new Summary(1, 2, 0, false, results.summary().get().wallMs()));
  }

  @Test
  void aTimeoutEndsThatJvmAndTheRestContinueInAFreshOne() throws Exception {
    long start = System.nanoTime();
    RunResults results = run("1.0", "SlowTests");

    assertThat(results.tests())
        .extracting(TestResult::name, TestResult::status, TestResult::limitMs)
        .containsExactly(
            tuple("sleepsPastItsLimit", TestStatus.TIMEOUT, 300L),
            tuple("runsAfterTheTimeoutInAFreshJvm", TestStatus.PASS, 2000L));
    assertThat(TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start)).isLessThan(30);
  }

  @Test
  void theCalibrationFactorScalesEveryLimit() throws Exception {
    assertThat(run("1.0", "ScaledTests").tests().get(0).status()).isEqualTo(TestStatus.TIMEOUT);
    TestResult scaled = run("2.0", "ScaledTests").tests().get(0);
    assertThat(scaled.status()).isEqualTo(TestStatus.PASS);
  }

  @Test
  void aJvmThatDiesOnItsOwnIsAHarnessError() throws Exception {
    assertThat(run("1.0", "ExitTests").tests())
        .extracting(TestResult::status)
        .containsExactly(TestStatus.ERROR);
  }

  @Test
  void aMissingTestClassIsAHarnessErrorAndTheRunStillCompletes() throws Exception {
    RunResults results = run("1.0", "NoSuchTests", "MixedTests");

    assertThat(results.tests().get(0).status()).isEqualTo(TestStatus.ERROR);
    assertThat(results.tests()).hasSize(4);
    assertThat(results.summary()).isPresent();
  }

  @Test
  void candidateOutputCannotChangeTheCounts() throws Exception {
    RunResults results = run("1.0", "NoisyTests");

    assertThat(results.tests())
        .extracting(TestResult::name, TestResult::status)
        .containsExactly(
            tuple("printsWithoutNewline", TestStatus.PASS),
            tuple("printsAFakeFailLine", TestStatus.PASS),
            tuple("plainPass", TestStatus.PASS));
    assertThat(results.summary().get().passed()).isEqualTo(3);
    assertThat(results.summary().get().failed()).isZero();
  }

  @Test
  void aClassThatFailsToInitialiseIsAnErrorAndLaterSuitesStillRun() throws Exception {
    RunResults results = run("1.0", "BrokenInitTests", "NoisyTests");

    assertThat(results.tests())
        .filteredOn(t -> t.suite().equals("BrokenInitTests"))
        .extracting(TestResult::status)
        .containsOnly(TestStatus.ERROR);
    assertThat(results.tests())
        .filteredOn(t -> t.suite().equals("NoisyTests"))
        .extracting(TestResult::status)
        .containsOnly(TestStatus.PASS);
    assertThat(results.summary()).isPresent();
  }
}
