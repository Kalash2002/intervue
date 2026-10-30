package si.intervue.engine.sections;

import java.util.List;
import java.util.Set;
import si.intervue.engine.health.HealthCheck;

/**
 * What a section package provides: how one kind of interview round is checked, graded, scored and
 * shown (docs/contracts/section-contract.md). Implementations register in {@code
 * META-INF/services/si.intervue.engine.sections.SectionPackage}.
 */
public interface SectionPackage {

  /** The SPI version this engine speaks; a package declaring another version is refused. */
  int SPI_VERSION = 1;

  SectionDefinition definition();

  ContentSchema schema();

  Grader grader();

  ScoringRule scoring();

  /** What the result page shows, in order. */
  List<ResultPart> resultParts();

  List<HealthCheck> healthChecks();

  PromptTemplates prompts();

  Set<AssistantChannel> channels();

  /** When true, Interview sessions may reveal hints too (Practice always may). */
  default boolean hintsInInterview() {
    return false;
  }

  /** When true, the schema requires {@code hints/phase<N>.md} for every phase. */
  boolean supportsHints();
}
