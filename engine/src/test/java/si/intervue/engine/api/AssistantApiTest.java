package si.intervue.engine.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.ai.AiClient;
import si.intervue.engine.ai.AiReply;
import si.intervue.engine.ai.AiRequest;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.ai.AiUnavailableException;
import si.intervue.engine.ai.AiUsage;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** The assistant routes stream NDJSON; a slow model never blocks the rest of the API. */
class AssistantApiTest {

  private static final String TOKEN = "t";
  private static final JsonMapper JSON = JsonMapper.builder().build();

  @TempDir Path root;
  private Engine engine;
  private LocalApi api;
  private final HttpClient http = HttpClient.newHttpClient();
  private volatile long modelDelayMs;
  private volatile String unavailable;

  /** A model double: two words, optionally slow, optionally unavailable. */
  private final AiClient model =
      new AiClient() {
        private AiReply answer(Consumer<String> deltas, String text) {
          if (unavailable != null) {
            throw new AiUnavailableException(unavailable);
          }
          try {
            Thread.sleep(modelDelayMs);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          for (String w : text.split("(?<= )")) {
            deltas.accept(w);
          }
          return new AiReply(text, new AiUsage("test", "m", 1, 1, 1));
        }

        @Override
        public AiReply chat(AiRequest r, Consumer<String> d) {
          return answer(d, "Look at twice.");
        }

        @Override
        public AiReply edit(AiRequest r, Consumer<String> d) {
          return answer(d, "{\"t\":\"note\",\"text\":\"No edit.\"}");
        }

        @Override
        public AiReply hintExplain(AiRequest r, Consumer<String> d) {
          return answer(d, "Where to look.");
        }

        @Override
        public AiReply feedback(AiRequest r, Consumer<String> d) {
          return answer(d, "## Verdict\nHire.");
        }

        @Override
        public AiStatus status() {
          return new AiStatus(true, unavailable == null, unavailable);
        }
      };

  @BeforeEach
  void start() throws Exception {
    Path problems = root.resolve("problems").resolve("tiny");
    Path from = Path.of(getClass().getResource("/problems/tiny").toURI());
    try (Stream<Path> walk = Files.walk(from)) {
      for (Path p : walk.toList()) {
        Path target = problems.resolve(from.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else {
          Files.copy(p, target);
        }
      }
    }
    engine =
        Engine.open(root.resolve("data"), root.resolve("problems"), Clock.systemUTC(), true, model);
    api = LocalApi.start(engine, TOKEN, 0);
  }

  @AfterEach
  void stop() {
    api.stop();
    engine.close();
  }

  private HttpRequest request(String method, String path, String body) {
    return HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + api.port() + path))
        .header("Authorization", "Bearer " + TOKEN)
        .method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body))
        .build();
  }

  private JsonNode call(String method, String path, String body) throws Exception {
    return JSON.readTree(http.send(request(method, path, body), BodyHandlers.ofString()).body());
  }

  private List<JsonNode> stream(String path, String body) throws IOException, InterruptedException {
    return http.send(request("POST", path, body), BodyHandlers.ofLines())
        .body()
        .filter(l -> !l.isBlank())
        .map(JSON::readTree)
        .toList();
  }

  private String session(String type) throws Exception {
    String body = "{\"problemId\":\"tiny\",\"type\":\"" + type + "\"}";
    return "/sessions/" + call("POST", "/sessions", body).get("id").asString();
  }

  @Test
  void askStreamsDeltasThenDone() throws Exception {
    String s = session("INTERVIEW");

    List<JsonNode> lines = stream(s + "/ask", "{\"message\":\"fix it\"}");

    JsonNode done = lines.get(lines.size() - 1);
    StringBuilder text = new StringBuilder();
    lines.subList(0, lines.size() - 1).forEach(l -> text.append(l.get("text").asString()));
    assertThat(lines.subList(0, lines.size() - 1))
        .allMatch(l -> l.get("t").asString().equals("delta"));
    assertThat(text).hasToString("Look at twice.");
    assertThat(done.get("t").asString()).isEqualTo("done");
    assertThat(done.get("result").asString()).isEqualTo("Look at twice.");
    assertThat(call("GET", s + "/chat", null).size()).isEqualTo(2);
  }

  @Test
  void writeCodeEndsWithTheEditReply() throws Exception {
    String s = session("INTERVIEW");

    List<JsonNode> lines = stream(s + "/write", "{\"message\":\"fix it\"}");

    JsonNode done = lines.get(lines.size() - 1);
    assertThat(done.get("t").asString()).isEqualTo("done");
    assertThat(done.get("result").get("note").asString()).isEqualTo("No edit.");
    assertThat(done.get("result").get("outcomes").size()).isZero();
  }

  @Test
  void anUnavailableRouteStreamsOneErrorLineWithTheReason() throws Exception {
    unavailable = "The Claude CLI is signed out. Run claude once in a terminal to sign in.";
    String s = session("INTERVIEW");

    List<JsonNode> lines = stream(s + "/ask", "{\"message\":\"hi\"}");

    assertThat(lines).hasSize(1);
    assertThat(lines.get(0).get("t").asString()).isEqualTo("error");
    assertThat(lines.get(0).get("reason").asString()).contains("signed out");
  }

  @Test
  void hintExplanationsStream() throws Exception {
    String s = session("PRACTICE");
    call("POST", s + "/hints", "{\"phase\":1}");

    List<JsonNode> lines = stream(s + "/hints/explain", "{\"phase\":1}");

    assertThat(lines.get(lines.size() - 1).get("t").asString()).isEqualTo("done");
  }

  @Test
  void theApiAnswersWhileTheModelIsThinking() throws Exception {
    String s = session("INTERVIEW");
    modelDelayMs = 3_000;
    CompletableFuture<List<JsonNode>> slow =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                return stream(s + "/ask", "{\"message\":\"slow\"}");
              } catch (Exception e) {
                throw new IllegalStateException(e);
              }
            });
    Thread.sleep(300);

    long started = System.nanoTime();
    assertThat(call("GET", "/local/ping", null).get("ok").asBoolean()).isTrue();
    assertThat(call("GET", s, null).get("id")).isNotNull();
    assertThat((System.nanoTime() - started) / 1_000_000).isLessThan(1_500);
    assertThat(slow.get().get(slow.get().size() - 1).get("t").asString()).isEqualTo("done");
  }

  @Test
  void finishingStartsTheReviewWhichLandsLater() throws Exception {
    String s = session("PRACTICE");

    call("POST", s + "/finish", null);

    JsonNode review = call("GET", s + "/review", null);
    for (int i = 0; i < 50 && review.get("status").asString().equals("pending"); i++) {
      Thread.sleep(100);
      review = call("GET", s + "/review", null);
    }
    assertThat(review.get("status").asString()).isEqualTo("ready");
    assertThat(review.get("text").asString()).startsWith("## Verdict");
  }
}
