package si.intervue.engine.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;
import si.intervue.engine.runners.ProcessControl;

/**
 * The developer-only route (PLAN.md 2.5): one {@code claude -p} process per call, text only, with
 * the rules as the system prompt and the prompt on stdin. Flags are the researched set
 * (docs/research/phase-1ab.md item 5); it runs in an empty temp folder so no project config loads.
 */
public final class CliModelClient implements AiClient {

  public static final String MODEL = "claude-sonnet-5-5";
  public static final long TIMEOUT_MS = 240_000;

  // ponytail: a fixed one-minute cache; the probe starts two processes and the Assistant tab asks
  // on every open. A failed call drops it so the next status shows the new reason.
  private static final long STATUS_TTL_MS = 60_000;

  private final ClaudeCli cli;
  private final long timeoutMs;
  private volatile AiStatus known;
  private volatile long knownAt;

  public CliModelClient(ClaudeCli cli, long timeoutMs) {
    this.cli = cli;
    this.timeoutMs = timeoutMs;
  }

  @Override
  public AiReply chat(AiRequest request, Consumer<String> deltas) {
    return call(request, deltas);
  }

  @Override
  public AiReply edit(AiRequest request, Consumer<String> deltas) {
    return call(request, deltas);
  }

  @Override
  public AiReply hintExplain(AiRequest request, Consumer<String> deltas) {
    return call(request, deltas);
  }

  @Override
  public AiReply feedback(AiRequest request, Consumer<String> deltas) {
    return call(request, deltas);
  }

  @Override
  public AiStatus status() {
    AiStatus s = known;
    if (s == null || System.currentTimeMillis() - knownAt > STATUS_TTL_MS) {
      s = cli.probe();
      knownAt = System.currentTimeMillis();
      known = s;
    }
    return s;
  }

  private AiReply call(AiRequest request, Consumer<String> deltas) {
    try {
      return run(request, deltas);
    } catch (AiUnavailableException e) {
      known = null;
      throw e;
    }
  }

  private AiReply run(AiRequest request, Consumer<String> deltas) {
    Path dir = null;
    try {
      dir = Files.createTempDirectory("intervue-ai");
      Path rules =
          Files.writeString(dir.resolve("rules.md"), request.rules(), StandardCharsets.UTF_8);
      Process process =
          new ProcessBuilder(command(rules))
              .directory(dir.toFile())
              .redirectError(ProcessBuilder.Redirect.DISCARD)
              .start();
      AtomicBoolean timedOut = watch(process);
      try (OutputStream in = process.getOutputStream()) {
        in.write(request.prompt().getBytes(StandardCharsets.UTF_8));
      }
      CliStream stream = new CliStream(deltas);
      try (BufferedReader out =
          new BufferedReader(
              new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        out.lines().forEach(stream::accept);
      }
      int exit = process.waitFor();
      if (timedOut.get()) {
        throw new AiUnavailableException(
            "The Claude CLI did not answer within " + timeoutMs / 1000 + " seconds.");
      }
      CliStream.Result r = stream.finish(exit);
      String model = r.model().isEmpty() ? MODEL : r.model();
      return new AiReply(
          r.text(), new AiUsage("cli", model, r.inputTokens(), r.outputTokens(), r.durationMs()));
    } catch (IOException e) {
      throw new AiUnavailableException("The Claude CLI could not be started.");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AiUnavailableException("The call was interrupted.");
    } finally {
      deleteQuietly(dir);
    }
  }

  private List<String> command(Path rules) {
    return cli.command(
        "-p",
        "--output-format",
        "stream-json",
        "--verbose",
        "--include-partial-messages",
        "--model",
        MODEL,
        "--effort",
        "low",
        "--tools",
        "",
        "--disallowedTools",
        "mcp__*",
        "--strict-mcp-config",
        "--max-turns",
        "1",
        "--safe-mode",
        "--no-session-persistence",
        "--system-prompt-file",
        rules.toString());
  }

  /** Kills the process tree when the call runs past the timeout; returns whether it did. */
  private AtomicBoolean watch(Process process) {
    AtomicBoolean timedOut = new AtomicBoolean();
    Thread.ofPlatform()
        .daemon()
        .name("claude-watchdog")
        .start(() -> timedOut.set(!ProcessControl.waitOrKill(process, timeoutMs)));
    return timedOut;
  }

  private static void deleteQuietly(Path dir) {
    if (dir == null) {
      return;
    }
    try (Stream<Path> walk = Files.walk(dir)) {
      for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) {
        Files.deleteIfExists(p);
      }
    } catch (IOException | UncheckedIOException e) {
      // A leftover temp folder is harmless; the OS cleans its temp directory.
    }
  }
}
