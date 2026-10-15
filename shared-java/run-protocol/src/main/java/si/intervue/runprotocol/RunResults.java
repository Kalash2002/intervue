package si.intervue.runprotocol;

import java.util.List;
import java.util.Optional;

/** Everything one run reported, in file order. */
public record RunResults(
    String runner,
    String language,
    List<Suite> suites,
    List<TestResult> tests,
    Optional<Summary> summary) {

  /** Tests a suite announced but never reported, for example because the runner was killed. */
  public int notRun(String suite) {
    int announced =
        suites.stream().filter(s -> s.name().equals(suite)).mapToInt(Suite::tests).sum();
    long reported = tests.stream().filter(t -> t.suite().equals(suite)).count();
    return (int) Math.max(0, announced - reported);
  }
}
