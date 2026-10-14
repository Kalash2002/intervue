package si.intervue.engine.languages;

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

  EditorSupport editor();
}
