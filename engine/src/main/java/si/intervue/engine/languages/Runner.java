package si.intervue.engine.languages;

import java.nio.file.Path;
import si.intervue.engine.runners.ProcessControl;

/** Compiles and runs a session's work folder for one language. */
public interface Runner {

  /** Compiles every source in {@code workDir}, including the installed kit, into {@code outDir}. */
  CompileResult compile(Path workDir, Path outDir);

  /**
   * Runs an already compiled target within the request's limits; test targets leave {@code
   * results.jsonl} in the out folder (docs/contracts/run-protocol.md).
   */
  RunOutput run(RunRequest request, ProcessControl processes);
}
