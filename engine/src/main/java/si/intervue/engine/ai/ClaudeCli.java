package si.intervue.engine.ai;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import si.intervue.engine.runners.ProcessControl;

/**
 * Finds the Claude Code CLI and checks it is signed in without spending tokens ({@code --version},
 * then {@code auth status}; docs/research/phase-1ab.md item 6). Developer mode only (PLAN.md 2.5):
 * the composition root is the only code that constructs it.
 */
public final class ClaudeCli {

  private static final long PROBE_MS = 20_000;
  private static final String NPM_EXE = "node_modules/@anthropic-ai/claude-code/bin/claude";

  private final List<String> command;
  private final ProcessControl processes = new ProcessControl();

  /** {@code command} starts the CLI: its path, or a launcher prefix such as a test double. */
  public ClaudeCli(List<String> command) {
    this.command = List.copyOf(command);
  }

  /** The CLI on this machine, or empty: {@code INTERVUE_CLAUDE}, then PATH. */
  public static Optional<ClaudeCli> find() {
    boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    return locate(System.getenv("INTERVUE_CLAUDE"), System.getenv("PATH"), windows)
        .map(exe -> new ClaudeCli(List.of(exe.toString())));
  }

  /**
   * The native executable to run. An npm install puts a shell shim on PATH; the native binary it
   * launches sits beside it, and running that directly keeps cmd.exe from re-parsing arguments.
   */
  static Optional<Path> locate(String override, String pathVar, boolean windows) {
    if (override != null && !override.isBlank()) {
      return Optional.of(Path.of(override));
    }
    String exe = windows ? "claude.exe" : "claude";
    for (String entry : pathVar == null ? new String[0] : pathVar.split(File.pathSeparator)) {
      if (entry.isBlank()) {
        continue;
      }
      Path dir = Path.of(entry.replace("\"", ""));
      Path npm = dir.resolve(NPM_EXE + (windows ? ".exe" : ""));
      if (Files.isRegularFile(npm)) {
        return Optional.of(npm);
      }
      if (Files.isRegularFile(dir.resolve(exe))) {
        return Optional.of(dir.resolve(exe));
      }
    }
    return Optional.empty();
  }

  public static final String NOT_FOUND =
      "The Claude CLI was not found. Install Claude Code, then restart Intervue.";
  public static final String SIGNED_OUT =
      "The Claude CLI is signed out. Run claude once in a terminal to sign in.";

  /** Found, starts, and signed in; reasons are mapped categories, never the CLI's own output. */
  public AiStatus probe() {
    ProcessControl.Result version;
    try {
      version = run("--version");
    } catch (UncheckedIOException e) {
      return unavailable(NOT_FOUND);
    }
    if (version.timedOut() || version.exitCode() != 0) {
      return unavailable("The Claude CLI did not start (exit code " + version.exitCode() + ").");
    }
    ProcessControl.Result auth = run("auth", "status");
    if (auth.exitCode() != 0
        || !auth.output().replaceAll("\\s", "").contains("\"loggedIn\":true")) {
      return unavailable(SIGNED_OUT);
    }
    return new AiStatus(true, true, null);
  }

  List<String> command(String... args) {
    List<String> full = new ArrayList<>(command);
    full.addAll(List.of(args));
    return full;
  }

  private ProcessControl.Result run(String... args) {
    return processes.run(
        command(args), Path.of(System.getProperty("java.io.tmpdir")), PROBE_MS, 64_000);
  }

  private static AiStatus unavailable(String reason) {
    return new AiStatus(true, false, reason);
  }
}
