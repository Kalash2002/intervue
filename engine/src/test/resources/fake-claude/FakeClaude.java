import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Stands in for the Claude CLI in tests, so no test ever calls a model. Run as {@code java
 * FakeClaude.java <scenario> <dir> <cli args...>}: the scenario picks the behaviour, and {@code dir}
 * receives {@code args.txt} and {@code stdin.txt} so tests can check exactly what was sent.
 *
 * <p>Scenarios: {@code ok} (signed in; {@code -p} replays {@code <dir>/stream.jsonl}), {@code
 * signed-out}, {@code broken} (every command fails), {@code marker} (writes {@code <dir>/ran} and
 * exits; used to prove a route never starts the CLI).
 */
public class FakeClaude {
  public static void main(String[] argv) throws IOException {
    String scenario = argv[0];
    Path dir = Path.of(argv[1]);
    List<String> args = Arrays.asList(argv).subList(2, argv.length);
    Files.writeString(dir.resolve("args.txt"), String.join("\n", args), StandardCharsets.UTF_8);
    if (scenario.equals("marker")) {
      Files.writeString(dir.resolve("ran"), "the CLI was started");
      System.exit(0);
    }
    if (scenario.equals("broken")) {
      System.err.println("something went wrong inside the CLI");
      System.exit(3);
    }
    if (args.contains("--version")) {
      System.out.println("2.1.299 (Claude Code)");
      return;
    }
    if (args.size() >= 2 && args.get(0).equals("auth") && args.get(1).equals("status")) {
      boolean in = scenario.equals("ok");
      System.out.println("{\"loggedIn\":" + in + ",\"authMethod\":\"" + (in ? "claude.ai" : "none") + "\"}");
      System.exit(in ? 0 : 1);
    }
    if (args.contains("-p")) {
      byte[] prompt = System.in.readAllBytes();
      Files.write(dir.resolve("stdin.txt"), prompt);
      Path stream = dir.resolve("stream.jsonl");
      System.out.print(Files.readString(stream, StandardCharsets.UTF_8));
      System.out.flush();
      System.exit(Files.readString(stream).contains("\"is_error\":true") ? 1 : 0);
    }
    System.err.println("unexpected arguments: " + args);
    System.exit(2);
  }
}
