package si.intervue.engine.health;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.languages.CalibrationBenchmark;
import si.intervue.engine.languages.LanguagePackage;
import si.intervue.engine.languages.LanguageRegistry;
import si.intervue.engine.runners.Calibration;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.SectionRegistry;
import si.intervue.engine.store.Settings;

/**
 * The Phase 1a Doctor checks owned by the engine (OS, data folder, local port, calibration per
 * language) and the collection of every installed package's own checks. PLAN.md 6.4 assigns the
 * first three to "the shell"; the shell holds no logic, so the engine runs them.
 */
public final class EngineChecks {

  private static final long MIN_FREE_BYTES = 1L << 30;

  private EngineChecks() {}

  /** Engine checks first, then each language's own checks and its calibration, then sections. */
  public static List<HealthCheck> all(
      Path dataDir,
      Settings settings,
      LanguageRegistry languages,
      SectionRegistry sections,
      ProcessControl processes) {
    HealthCheck os =
        os(
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"));
    List<HealthCheck> checks = new ArrayList<>(List.of(os, dataFolder(dataDir), port()));
    for (LanguagePackage pkg : languages.all()) {
      checks.addAll(pkg.healthChecks());
      checks.add(calibration(pkg.definition().id(), pkg.benchmark(), settings, processes));
    }
    sections.all().forEach(s -> checks.addAll(s.healthChecks()));
    return checks;
  }

  /** Windows 10 and later or macOS 13 and later, on x64 or arm64 (the support matrix). */
  static HealthCheck os(String name, String version, String arch) {
    String machine = name + " " + version + " (" + arch + ")";
    return new HealthCheck(
        "os",
        "Operating system",
        true,
        true,
        () ->
            supported(name, version, arch)
                ? HealthResult.pass(machine, machine)
                : HealthResult.fail(
                    "Intervue supports Windows 10 version 2004 and later and macOS 13 and later,"
                        + " on x64 or arm64. This machine: "
                        + machine,
                    machine));
  }

  // Java reports "10.0" for both Windows 10 and 11 and cannot see the 2004 build number.
  private static boolean supported(String name, String version, String arch) {
    String os = name.toLowerCase(Locale.ROOT);
    boolean cpu = List.of("amd64", "x86_64", "aarch64", "arm64").contains(arch);
    int major = majorVersion(version);
    return cpu && (os.startsWith("windows") ? major >= 10 : os.startsWith("mac") && major >= 13);
  }

  private static int majorVersion(String version) {
    try {
      return Integer.parseInt(version.split("\\.")[0]);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /** Creates and deletes a file in the data folder and asks for at least 1 GB free. */
  static HealthCheck dataFolder(Path dataDir) {
    return new HealthCheck(
        "data-folder",
        "Sessions folder writable",
        true,
        true,
        () -> {
          long free;
          try {
            Files.delete(Files.createTempFile(dataDir, "doctor", ".tmp"));
            free = Files.getFileStore(dataDir).getUsableSpace();
          } catch (IOException e) {
            return HealthResult.fail("Intervue cannot write to " + dataDir, e.toString());
          }
          String measured = (free >> 20) + " MB free";
          return free < MIN_FREE_BYTES
              ? HealthResult.fail("Only " + measured + "; Intervue needs 1 GB.", measured)
              : HealthResult.pass("Writable, " + measured, measured);
        });
  }

  /** Binds a loopback port; the UI's {@code GET /local/ping} proves the rest end to end. */
  static HealthCheck port() {
    return new HealthCheck(
        "port",
        "Local port free",
        true,
        true,
        () -> {
          try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            return HealthResult.pass("Local connection available", "port " + socket.getLocalPort());
          } catch (IOException e) {
            return HealthResult.fail(
                "A firewall or antivirus is blocking Intervue's local connection", e.getMessage());
          }
        });
  }

  /**
   * Developer mode only (PLAN.md 6.4): whether the assistant route can answer. A warning, because
   * sessions run without the assistant; fast, because it only asks the route, which never spends
   * tokens to answer.
   */
  public static HealthCheck assistant(Supplier<AiStatus> status) {
    return new HealthCheck(
        "ai.cli",
        "Claude CLI found and signed in",
        false,
        true,
        () -> {
          AiStatus s = status.get();
          return s.available()
              ? HealthResult.pass("The assistant can answer through the Claude CLI", "signed in")
              : HealthResult.warn(s.reason(), "unavailable");
        });
  }

  /** Measures the factor of PLAN.md 4.4 and stores it, so later runs scale their limits. */
  static HealthCheck calibration(
      String language,
      CalibrationBenchmark benchmark,
      Settings settings,
      ProcessControl processes) {
    return new HealthCheck(
        language + ".calibration",
        "Calibration (" + language + ")",
        false,
        false,
        () -> {
          double factor = Calibration.factor(benchmark, processes);
          settings.put("calibration." + language, String.valueOf(factor));
          String message =
              factor > Calibration.MIN_FACTOR
                  ? String.format(
                      Locale.ROOT,
                      "This machine is %.1fx slower than our reference; time limits are scaled"
                          + " accordingly",
                      factor)
                  : "This machine is at least as fast as our reference";
          return HealthResult.pass(message, String.format(Locale.ROOT, "factor %.2f", factor));
        });
  }
}
