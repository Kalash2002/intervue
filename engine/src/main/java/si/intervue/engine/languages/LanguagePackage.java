package si.intervue.engine.languages;

import java.util.List;
import si.intervue.engine.health.HealthCheck;

/**
 * What a language package provides so the engine can run its problems without knowing the language
 * (docs/contracts/language-contract.md). Implementations register in {@code
 * META-INF/services/si.intervue.engine.languages.LanguagePackage}.
 */
public interface LanguagePackage {

  /** The SPI version this engine speaks; a package declaring another version is refused. */
  int SPI_VERSION = 1;

  LanguageDefinition definition();

  Runner runner();

  TestKit testKit();

  CalibrationBenchmark benchmark();

  /** Phase 1a: runtime intact, compile-and-run sample, encoding; the engine adds calibration. */
  List<HealthCheck> healthChecks();

  EditorSupport editor();
}
