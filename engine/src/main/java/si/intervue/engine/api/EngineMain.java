package si.intervue.engine.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.HexFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Starts the engine: {@code java -jar engine.jar --data <dir> --content <dir> [--port <n>]}. The
 * shell passes the token in {@code INTERVUE_ENGINE_TOKEN}; the only line on stdout is the ready
 * line, which carries the port and never the token.
 */
public final class EngineMain {

  public static final String READY = "INTERVUE_ENGINE_READY port=";
  private static final long HEARTBEAT_SECONDS = 15;

  private EngineMain() {}

  /**
   * Command-line arguments; {@code problems} is the {@code problems} folder of the content. With
   * {@code exitOnStdinClose} the engine shuts down cleanly when its parent closes stdin. {@code
   * developerMode} enables the developer-only Claude CLI route (PLAN.md 2.5); release shells never
   * pass it.
   */
  record Args(Path data, Path problems, int port, boolean exitOnStdinClose, boolean developerMode) {
    static Args parse(String[] argv) {
      Path data = null;
      Path content = null;
      int port = 0;
      boolean exitOnStdinClose = false;
      boolean developerMode = false;
      for (int i = 0; i < argv.length; i++) {
        if (argv[i].equals("--exit-on-stdin-close")) {
          exitOnStdinClose = true;
          continue;
        }
        if (argv[i].equals("--developer-mode")) {
          developerMode = true;
          continue;
        }
        if (i + 1 >= argv.length) {
          throw new IllegalArgumentException("Missing value for " + argv[i]);
        }
        switch (argv[i]) {
          case "--data" -> data = Path.of(argv[++i]);
          case "--content" -> content = Path.of(argv[++i]);
          case "--port" -> port = Integer.parseInt(argv[++i]);
          default -> throw new IllegalArgumentException("Unknown argument " + argv[i]);
        }
      }
      if (data == null || content == null) {
        throw new IllegalArgumentException(
            "Usage: --data <dir> --content <dir> [--port <n>] [--exit-on-stdin-close]"
                + " [--developer-mode]");
      }
      return new Args(data, content.resolve("problems"), port, exitOnStdinClose, developerMode);
    }
  }

  public static void main(String[] argv) {
    Args args = Args.parse(argv);
    String token = token(System.getenv("INTERVUE_ENGINE_TOKEN"), args.data());
    Engine engine =
        Engine.open(args.data(), args.problems(), Clock.systemUTC(), args.developerMode());
    engine.sessions().onStartup();
    LocalApi api = LocalApi.start(engine, token, args.port());
    ScheduledExecutorService heartbeat =
        Executors.newSingleThreadScheduledExecutor(
            Thread.ofPlatform().name("heartbeat").daemon().factory());
    heartbeat.scheduleAtFixedRate(
        () -> engine.locked(() -> engine.sessions().heartbeat()),
        HEARTBEAT_SECONDS,
        HEARTBEAT_SECONDS,
        TimeUnit.SECONDS);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  heartbeat.shutdownNow();
                  api.stop();
                  // A run still going (an endless main, a slow test JVM) must not outlive us.
                  ProcessHandle.current().descendants().forEach(ProcessHandle::destroyForcibly);
                  engine.locked(() -> engine.sessions().onShutdown());
                  engine.close();
                }));
    System.out.println(READY + api.port());
    System.out.flush();
    if (args.exitOnStdinClose()) {
      exitWhenStdinCloses();
    }
  }

  // The shell can only kill a process outright on Windows; closing our stdin asks us to stop, so
  // the shutdown hook pauses sessions and ends child processes first.
  private static void exitWhenStdinCloses() {
    Thread.ofPlatform()
        .name("stdin-watch")
        .daemon()
        .start(
            () -> {
              try {
                System.in.transferTo(OutputStream.nullOutputStream());
              } catch (IOException e) {
                // A broken pipe means the parent is gone, which is the same signal.
              }
              System.exit(0);
            });
  }

  /**
   * The shell's token, or, in browser dev mode where no shell exists, a fresh one written to {@code
   * <data>/dev-token} for the Vite proxy.
   */
  static String token(String fromShell, Path dataDir) {
    if (fromShell != null && !fromShell.isEmpty()) {
      return fromShell;
    }
    byte[] bytes = new byte[32];
    new SecureRandom().nextBytes(bytes);
    String token = HexFormat.of().formatHex(bytes);
    try {
      Files.createDirectories(dataDir);
      Files.writeString(dataDir.resolve("dev-token"), token);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return token;
  }
}
