package si.intervue.engine.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.ai.AiStatus;
import si.intervue.engine.ai.ClaudeCli;
import si.intervue.engine.ai.FakeCli;

/**
 * Exit criterion 2: without developer mode no assistant route starts the Claude CLI. The finder
 * would hand the engine a fake CLI that leaves a marker file the moment it is started.
 */
class DeveloperModeTest {

  @TempDir Path root;
  private Engine engine;
  private LocalApi api;
  private final HttpClient http = HttpClient.newHttpClient();

  private Path marker() {
    return root.resolve("cli").resolve("ran");
  }

  private void open(boolean developerMode) throws Exception {
    Path problems = root.resolve("problems").resolve("tiny");
    Path from = Path.of(getClass().getResource("/problems/tiny").toURI());
    try (Stream<Path> walk = Files.walk(from)) {
      for (Path p : walk.toList()) {
        Path target = problems.resolve(from.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else if (!Files.exists(target)) {
          Files.copy(p, target);
        }
      }
    }
    Files.createDirectories(root.resolve("cli"));
    ClaudeCli markerCli = new ClaudeCli(FakeCli.command("marker", root.resolve("cli")));
    engine =
        Engine.open(
            root.resolve("data"),
            root.resolve("problems"),
            Clock.systemUTC(),
            developerMode,
            () -> Optional.of(markerCli));
    api = LocalApi.start(engine, "t", 0);
  }

  @AfterEach
  void stop() {
    api.stop();
    engine.close();
  }

  private String call(String method, String path, String body) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + api.port() + path))
            .header("Authorization", "Bearer t")
            .method(method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofString(body))
            .build();
    return http.send(request, BodyHandlers.ofString()).body();
  }

  @Test
  void endUsersNeverStartTheCli() throws Exception {
    open(false);
    String created = call("POST", "/sessions", "{\"problemId\":\"tiny\",\"type\":\"PRACTICE\"}");
    String s = "/sessions/" + created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");
    call("POST", s + "/hints", "{\"phase\":1}");

    assertThat(call("GET", "/ai/status", null)).contains(AiStatus.NO_ROUTE);
    assertThat(call("POST", s + "/ask", "{\"message\":\"fix it\"}")).contains(AiStatus.NO_ROUTE);
    assertThat(call("POST", s + "/write", "{\"message\":\"fix it\"}")).contains(AiStatus.NO_ROUTE);
    assertThat(call("POST", s + "/hints/explain", "{\"phase\":1}")).contains(AiStatus.NO_ROUTE);
    call("POST", "/health/run", null);
    call("POST", s + "/finish", null);
    Thread.sleep(500);

    assertThat(call("GET", s + "/review", null)).contains(AiStatus.NO_ROUTE);
    assertThat(marker()).doesNotExist();
  }

  @Test
  void developerModeDoesStartIt() throws Exception {
    open(true);

    call("GET", "/ai/status", null);

    // The control: the same finder does reach the CLI once developer mode is on.
    assertThat(marker()).exists();
  }
}
