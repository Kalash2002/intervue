package si.intervue.engine.health;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.health.HealthResult.Status;
import si.intervue.engine.languages.CalibrationBenchmark;
import si.intervue.engine.languages.LanguageRegistry;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.SectionRegistry;
import si.intervue.engine.store.Database;
import si.intervue.engine.store.Settings;

class EngineChecksTest {

  @TempDir Path root;

  private static Status os(String name, String version, String arch) {
    return EngineChecks.os(name, version, arch).run().status();
  }

  @Test
  void supportedSystemsPassAndOthersFail() {
    assertThat(os("Windows 11", "10.0", "amd64")).isEqualTo(Status.PASS);
    assertThat(os("Mac OS X", "14.4", "aarch64")).isEqualTo(Status.PASS);
    assertThat(os("Mac OS X", "13.0", "x86_64")).isEqualTo(Status.PASS);
    assertThat(os("Mac OS X", "12.7", "aarch64")).isEqualTo(Status.FAIL);
    assertThat(os("Windows 7", "6.1", "amd64")).isEqualTo(Status.FAIL);
    assertThat(os("Windows 11", "10.0", "x86")).isEqualTo(Status.FAIL);
    assertThat(os("Linux", "6.8", "amd64")).isEqualTo(Status.FAIL);
  }

  @Test
  void theOsMessageNamesThisMachine() {
    assertThat(EngineChecks.os("Linux", "6.8", "amd64").run().message()).contains("Linux 6.8");
  }

  @Test
  void aWritableDataFolderPassesAndLeavesNothingBehind() throws Exception {
    HealthResult result = EngineChecks.dataFolder(root).run();

    assertThat(result.status()).isEqualTo(Status.PASS);
    try (var files = Files.list(root)) {
      assertThat(files).isEmpty();
    }
  }

  @Test
  void aDataFolderThatIsNotAFolderFails() throws Exception {
    Path file = Files.writeString(root.resolve("not-a-folder"), "x");

    HealthResult result = EngineChecks.dataFolder(file).run();

    assertThat(result.status()).isEqualTo(Status.FAIL);
    assertThat(result.message()).contains("cannot write");
  }

  @Test
  void theLoopbackPortCheckBinds() {
    assertThat(EngineChecks.port().run().status()).isEqualTo(Status.PASS);
  }

  @Test
  void calibrationStoresTheFactorForTheLanguage() {
    CalibrationBenchmark bench =
        new CalibrationBenchmark() {
          @Override
          public long referenceMs() {
            return 1000;
          }

          @Override
          public long measureMs(ProcessControl processes) {
            return 1300;
          }
        };
    try (Database db = Database.open(root.resolve("intervue.db"))) {
      Settings settings = new Settings(db);

      HealthResult result =
          EngineChecks.calibration("java", bench, settings, new ProcessControl()).run();

      assertThat(result.status()).isEqualTo(Status.PASS);
      assertThat(result.measured()).isEqualTo("factor 1.30");
      assertThat(settings.calibrationFactor("java")).isEqualTo(1.3);
    }
  }

  @Test
  void collectsEngineChecksAndEveryInstalledPackagesChecks() {
    try (Database db = Database.open(root.resolve("intervue.db"))) {
      List<HealthCheck> checks =
          EngineChecks.all(
              root,
              new Settings(db),
              LanguageRegistry.load(),
              SectionRegistry.load(),
              new ProcessControl());

      assertThat(checks)
          .extracting(HealthCheck::id)
          .containsExactly(
              "os",
              "data-folder",
              "port",
              "java.runtime",
              "java.sample",
              "java.encoding",
              "java.calibration");
    }
  }

  @Test
  void theAssistantRouteCheckIsAWarningThatCarriesTheReason() {
    HealthCheck ok = EngineChecks.assistant(() -> new AiStatus(true, true, null));
    HealthCheck out =
        EngineChecks.assistant(() -> new AiStatus(true, false, "The Claude CLI is signed out."));

    assertThat(ok.id()).isEqualTo("ai.cli");
    assertThat(ok.blocking()).isFalse();
    assertThat(ok.run().status()).isEqualTo(Status.PASS);
    assertThat(out.run().status()).isEqualTo(Status.WARN);
    assertThat(out.run().message()).contains("signed out");
  }
}
