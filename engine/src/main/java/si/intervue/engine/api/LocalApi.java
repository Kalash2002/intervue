package si.intervue.engine.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import si.intervue.engine.ai.AiUnavailableException;
import si.intervue.engine.content.ContentException;
import si.intervue.engine.session.SessionException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * The engine's HTTP API on 127.0.0.1 (PLAN.md 5.3). Every request but a CORS preflight must carry
 * the per-launch token as {@code Authorization: Bearer <token>}; the token is never logged.
 */
public final class LocalApi {

  private static final Logger LOG = Logger.getLogger(LocalApi.class.getName());
  private static final JsonMapper JSON = JsonMapper.builder().build();

  /** The Tauri webview on Windows and macOS, and the Vite dev server the shell loads in dev. */
  private static final Set<String> ORIGINS =
      Set.of("http://tauri.localhost", "tauri://localhost", "http://localhost:1420");

  /** A request the caller got wrong; answered with 400 and its message. */
  static final class BadRequest extends RuntimeException {
    private static final long serialVersionUID = 1L;

    BadRequest(String message) {
      super(message);
    }
  }

  /** No route matched; answered with 404. */
  static final class NotFound extends RuntimeException {
    private static final long serialVersionUID = 1L;

    NotFound(String message) {
      super(message);
    }
  }

  private final HttpServer server;
  private final byte[] token;
  private final Routes routes;
  private final Engine engine;

  private LocalApi(HttpServer server, String token, Routes routes, Engine engine) {
    this.server = server;
    this.token = token.getBytes(StandardCharsets.UTF_8);
    this.routes = routes;
    this.engine = engine;
  }

  /** Binds 127.0.0.1 on {@code port} (0 picks a free one) and starts serving. */
  public static LocalApi start(Engine engine, String token, int port) {
    HttpServer server;
    try {
      server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot bind 127.0.0.1:" + port, e);
    }
    LocalApi api = new LocalApi(server, token, new Routes(engine), engine);
    server.createContext("/", api::handle);
    // Several threads so a streaming model reply never blocks other calls; the engine lock still
    // serialises every store and file operation (a long run or the Doctor holds that lock).
    server.setExecutor(Executors.newFixedThreadPool(8));
    server.start();
    return api;
  }

  public int port() {
    return server.getAddress().getPort();
  }

  public void stop() {
    server.stop(0);
  }

  private void handle(HttpExchange exchange) throws IOException {
    try (exchange) {
      String origin = exchange.getRequestHeaders().getFirst("Origin");
      if (origin != null && ORIGINS.contains(origin)) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", origin);
        exchange.getResponseHeaders().add("Vary", "Origin");
      }
      String method = exchange.getRequestMethod();
      if (method.equals("OPTIONS")) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT");
        exchange
            .getResponseHeaders()
            .add("Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.sendResponseHeaders(204, -1);
        return;
      }
      if (!authorised(exchange.getRequestHeaders().getFirst("Authorization"))) {
        send(exchange, 401, Map.of("error", "Missing or wrong token"));
        return;
      }
      respond(exchange, method);
    }
  }

  private void respond(HttpExchange exchange, String method) throws IOException {
    try {
      List<String> path =
          Arrays.stream(exchange.getRequestURI().getPath().split("/"))
              .filter(s -> !s.isEmpty())
              .toList();
      Map<String, String> query = Routes.query(exchange.getRequestURI().getRawQuery());
      JsonNode body = readBody(exchange.getRequestBody());
      Object result = engine.locked(() -> routes.handle(method, path, query, body));
      if (result instanceof Routes.Streamed streamed) {
        stream(exchange, streamed);
        return;
      }
      send(exchange, 200, result == null ? Map.of("ok", true) : result);
    } catch (NotFound e) {
      send(exchange, 404, Map.of("error", e.getMessage()));
    } catch (BadRequest
        | SessionException
        | ContentException
        | IllegalArgumentException
        | JacksonException e) {
      send(exchange, 400, Map.of("error", String.valueOf(e.getMessage())));
    } catch (RuntimeException e) {
      LOG.log(
          Level.WARNING, "Request failed: " + method + " " + exchange.getRequestURI().getPath(), e);
      send(exchange, 500, Map.of("error", "The engine hit an internal error; see its log."));
    }
  }

  /**
   * Streams NDJSON: {@code {"t":"delta","text":...}} lines while the model answers, then one {@code
   * {"t":"done","result":...}} or {@code {"t":"error","reason":...}}. Runs outside the engine lock;
   * the assistant takes it only around store work.
   */
  private static void stream(HttpExchange exchange, Routes.Streamed streamed) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "application/x-ndjson; charset=utf-8");
    exchange.sendResponseHeaders(200, 0);
    OutputStream out = exchange.getResponseBody();
    Consumer<Object> line =
        value -> {
          try {
            out.write(JSON.writeValueAsBytes(value));
            out.write('\n');
            out.flush();
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        };
    try {
      Object result =
          streamed.body().apply(text -> line.accept(Map.of("t", "delta", "text", text)));
      line.accept(Map.of("t", "done", "result", result));
    } catch (AiUnavailableException | SessionException | BadRequest e) {
      line.accept(Map.of("t", "error", "reason", String.valueOf(e.getMessage())));
    } catch (UncheckedIOException e) {
      LOG.log(Level.FINE, "The client went away during a stream", e);
    } catch (RuntimeException e) {
      LOG.log(Level.WARNING, "Streaming request failed", e);
      line.accept(Map.of("t", "error", "reason", "The engine hit an internal error; see its log."));
    }
  }

  // Constant-time comparison so the token cannot be guessed byte by byte from response timing.
  private boolean authorised(String header) {
    if (header == null || !header.startsWith("Bearer ")) {
      return false;
    }
    return MessageDigest.isEqual(token, header.substring(7).getBytes(StandardCharsets.UTF_8));
  }

  private static JsonNode readBody(InputStream in) throws IOException {
    byte[] bytes = in.readAllBytes();
    return bytes.length == 0 ? JSON.createObjectNode() : JSON.readTree(bytes);
  }

  private static void send(HttpExchange exchange, int status, Object value) throws IOException {
    byte[] bytes = JSON.writeValueAsBytes(value);
    exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(status, bytes.length);
    exchange.getResponseBody().write(bytes);
  }
}
