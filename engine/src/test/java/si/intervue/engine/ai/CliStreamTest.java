package si.intervue.engine.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Recorded stream-json lines (docs/research/phase-1ab.md item 2) through the parser. */
class CliStreamTest {

  static final String INIT =
      "{\"type\":\"system\",\"subtype\":\"init\",\"model\":\"claude-sonnet-5-5\",\"effort\":\"low\"}";

  static String delta(String text) {
    return "{\"type\":\"stream_event\",\"event\":{\"type\":\"content_block_delta\",\"index\":0,"
        + "\"delta\":{\"type\":\"text_delta\",\"text\":\""
        + text
        + "\"}}}";
  }

  static String result(String text, boolean error) {
    return "{\"type\":\"result\",\"subtype\":\""
        + (error ? "error_during_execution" : "success")
        + "\",\"is_error\":"
        + error
        + ",\"result\":\""
        + text
        + "\",\"duration_ms\":1234,\"usage\":{\"input_tokens\":900,\"output_tokens\":42}}";
  }

  private final List<String> seen = new ArrayList<>();
  private final CliStream stream = new CliStream(seen::add);

  private CliStream.Result feed(int exitCode, String... lines) {
    for (String line : lines) {
      stream.accept(line);
    }
    return stream.finish(exitCode);
  }

  @Test
  void streamsDeltasAndReportsUsage() {
    CliStream.Result r =
        feed(
            0, INIT, delta("Look at "), delta("exposureAt."), result("Look at exposureAt.", false));

    assertThat(seen).containsExactly("Look at ", "exposureAt.");
    assertThat(r.text()).isEqualTo("Look at exposureAt.");
    assertThat(r.model()).isEqualTo("claude-sonnet-5-5");
    assertThat(r.inputTokens()).isEqualTo(900);
    assertThat(r.outputTokens()).isEqualTo(42);
    assertThat(r.durationMs()).isEqualTo(1234);
  }

  @Test
  void aReplyWithoutDeltasComesFromTheResult() {
    CliStream.Result r = feed(0, INIT, result("Whole reply.", false));

    assertThat(r.text()).isEqualTo("Whole reply.");
    assertThat(seen).containsExactly("Whole reply.");
  }

  @Test
  void unparsableAndUnknownLinesAreIgnored() {
    CliStream.Result r =
        feed(0, "not json", "{\"type\":\"something_new\"}", delta("ok"), result("ok", false));

    assertThat(r.text()).isEqualTo("ok");
  }

  @Test
  void aSignedOutRunIsReportedAsSignedOut() {
    String retry =
        "{\"type\":\"system\",\"subtype\":\"api_retry\",\"error\":\"authentication_failed\"}";

    assertThatThrownBy(() -> feed(1, INIT, retry, result("Invalid API key xyz", true)))
        .isInstanceOf(AiUnavailableException.class)
        .hasMessageContaining("signed out")
        .hasMessageNotContaining("xyz");
  }

  @Test
  void aRejectedRateLimitIsReportedAsAUsageLimit() {
    String limit = "{\"type\":\"rate_limit_event\",\"rate_limit_info\":{\"status\":\"rejected\"}}";

    assertThatThrownBy(() -> feed(1, INIT, limit, result("limit", true)))
        .isInstanceOf(AiUnavailableException.class)
        .hasMessageContaining("usage limit");
  }

  @Test
  void anyOtherFailureNamesTheExitCodeOnly() {
    assertThatThrownBy(() -> feed(5, INIT, result("stack trace with secrets", true)))
        .isInstanceOf(AiUnavailableException.class)
        .hasMessageContaining("exit code 5")
        .hasMessageNotContaining("secrets");
  }
}
