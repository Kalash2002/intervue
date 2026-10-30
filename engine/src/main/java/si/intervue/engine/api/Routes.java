package si.intervue.engine.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import si.intervue.engine.api.LocalApi.BadRequest;
import si.intervue.engine.api.LocalApi.NotFound;
import si.intervue.engine.sections.AssistantChannel;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.SessionAssistant;
import si.intervue.engine.session.Sessions;
import si.intervue.engine.session.edits.FakeEditSource;
import si.intervue.problemformat.Problem;
import tools.jackson.databind.JsonNode;

/**
 * Maps a method and path to one engine call. Only plumbing: every rule lives in the use cases.
 * Returned values become the JSON body; {@code null} means {@code {"ok":true}}.
 */
final class Routes {

  /**
   * A response that streams: {@code body} gets a sink for text deltas and returns the final result.
   * Routes only build it; LocalApi runs it outside the engine lock.
   */
  record Streamed(Function<Consumer<String>, Object> body) {}

  /** One dashboard row; the full problem is only read when a session starts. */
  record ProblemSummary(
      String id,
      String section,
      String title,
      String summary,
      String difficulty,
      List<String> techniques,
      List<String> tags,
      int minutes,
      int phases,
      boolean free,
      List<String> coveredBy) {}

  private final Engine engine;

  Routes(Engine engine) {
    this.engine = engine;
  }

  Object handle(String method, List<String> path, Map<String, String> query, JsonNode body) {
    if (path.size() >= 2 && path.get(0).equals("sessions")) {
      return session(method, path, query, body);
    }
    if (method.equals("GET")
        && path.size() == 3
        && path.get(0).equals("problems")
        && path.get(2).equals("walkthrough")) {
      return engine.sessions().walkthrough(path.get(1));
    }
    String route = method + " /" + String.join("/", path);
    return switch (route) {
      case "GET /local/ping" -> Map.of("ok", true);
      case "GET /problems" -> problems();
      case "GET /sessions" -> engine.sessions().history();
      case "POST /sessions" ->
          engine
              .sessions()
              .create(
                  text(body, "problemId"),
                  SessionType.valueOf(text(body, "type")),
                  flag(body, "timed"));
      case "GET /fake-edits" -> demo(FakeEditSource.ids());
      case "GET /ai/status" -> engine.aiStatus();
      case "GET /health" -> engine.doctor().lastResults();
      case "POST /health/run" ->
          "true".equals(query.get("fast")) ? engine.doctor().runFast() : engine.doctor().runAll();
      default -> throw new NotFound("No route " + route);
    };
  }

  /** Routes under {@code /sessions/<id>}; {@code path} is the whole path, split on slashes. */
  private Object session(
      String method, List<String> path, Map<String, String> query, JsonNode body) {
    Sessions s = engine.sessions();
    String id = path.get(1);
    if (method.equals("POST") && path.size() == 5 && path.get(4).equals("undo")) {
      return switch (path.get(2)) {
        case "edits" -> s.undoEdit(id, path.get(3));
        case "replies" -> s.undoReply(id, path.get(3));
        default -> throw new NotFound("No undo for " + path.get(2));
      };
    }
    String route = method + " " + String.join("/", path.subList(2, path.size()));
    return switch (route) {
      case "GET " -> s.view(id);
      case "GET files" -> s.files(id);
      case "GET file" -> Map.of("text", s.readFile(id, required(query, "path")));
      case "PUT file" -> {
        s.writeFile(id, required(query, "path"), text(body, "text"));
        yield null;
      }
      case "POST files" -> {
        s.createFile(id, text(body, "path"));
        yield null;
      }
      case "GET symbols" -> s.symbols(id);
      case "GET diagnostics" -> s.diagnostics(id);
      case "PUT notes" -> {
        s.saveNotes(id, text(body, "notes"));
        yield null;
      }
      case "POST run" -> s.run(id, text(body, "target"));
      case "POST skip" -> s.skip(id);
      case "POST hints" -> s.hint(id, number(body, "phase"));
      case "GET hints" -> s.revealedHints(id, Integer.parseInt(required(query, "phase")));
      case "POST fake-edits" -> s.applyEdits(id, FakeEditSource.lines(demo(text(body, "id"))));
      case "POST reset-file" -> Map.of("text", s.resetFile(id, text(body, "path")));
      case "POST reset-problem" -> {
        s.resetProblem(id);
        yield null;
      }
      case "POST finish" -> engine.assistant().finish(id, flag(body, "early"));
      case "GET chat" -> engine.assistant().chat(id);
      case "GET review" -> engine.assistant().review(id);
      case "POST ask" -> {
        engine.assistant().preflight(id, AssistantChannel.ASK, false);
        yield new Streamed(d -> engine.assistant().ask(id, text(body, "message"), rules(body), d));
      }
      case "POST write" -> {
        engine.assistant().preflight(id, AssistantChannel.WRITE_CODE, false);
        yield new Streamed(d -> engine.assistant().write(id, text(body, "message"), rules(body)));
      }
      case "POST hints/explain" -> {
        engine.assistant().preflight(id, AssistantChannel.ASK, true);
        yield new Streamed(d -> engine.assistant().explainHint(id, number(body, "phase"), d));
      }
      default -> throw new NotFound("No session route " + route);
    };
  }

  /** The recorded-reply demo exists only in developer mode; otherwise the route does not exist. */
  private <T> T demo(T value) {
    if (!engine.developerMode()) {
      throw new NotFound("No such route");
    }
    return value;
  }

  private List<ProblemSummary> problems() {
    return engine.content().ids().stream()
        .map(id -> summary(engine.content().load(id).problem(), engine.content().isFree(id)))
        .toList();
  }

  private static ProblemSummary summary(Problem p, boolean free) {
    return new ProblemSummary(
        p.id(),
        p.section(),
        p.title(),
        p.summary(),
        p.difficulty(),
        p.techniques(),
        p.tags(),
        p.minutes(),
        p.phases().size(),
        free,
        p.coveredBy());
  }

  /** Practice rules: Helpful unless the room asks for Interview rules. */
  private static String rules(JsonNode body) {
    JsonNode rules = body.get("rules");
    return rules != null && rules.isString() ? rules.asString() : SessionAssistant.HELPFUL;
  }

  /** An optional true/false field; absent means false. */
  private static boolean flag(JsonNode body, String field) {
    JsonNode value = body.get(field);
    return value != null && value.asBoolean(false);
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
