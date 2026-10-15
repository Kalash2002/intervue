package si.intervue.languages.java;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import si.intervue.engine.health.HealthCheck;
import si.intervue.engine.health.HealthResult;
import si.intervue.engine.health.HealthResult.Status;

/** The Java package's Doctor checks, run against the real JDK. */
class JavaHealthChecksTest {

  private final Map<String, HealthCheck> checks =
      new JavaLanguagePackage()
          .healthChecks().stream().collect(Collectors.toMap(HealthCheck::id, c -> c));

  @Test
  void theRuntimeThatRunsTheEngineIsIntact() {
    HealthResult result = checks.get("java.runtime").run();

    assertThat(result.status()).isEqualTo(Status.PASS);
    assertThat(result.measured()).isEqualTo(System.getProperty("java.version"));
  }

  @Test
  void theSampleCompilesAndRuns() {
    HealthResult result = checks.get("java.sample").run();

    assertThat(result.status()).as(result.message()).isEqualTo(Status.PASS);
    assertThat(result.measured()).endsWith(" ms");
  }

  @Test
  void utf8TextAndCrlfSourcesComeBackByteExact() {
    HealthResult result = checks.get("java.encoding").run();

    assertThat(result.status()).as(result.message()).isEqualTo(Status.PASS);
  }

  @Test
  void runtimeAndSampleAreBlockingAndEncodingIsAWarning() {
    assertThat(checks.get("java.runtime").blocking()).isTrue();
    assertThat(checks.get("java.runtime").fast()).isTrue();
    assertThat(checks.get("java.sample").blocking()).isTrue();
    assertThat(checks.get("java.encoding").blocking()).isFalse();
  }
}
