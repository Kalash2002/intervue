package si.intervue.engine.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import si.intervue.engine.api.LocalApi.BadRequest;
import si.intervue.engine.api.LocalApi.NotFound;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.Sessions;
import si.intervue.engine.session.edits.FakeEditSource;
import si.intervue.problemformat.Problem;
import tools.jackson.databind.JsonNode;

/**
 * Maps a method and path to one engine call. Only plumbing: every rule lives in the use cases.
 * Returned values become the JSON body; {@code null} means {@code {"ok":true}}.
 */
final class Routes {

  /** One dashboard row; the full problem is only read when a session starts. */
  record ProblemSummary(
      String id, String title, String summary, String difficulty, int minutes, int phases) {}

  private final Engine engine;

  Routes(Engine engine) {
    this.engine = engine;
  }

  Object handle(String method, List<String> path, Map<String, String> query, JsonNode body) {
    if (path.size() >= 2 && path.get(0).equals("sessions")) {
      String rest = String.join("/", path.subList(2, path.size()));
      return session(method, path.get(1), rest, query, body);
    }
    String route = method + " /" + String.join("/", path);
    return switch (route) {
      case "GET /local/ping" -> Map.of("ok", true);
      case "GET /problems" -> problems();
      case "GET /sessions" -> engine.sessions().history();
      case "POST /sessions" ->
          engine
              .sessions()
              .create(text(body, "problemId"), SessionType.valueOf(text(body, "type")));
      case "GET /fake-edits" -> FakeEditSource.ids();
      case "GET /health" -> engine.doctor().lastResults();
      case "POST /health/run" ->
          "true".equals(query.get("fast")) ? engine.doctor().runFast() : engine.doctor().runAll();
      default -> throw new NotFound("No route " + route);
    };
  }

  private Object session(
      String method, String id, String rest, Map<String, String> query, JsonNode body) {
    Sessions s = engine.sessions();
    String route = method + " " + rest;
    if (route.startsWith("POST edits/") && route.endsWith("/undo")) {
      return s.undoEdit(id, rest.substring(6, rest.length() - 5));
    }
    if (route.startsWith("POST replies/") && route.endsWith("/undo")) {
      return s.undoReply(id, rest.substring(8, rest.length() - 5));
    }
    switch (route) {
      case "PUT file" -> s.writeFile(id, required(query, "path"), text(body, "text"));
      case "POST files" -> s.createFile(id, text(body, "path"));
      case "PUT notes" -> s.saveNotes(id, text(body, "notes"));
      case "POST reset-problem" -> s.resetProblem(id);
      default -> {
        return sessionQuery(s, route, id, query, body);
      }
    }
    return null;
  }

  private static Object sessionQuery(
      Sessions s, String route, String id, Map<String, String> query, JsonNode body) {
    return switch (route) {
      case "GET " -> s.view(id);
      case "GET files" -> s.files(id);
      case "GET file" -> Map.of("text", s.readFile(id, required(query, "path")));
      case "GET symbols" -> s.symbols(id);
      case "GET diagnostics" -> s.diagnostics(id);
      case "POST run" -> s.run(id, text(body, "target"));
      case "POST skip" -> s.skip(id);
      case "POST hints" -> s.hint(id, number(body, "phase"));
      case "GET hints" -> s.revealedHints(id, Integer.parseInt(required(query, "phase")));
      case "POST fake-edits" -> s.applyEdits(id, FakeEditSource.lines(text(body, "id")));
      case "POST reset-file" -> Map.of("text", s.resetFile(id, text(body, "path")));
      case "POST finish" -> s.finish(id);
      default -> throw new NotFound("No session route " + route);
    };
  }

  private List<ProblemSummary> problems() {
    return engine.content().ids().stream()
        .map(id -> engine.content().load(id).problem())
        .map(Routes::summary)
        .toList();
  }

  private static ProblemSummary summary(Problem p) {
    return new ProblemSummary(
        p.id(), p.title(), p.summary(), p.difficulty(), p.minutes(), p.phases().size());
  }

  private static String text(JsonNode body, String field) {
    JsonNode value = body.get(field);
    if (value == null || !value.isString()) {
      throw new BadRequest("Missing text field \"" + field + "\"");
    }
    return value.asString();
  }

  private static int number(JsonNode body, String field) {
    JsonNode value = body.get(field);
    if (value == null || !value.isInt()) {
      throw new BadRequest("Missing number field \"" + field + "\"");
    }
    return value.asInt();
  }

  private static String required(Map<String, String> query, String name) {
    String value = query.get(name);
    if (value == null) {
      throw new BadRequest("Missing query parameter \"" + name + "\"");
    }
    return value;
  }

  static Map<String, String> query(String raw) {
    Map<String, String> query = new HashMap<>();
    if (raw == null) {
      return query;
    }
    for (String pair : raw.split("&")) {
      int eq = pair.indexOf('=');
      String key = eq < 0 ? pair : pair.substring(0, eq);
      String value = eq < 0 ? "" : pair.substring(eq + 1);
      query.put(
          URLDecoder.decode(key, StandardCharsets.UTF_8),
          URLDecoder.decode(value, StandardCharsets.UTF_8));
    }
    return query;
  }
}
