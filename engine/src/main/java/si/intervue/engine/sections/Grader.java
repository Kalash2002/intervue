package si.intervue.engine.sections;

import java.util.List;
import si.intervue.problemformat.Problem;
import si.intervue.problemformat.Variant;
import si.intervue.runprotocol.RunResults;

/** Turns one run's results into per-phase results. */
public interface Grader {

  /** {@code results} is null when the run did not compile; every phase then has no passes. */
  List<PhaseResult> grade(Problem problem, Variant variant, RunResults results);
}
