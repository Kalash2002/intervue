package si.intervue.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.runners.ProcessControl;

/**
 * Each test writes a tiny two-phase problem whose phase 2 limit is 1 s, then breaks one rule. Phase
 * 1 checks {@code f}; phase 2 checks {@code g}, which busy-waits for a chosen time.
 */
class ProblemVerifierTest {

  private static final String CORRECT_F = "n * 2";
  private static final String BUGGY_F = "n + 1";
  private static final String HINT =
      "## Level 1\nWhere.\n\n## Level 2\nHow.\n\n## Level 3\nSteps.\n";
  private static final String WALKTHROUGH =
      "# Walkthrough\n\n## DSA reference\n\n### Theory\n\ntext\n\n### Code\n\ncode\n\n"
          + "### Complexity\n\ntable\n\n### Practise the technique\n\n- list\n\n"
          + "## Time budget\n\ntable\n\n## Follow-up answers\n\ntext\n";

  @TempDir Path root;

  private VerifyReport verify(
      String starterF, String referenceF, long referenceG, String naiveF, long naiveG)
      throws IOException {
    Path dir = root.resolve("problems").resolve("tiny");
    writeProblem(dir);
    Path variant = dir.resolve("variants/java");
    write(variant.resolve("starter/Calc.java"), calc(starterF, 0));
    write(variant.resolve("reference/Calc.java"), calc(referenceF, referenceG));
    write(variant.resolve("naive/Calc.java"), calc(naiveF, naiveG));
    return new ProblemVerifier(root.resolve("scratch"), 1.0, new ProcessControl()).verify(dir);
  }

  private static String calc(String fBody, long gDelayMs) {
    return """
        public class Calc {
          static int f(int n) {
            return %s;
          }

          static long g(long n) {
            long end = System.nanoTime() + %dL * 1_000_000L;
            while (System.nanoTime() < end) {
              Thread.onSpinWait();
            }
            return n;
          }
        }
        """
        .formatted(fBody, gDelayMs);
  }

  private static void writeProblem(Path dir) throws IOException {
    write(
        dir.resolve("problem.json"),
        """
        {"formatVersion":1,"id":"tiny","version":1,"section":"coding-round","title":"Tiny",
         "summary":"Two phases.","difficulty":"Easy","techniques":["arrays"],"domain":"test",
         "minutes":10,"followUps":[],"languages":["java"],
         "phases":[
          {"id":1,"title":"Fix","brief":"brief/phase1.md","hints":"hints/phase1.md","weight":20,"allOrNothing":true,"timeoutRefMs":20000},
          {"id":2,"title":"Fast","brief":"brief/phase2.md","hints":"hints/phase2.md","weight":50,"allOrNothing":false,"timeoutRefMs":20000}]}
        """);
    write(
        dir.resolve("variants/java/variant.json"),
        """
        {"language":"java","minToolchain":"21","main":"Calc",
         "readOnly":["Phase1Tests.java","Phase2Tests.java"],
         "tests":{"1":"Phase1Tests","2":"Phase2Tests"}}
        """);
    write(
        dir.resolve("variants/java/starter/Phase1Tests.java"),
        """
        import kit.Check;
        import kit.Test;

        public class Phase1Tests {
          @Test(order = 1, timeoutMs = 2000)
          void doubles() {
            Check.assertEquals(4, Calc.f(2));
          }
        }
        """);
    write(
        dir.resolve("variants/java/starter/Phase2Tests.java"),
        """
        import kit.Check;
        import kit.Test;

        public class Phase2Tests {
          @Test(order = 1, timeoutMs = 1000)
          void isFast() {
            Check.assertEquals(1000L, Calc.g(1000));
          }
        }
        """);
    for (String file :
        new String[] {"brief/intro.md", "brief/phase1.md", "brief/phase2.md", "grading/NOTES.md"}) {
      write(dir.resolve(file), "text");
    }
    write(dir.resolve("walkthrough.md"), WALKTHROUGH);
    write(dir.resolve("hints/phase1.md"), HINT);
    write(dir.resolve("hints/phase2.md"), HINT);
  }

  private static void write(Path file, String text) throws IOException {
    Files.createDirectories(file.getParent());
    Files.writeString(file, text);
  }

  @Test
  void acceptsAProblemThatFollowsEveryRule() throws IOException {
    VerifyReport report = verify(BUGGY_F, CORRECT_F, 0, CORRECT_F, 10_000);

    assertThat(report.ok()).as(String.join("\n", report.lines())).isTrue();
  }

  @Test
  void refusesAStarterThatAlreadyPassesPhaseOne() throws IOException {
    VerifyReport report = verify(CORRECT_F, CORRECT_F, 0, CORRECT_F, 10_000);

    assertThat(report.ok()).isFalse();
    assertThat(report.lines()).anyMatch(l -> l.contains("starter") && l.contains("FAIL"));
  }

  @Test
  void refusesAReferenceThatFailsATest() throws IOException {
    VerifyReport report = verify(BUGGY_F, BUGGY_F, 0, CORRECT_F, 10_000);

    assertThat(report.lines()).anyMatch(l -> l.contains("reference") && l.contains("FAIL"));
  }

  @Test
  void refusesANaiveSolutionThatDoesNotTimeOut() throws IOException {
    VerifyReport report = verify(BUGGY_F, CORRECT_F, 0, CORRECT_F, 0);

    assertThat(report.lines()).anyMatch(l -> l.contains("naive") && l.contains("FAIL"));
  }

  @Test
  void refusesANaiveSolutionThatFailsAnEarlierPhase() throws IOException {
    VerifyReport report = verify(BUGGY_F, CORRECT_F, 0, BUGGY_F, 10_000);

    assertThat(report.lines()).anyMatch(l -> l.contains("naive") && l.contains("FAIL"));
  }

  @Test
  void refusesANaiveSolutionWithinThreeTimesTheLimit() throws IOException {
    VerifyReport report = verify(BUGGY_F, CORRECT_F, 0, CORRECT_F, 1_800);

    assertThat(report.ok()).isFalse();
    assertThat(report.lines()).anyMatch(l -> l.contains("3x"));
  }

  @Test
  void refusesAReferenceWithoutFiveTimesHeadroom() throws IOException {
    VerifyReport report = verify(BUGGY_F, CORRECT_F, 300, CORRECT_F, 10_000);

    assertThat(report.ok()).isFalse();
    assertThat(report.lines()).anyMatch(l -> l.contains("5x"));
  }

  @Test
  void refusesAProblemThatBreaksTheFormatBeforeRunningAnything() throws IOException {
    Path dir = root.resolve("problems").resolve("tiny");
    writeProblem(dir);
    Files.delete(dir.resolve("hints/phase2.md"));

    VerifyReport report =
        new ProblemVerifier(root.resolve("scratch"), 1.0, new ProcessControl()).verify(dir);

    assertThat(report.ok()).isFalse();
    assertThat(report.lines()).anyMatch(l -> l.contains("hints/phase2.md"));
  }
}
