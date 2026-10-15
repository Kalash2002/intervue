// Grades the single phase of a DSA question by the tests of its test class in one run.
// Must not score; DsaScore does.
// ponytail: copied from the coding round's TestsGrader because sections may not import each other;
// lift into the SPI if a third section needs it.

package si.intervue.sections.dsaquick;

import java.util.ArrayList;
import java.util.List;
import si.intervue.engine.sections.Grader;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.problemformat.Phase;
import si.intervue.problemformat.Problem;
import si.intervue.problemformat.Variant;
import si.intervue.runprotocol.RunResults;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

/** One-phase grader. */
final class DsaGrader implements Grader {

  @Override
  public List<PhaseResult> grade(Problem problem, Variant variant, RunResults results) {
    List<PhaseResult> graded = new ArrayList<>();
    for (Phase phase : problem.phases()) {
      String suite = variant.tests().get(String.valueOf(phase.id()));
      int passed = 0;
      int failed = 0;
      int timeouts = 0;
      if (results != null) {
        for (TestResult test : results.tests()) {
          if (!test.suite().equals(suite)) {
            continue;
          }
          passed += test.status() == TestStatus.PASS ? 1 : 0;
          timeouts += test.status() == TestStatus.TIMEOUT ? 1 : 0;
          failed += test.status() == TestStatus.FAIL || test.status() == TestStatus.ERROR ? 1 : 0;
        }
      }
      int notRun = results == null ? 0 : results.notRun(suite);
      graded.add(
          new PhaseResult(
              phase.id(),
              phase.title(),
              phase.weight(),
              phase.allOrNothing(),
              passed,
              failed,
              timeouts,
              notRun));
    }
    return graded;
  }
}
