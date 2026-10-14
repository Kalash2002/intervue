package si.intervue.engine.content;

import java.util.List;
import si.intervue.problemformat.ProblemFolder;

/**
 * Where problems come from (PLAN.md 5.3): a plain folder in development, the free problems bundled
 * with the installer, and from Phase 1b decrypted packs. Every source offers only valid problems.
 */
public interface ContentStore {

  /** Ids of the valid problems this source offers, in a stable order. */
  List<String> ids();

  /** The problem, read and validated; {@link ContentException} if unknown or invalid. */
  ProblemFolder load(String id);
}
