package si.intervue.engine.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** The CLI route against FakeClaude.java: the exact command and stdin, never a model. */
class CliModelClientTest {

  @TempDir Path dir;

  private CliModelClient client(String scenario, long timeoutMs) {
    return new CliModelClient(new ClaudeCli(FakeCli.command(scenario, dir)), timeoutMs);
  }

  private void recorded(String... lines) throws IOException {
    Files.writeString(dir.resolve("stream.jsonl"), String.join("\n", lines) + "\n");
  }

  private List<String> args() throws IOException {
    return Files.readAllLines(dir.resolve("args.txt"));
  }

  @Test
  void streamsTheReplyAndReportsTheCliRoute() throws IOException {
    recorded(
        CliStreamTest.INIT,
        CliStreamTest.delta("Name "),
        CliStreamTest.delta("the line."),
        CliStreamTest.result("Name the line.", false));
    List<String> deltas = new ArrayList<>();

    AiReply reply = client("ok", 60_000).chat(new AiRequest("RULES", "PROMPT"), deltas::add);

    assertThat(deltas).containsExactly("Name ", "the line.");
    assertThat(reply.text()).isEqualTo("Name the line.");
    assertThat(reply.usage().route()).isEqualTo("cli");
    assertThat(reply.usage().model()).isEqualTo("claude-sonnet-5-5");
    assertThat(reply.usage().inputTokens()).isEqualTo(900);
  }

  @Test
  void sendsThePromptOnStdinAndTheRulesAsTheSystemPrompt() throws IOException {
    recorded(CliStreamTest.INIT, CliStreamTest.result("ok", false));

    client("ok", 60_000).edit(new AiRequest("RULES ₹ ✓", "PROMPT ₹ ✓"), d -> {});

    assertThat(Files.readString(dir.resolve("stdin.txt"), StandardCharsets.UTF_8))
        .isEqualTo("PROMPT ₹ ✓");
    assertThat(Files.readString(dir.resolve("system.txt"), StandardCharsets.UTF_8))
        .isEqualTo("RULES ₹ ✓");
    assertThat(args()).doesNotContain("PROMPT ₹ ✓");
  }

  @Test
  void runsTextOnlyWithTheResearchedFlags() throws IOException {
    recorded(CliStreamTest.INIT, CliStreamTest.result("ok", false));

    client("ok", 60_000).feedback(new AiRequest("R", "P"), d -> {});

    List<String> args = args();
    assertThat(args)
        .contains("-p", "--safe-mode", "--no-session-persistence", "--strict-mcp-config")
        .containsSubsequence("--model", "claude-sonnet-5-5")
        .containsSubsequence("--effort", "low")
        .containsSubsequence("--output-format", "stream-json")
        .containsSubsequence("--max-turns", "1")
        .doesNotContain("--bare");
    assertThat(args.get(args.indexOf("--tools") + 1)).isEmpty();
  }

  @Test
  void aFailedCallGivesTheMappedReason() throws IOException {
    recorded(CliStreamTest.INIT, CliStreamTest.result("raw internals", true));

    assertThatThrownBy(() -> client("ok", 60_000).chat(new AiRequest("R", "P"), d -> {}))
        .isInstanceOf(AiUnavailableException.class)
        .hasMessageContaining("exit code 1")
        .hasMessageNotContaining("raw internals");
  }

  @Test
  void aCallThatNeverAnswersIsStopped() throws IOException {
    recorded(CliStreamTest.INIT, CliStreamTest.result("late", false));

    assertThatThrownBy(() -> client("hang", 3_000).chat(new AiRequest("R", "P"), d -> {}))
        .isInstanceOf(AiUnavailableException.class)
        .hasMessageContaining("did not answer");
  }

  @Test
  void statusComesFromTheProbe() {
    assertThat(client("ok", 60_000).status().available()).isTrue();
    assertThat(client("signed-out", 60_000).status().reason()).contains("signed out");
  }
}
