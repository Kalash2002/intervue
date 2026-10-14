package si.intervue.engine.api;

import java.io.IOException;
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

  /** Command-line arguments; {@code problems} is the {@code problems} folder of the content. */
  record Args(Path data, Path problems, int port) {
    static Args parse(String[] argv) {
      Path data = null;
      Path content = null;
      int port = 0;
      for (int i = 0; i < argv.length; i += 2) {
        if (i + 1 >= argv.length) {
          throw new IllegalArgumentException("Missing value for " + argv[i]);
        }
        switch (argv[i]) {
          case "--data" -> data = Path.of(argv[i + 1]);
          case "--content" -> content = Path.of(argv[i + 1]);
          case "--port" -> port = Integer.parseInt(argv[i + 1]);
          default -> throw new IllegalArgumentException("Unknown argument " + argv[i]);
        }
      }
      if (data == null || content == null) {
        throw new IllegalArgumentException("Usage: --data <dir> --content <dir> [--port <n>]");
      }
      return new Args(data, content.resolve("problems"), port);
    }
  }

  public static void main(String[] argv) {
    Args args = Args.parse(argv);
    String token = token(System.getenv("INTERVUE_ENGINE_TOKEN"), args.data());
    Engine engine = Engine.open(args.data(), args.problems(), Clock.systemUTC());
    engine.sessions().onStartup();
    LocalApi api = LocalApi.start(engine, token, args.port());
    ScheduledExecutorService heartbeat =
        Executors.newSingleThreadScheduledExecutor(
            Thread.ofPlatform().name("heartbeat").daemon().factory());
    heartbeat.scheduleAtFixedRate(
        engine.sessions()::heartbeat, HEARTBEAT_SECONDS, HEARTBEAT_SECONDS, TimeUnit.SECONDS);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  heartbeat.shutdownNow();
                  api.stop();
                  engine.sessions().onShutdown();
                  engine.close();
                }));
    System.out.println(READY + api.port());
    System.out.flush();
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
