package si.intervue.languages.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import si.intervue.engine.languages.CalibrationBenchmark;
import si.intervue.engine.languages.EditorSupport;
import si.intervue.engine.languages.LanguageDefinition;
import si.intervue.engine.languages.LanguagePackage;
import si.intervue.engine.languages.Runner;
import si.intervue.engine.languages.TestKit;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * The Java language package: compiles with the bundled JDK's compiler and runs tests through the
 * Java kit. Its identity and calibration reference come from {@code language.json} in the jar.
 */
public final class JavaLanguagePackage implements LanguagePackage {

  /** The parts of language.json this package reads. */
  private record LanguageJson(
      String id,
      String displayName,
      int spiVersion,
      List<String> extensions,
      Calibration calibration) {
    record Calibration(long referenceMs) {}
  }

  private final LanguageDefinition definition;
  private final JavaRunner runner = new JavaRunner();
  private final JavaTestKit testKit = new JavaTestKit();
  private final JavaBenchmark benchmark;
  private final JavaEditorSupport editor = new JavaEditorSupport();

  public JavaLanguagePackage() {
    LanguageJson json = readLanguageJson();
    definition =
        new LanguageDefinition(json.id(), json.displayName(), json.spiVersion(), json.extensions());
    benchmark = new JavaBenchmark(json.calibration().referenceMs());
  }

  private static LanguageJson readLanguageJson() {
    try (InputStream in = JavaLanguagePackage.class.getResourceAsStream("/language.json")) {
      if (in == null) {
        throw new IllegalStateException("language.json is missing from the Java package jar");
      }
      return JsonMapper.builder()
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .build()
          .readValue(in, LanguageJson.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public LanguageDefinition definition() {
    return definition;
  }

  @Override
  public Runner runner() {
    return runner;
  }

  @Override
  public TestKit testKit() {
    return testKit;
  }

  @Override
  public CalibrationBenchmark benchmark() {
    return benchmark;
  }

  @Override
  public EditorSupport editor() {
    return editor;
  }
}
