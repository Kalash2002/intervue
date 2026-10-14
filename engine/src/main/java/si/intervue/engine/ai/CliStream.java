package si.intervue.engine.ai;

import java.util.function.Consumer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Reads the CLI's {@code --output-format stream-json} lines (docs/research/phase-1ab.md item 2):
 * text deltas go to the caller as they arrive; the final {@code result} line gives usage. Failures
 * become mapped reasons; the CLI's own error text is never shown or logged, since it can hold
 * anything.
 */
final class CliStream {

  /** The reply and what it cost. */
  record Result(String text, String model, long inputTokens, long outputTokens, long durationMs) {}

  private static final JsonMapper JSON = JsonMapper.builder().build();

  private final Consumer<String> deltas;
  private final StringBuilder text = new StringBuilder();
  private String model = "";
  private String resultText = "";
  private boolean failed;
  private boolean signedOut;
  private boolean usageLimit;
  private long inputTokens;
  private long outputTokens;
  private long durationMs;

  CliStream(Consumer<String> deltas) {
    this.deltas = deltas;
  }

  void accept(String line) {
    JsonNode msg;
    try {
      msg = JSON.readTree(line);
    } catch (JacksonException e) {
      return; // the CLI may print other lines; only its JSON events matter
    }
    switch (msg.path("type").asString("")) {
      case "system" -> system(msg);
      case "stream_event" -> streamEvent(msg.path("event"));
      case "rate_limit_event" ->
          usageLimit |= "rejected".equals(msg.path("rate_limit_info").path("status").asString(""));
      case "result" -> result(msg);
      default -> {
        // Unknown event types are ignored so a newer CLI does not break the route.
      }
    }
  }

  private void system(JsonNode msg) {
    switch (msg.path("subtype").asString("")) {
      case "init" -> model = msg.path("model").asString("");
      case "api_retry" -> {
        String error = msg.path("error").asString("");
        signedOut |= error.equals("authentication_failed");
        usageLimit |= error.equals("rate_limit") || error.equals("billing_error");
      }
      default -> {
        // Other system events carry nothing the engine uses.
      }
    }
  }

  private void streamEvent(JsonNode event) {
    JsonNode delta = event.path("delta");
    if (event.path("type").asString("").equals("content_block_delta")
        && delta.path("type").asString("").equals("text_delta")) {
      String chunk = delta.path("text").asString("");
      text.append(chunk);
      deltas.accept(chunk);
    }
  }

  private void result(JsonNode msg) {
    failed = msg.path("is_error").asBoolean(false);
    resultText = msg.path("result").asString("");
    inputTokens = msg.path("usage").path("input_tokens").asLong(0);
    outputTokens = msg.path("usage").path("output_tokens").asLong(0);
    durationMs = msg.path("duration_ms").asLong(0);
  }

  /** The reply once the process ended, or the mapped reason it failed. */
  Result finish(int exitCode) {
    if (failed || exitCode != 0) {
      throw new AiUnavailableException(reason(exitCode));
    }
    // Without partial messages the whole reply arrives only in the result line.
    if (text.isEmpty() && !resultText.isEmpty()) {
      text.append(resultText);
      deltas.accept(resultText);
    }
    return new Result(text.toString(), model, inputTokens, outputTokens, durationMs);
  }

  private String reason(int exitCode) {
    if (signedOut) {
      return "The Claude CLI is signed out. Run claude once in a terminal to sign in.";
    }
    if (usageLimit) {
      return "The Claude CLI hit its usage limit. Try again later.";
    }
    return "The Claude CLI failed (exit code " + exitCode + ").";
  }
}
