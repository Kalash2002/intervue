package si.intervue.engine.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** The local API over a real server on a loopback port, with the tiny fixture problem. */
class LocalApiTest {

  private static final String TOKEN = "test-token";
  private static final JsonMapper JSON = JsonMapper.builder().build();

  @TempDir Path root;
  private Engine engine;
  private LocalApi api;
  private final HttpClient http = HttpClient.newHttpClient();

  @BeforeEach
  void start() throws Exception {
    Path problems = root.resolve("problems");
    copyTree(Path.of(getClass().getResource("/problems/tiny").toURI()), problems.resolve("tiny"));
    copyTree(
        Path.of(getClass().getResource("/dsa/tiny-dsa").toURI()), root.resolve("dsa/tiny-dsa"));
    open(false);
  }

  /** End users run without developer mode; tests that need it reopen the engine with it on. */
  private void open(boolean developerMode) {
    if (api != null) {
      api.stop();
      engine.close();
    }
    engine =
        Engine.open(
            root.resolve("data"), root.resolve("problems"), Clock.systemUTC(), developerMode);
    api = LocalApi.start(engine, TOKEN, 0);
  }

  @AfterEach
  void stop() {
    api.stop();
    engine.close();
  }

  private HttpResponse<String> send(String method, String path, String body, String token)
      throws IOException, InterruptedException {
    HttpRequest.Builder request =
        HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + api.port() + path))
            .method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body));
    if (token != null) {
      request.header("Authorization", "Bearer " + token);
    }
    return http.send(request.build(), BodyHandlers.ofString());
  }

  private JsonNode call(String method, String path, String body) throws Exception {
    HttpResponse<String> response = send(method, path, body, TOKEN);
    assertThat(response.statusCode()).as(response.body()).isEqualTo(200);
    return JSON.readTree(response.body());
  }

  @Test
  void aRequestWithoutTheTokenIsRefused() throws Exception {
    assertThat(send("GET", "/local/ping", null, null).statusCode()).isEqualTo(401);
    assertThat(send("GET", "/problems", null, "wrong").statusCode()).isEqualTo(401);
  }

  @Test
  void pingAnswers() throws Exception {
    assertThat(call("GET", "/local/ping", null).get("ok").asBoolean()).isTrue();
  }

  @Test
  void anUnknownRouteIs404AndABadRequestIs400WithAMessage() throws Exception {
    assertThat(send("GET", "/nowhere", null, TOKEN).statusCode()).isEqualTo(404);
    HttpResponse<String> bad =
        send("POST", "/sessions", "{\"problemId\":\"missing\",\"type\":\"INTERVIEW\"}", TOKEN);
    assertThat(bad.statusCode()).isEqualTo(400);
    assertThat(JSON.readTree(bad.body()).get("error").asString()).isNotBlank();
  }

  @Test
  void aPreflightFromTheShellOriginNeedsNoToken() throws Exception {
    HttpRequest preflight =
        HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + api.port() + "/problems"))
            .method("OPTIONS", BodyPublishers.noBody())
            .header("Origin", "http://tauri.localhost")
            .header("Access-Control-Request-Method", "GET")
            .build();

    HttpResponse<String> response = http.send(preflight, BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(204);
    assertThat(response.headers().firstValue("Access-Control-Allow-Origin"))
        .hasValue("http://tauri.localhost");
  }

  @Test
  void problemsAreListed() throws Exception {
    JsonNode problems = call("GET", "/problems", null);

    assertThat(problems.get(0).get("id").asString()).isEqualTo("tiny");
    assertThat(problems.get(0).get("phases").asInt()).isEqualTo(2);
    assertThat(problems.get(0).get("section").asString()).isEqualTo("coding-round");
    assertThat(problems.get(0).get("techniques").get(0).asString()).isEqualTo("arrays");
    assertThat(problems.get(0).get("tags").isArray()).isTrue();
    assertThat(problems.get(0).get("tags").size()).isZero();
    assertThat(problems.get(0).get("free").asBoolean()).isFalse();
  }

  @Test
  void aFreeListNextToTheProblemsMarksProblemsFree() throws Exception {
    Path free = root.resolve("free");
    Files.createDirectories(free);
    Files.writeString(free.resolve("free.json"), "{\"formatVersion\":1,\"problems\":[\"tiny\"]}");
    open(false);

    assertThat(call("GET", "/problems", null).get(0).get("free").asBoolean()).isTrue();
  }

  @Test
  void aSessionViewCarriesTheProblemTags() throws Exception {
    Path json = root.resolve("problems/tiny/problem.json");
    Files.writeString(
        json,
        Files.readString(json)
            .replace(
                "\"domain\": \"test\"", "\"domain\": \"test\", \"tags\": [\"arrays\", \"test\"]"));
    open(false);
    JsonNode session = call("POST", "/sessions", "{\"problemId\":\"tiny\",\"type\":\"INTERVIEW\"}");

    assertThat(session.get("tags").get(1).asString()).isEqualTo("test");
    assertThat(session.get("section").asString()).isEqualTo("coding-round");
    assertThat(call("GET", "/problems", null).get(0).get("tags").get(0).asString())
        .isEqualTo("arrays");
  }

  @Test
  void aWalkthroughIsServedWithoutASession() throws Exception {
    JsonNode answer = call("GET", "/problems/tiny/walkthrough", null);
    assertThat(answer.get("text").asString()).isNotBlank();
    assertThat(answer.get("figures").size()).isZero();
    assertThat(send("GET", "/problems/nope/walkthrough", null, TOKEN).statusCode()).isEqualTo(400);
  }

  @Test
  void walkthroughFiguresAreInlinedAsDataUris() throws Exception {
    Path figures = root.resolve("problems/tiny/figures");
    Files.createDirectories(figures);
    Files.writeString(figures.resolve("stack.svg"), "<svg xmlns='http://www.w3.org/2000/svg'/>");
    Files.writeString(figures.resolve("notes.txt"), "not a figure");

    JsonNode answer = call("GET", "/problems/tiny/walkthrough", null);

    assertThat(answer.get("figures").size()).isEqualTo(1);
    assertThat(answer.get("figures").get("figures/stack.svg").asString())
        .startsWith("data:image/svg+xml;base64,");
  }

  @Test
  void problemsListCarriesSectionAndCoveredBy() throws Exception {
    JsonNode problems = call("GET", "/problems", null);

    JsonNode dsa = problems.get(1);
    assertThat(dsa.get("id").asString()).isEqualTo("tiny-dsa");
    assertThat(dsa.get("section").asString()).isEqualTo("dsa-quick");
    assertThat(dsa.get("coveredBy").get(0).asString()).isEqualTo("tiny");
    assertThat(problems.get(0).get("coveredBy").size()).isZero();
  }

  @Test
  void historyRowCarriesSectionId() throws Exception {
    JsonNode session = call("POST", "/sessions", "{\"problemId\":\"tiny\",\"type\":\"INTERVIEW\"}");
    call("POST", "/sessions/" + session.get("id").asString() + "/finish", null);

    assertThat(call("GET", "/sessions", null).get(0).get("sectionId").asString())
        .isEqualTo("coding-round");
  }

  @Test
  void aOnePhaseProblemHasAWalkthrough() throws Exception {
    assertThat(call("GET", "/problems/tiny-dsa/walkthrough", null).toString()).contains("Approach");
  }

  @Test
  void aOnePhaseDsaSessionStartsRunsAndFinishes() throws Exception {
    JsonNode session =
        call("POST", "/sessions", "{\"problemId\":\"tiny-dsa\",\"type\":\"PRACTICE\"}");
    String s = "/sessions/" + session.get("id").asString();
    call(
        "PUT",
        s + "/file?path=Calc.java",
        "{\"text\":" + JSON.writeValueAsString(fixedCalc()) + "}");
    call("POST", s + "/run", "{\"target\":\"phase:1\"}");
    JsonNode finished = call("POST", s + "/finish", null);

    assertThat(finished.get("result").get("score").get("label").asString()).isEqualTo("Practice");
    assertThat(finished.get("result").get("score").get("total").asInt()).isEqualTo(100);
    assertThat(call("GET", "/sessions", null).get(0).get("sectionId").asString())
        .isEqualTo("dsa-quick");
  }

  @Test
  void anInterviewSessionRunsEndToEnd() throws Exception {
    JsonNode session = call("POST", "/sessions", "{\"problemId\":\"tiny\",\"type\":\"INTERVIEW\"}");
    String s = "/sessions/" + session.get("id").asString();

    assertThat(call("GET", s + "/files", null).findValuesAsString("path")).contains("Calc.java");
    call(
        "PUT",
        s + "/file?path=Calc.java",
        "{\"text\":" + JSON.writeValueAsString(fixedCalc()) + "}");
    assertThat(call("GET", s + "/file?path=Calc.java", null).get("text").asString())
        .isEqualTo(fixedCalc());
    assertThat(call("GET", s + "/symbols", null).findValuesAsString("name")).contains("twice");
    assertThat(call("GET", s + "/diagnostics", null).size()).isZero();

    JsonNode run = call("POST", s + "/run", "{\"target\":\"phase:1\"}");
    assertThat(run.get("newlyPassed").get(0).asInt()).isEqualTo(1);

    call("PUT", s + "/notes", "{\"notes\":\"checked\"}");
    call("POST", s + "/skip", null);
    JsonNode finished = call("POST", s + "/finish", null);

    assertThat(finished.get("finished").asBoolean()).isTrue();
    assertThat(finished.get("result").get("score").get("label").asString()).isEqualTo("Interview");
    assertThat(call("GET", "/sessions", null).get(0).get("finished").asBoolean()).isTrue();
  }

  @Test
  void practiceHintsAndResetsWork() throws Exception {
    JsonNode session = call("POST", "/sessions", "{\"problemId\":\"tiny\",\"type\":\"PRACTICE\"}");
    String s = "/sessions/" + session.get("id").asString();

    assertThat(call("POST", s + "/hints", "{\"phase\":1}").get("level").asInt()).isEqualTo(1);
    assertThat(call("GET", s + "/hints?phase=1", null).size()).isEqualTo(1);

    call("PUT", s + "/file?path=Calc.java", "{\"text\":\"broken\"}");
    call("POST", s + "/reset-file", "{\"path\":\"Calc.java\"}");
    assertThat(call("GET", s + "/file?path=Calc.java", null).get("text").asString())
        .contains("class Calc");
    call("POST", s + "/reset-problem", null);
  }

  @Test
  void onlyBundledFakeEditStreamsReplay() throws Exception {
    open(true);
    JsonNode session = call("POST", "/sessions", "{\"problemId\":\"tiny\",\"type\":\"INTERVIEW\"}");
    String s = "/sessions/" + session.get("id").asString();

    assertThat(call("GET", "/fake-edits", null).get(0).asString())
        .isEqualTo("hold-exposure-phase1");
    JsonNode reply = call("POST", s + "/fake-edits", "{\"id\":\"hold-exposure-phase1\"}");
    String replyId = reply.get("replyId").asString();
    assertThat(call("POST", s + "/replies/" + replyId + "/undo", null).isArray()).isTrue();
    assertThat(send("POST", s + "/fake-edits", "{\"id\":\"../../etc\"}", TOKEN).statusCode())
        .isEqualTo(400);
  }

  @Test
  void withoutDeveloperModeTheAssistantIsUnavailableAndTheDemoIsHidden() throws Exception {
    JsonNode status = call("GET", "/ai/status", null);

    assertThat(status.get("developer").asBoolean()).isFalse();
    assertThat(status.get("available").asBoolean()).isFalse();
    assertThat(status.get("reason").asString())
        .isEqualTo("Assistant unavailable: sign-in arrives in Phase 1b");
    assertThat(send("GET", "/fake-edits", null, TOKEN).statusCode()).isEqualTo(404);
  }

  @Test
  void developerModeIsReportedAndShowsTheDemo() throws Exception {
    open(true);

    assertThat(call("GET", "/ai/status", null).get("developer").asBoolean()).isTrue();
    assertThat(call("GET", "/fake-edits", null).isArray()).isTrue();
  }

  @Test
  void healthResultsAreServedAfterARun() throws Exception {
    JsonNode report = call("POST", "/health/run?fast=true", null);

    assertThat(report.get("results").findValuesAsString("id")).contains("os", "data-folder");
    // The developer-only CLI check does not exist for end users.
    assertThat(report.get("results").findValuesAsString("id")).doesNotContain("ai.cli");
    assertThat(call("GET", "/health", null).findValuesAsString("id")).contains("port");
  }

  private static String fixedCalc() {
    return "public class Calc {\n  static int twice(int n) {\n    return n + n;\n  }\n\n"
        + "  static int sum(int[] values) {\n    return 0;\n  }\n}\n";
  }

  private static void copyTree(Path from, Path to) throws IOException {
    try (Stream<Path> walk = Files.walk(from)) {
      for (Path p : walk.toList()) {
        Path target = to.resolve(from.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else {
          Files.copy(p, target);
        }
      }
    }
  }
}
