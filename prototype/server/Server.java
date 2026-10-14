import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Intervue: a local practice room for AI-assisted coding interviews.
 *
 * Single file, no dependencies. Start it with:  java server/Server.java [--port 4477]
 * It serves the interface in web/, keeps each practice session in sessions/, compiles and runs
 * the candidate's Java with the JDK that runs this server, and relays chat to the Claude Code CLI.
 */
public class Server {

    static Path root;
    static int port = 4477;
    static boolean openBrowser = true;
    static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();
    static volatile Map<String, Object> claudeInfo;

    public static void main(String[] args) throws Exception {
        root = Path.of("").toAbsolutePath();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--port") && i + 1 < args.length) {
                port = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--root") && i + 1 < args.length) {
                root = Path.of(args[++i]).toAbsolutePath();
            } else if (args[i].equals("--no-open")) {
                openBrowser = false;
            }
        }
        if (!Files.isDirectory(root.resolve("problems")) || !Files.isDirectory(root.resolve("web"))) {
            System.err.println("Cannot find the problems/ and web/ folders in " + root);
            System.err.println("Start the server from the Intervue folder (use run.bat or run.sh).");
            System.exit(2);
        }
        Files.createDirectories(root.resolve("sessions"));
        claudeInfo = Claude.detect();

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        } catch (java.net.BindException e) {
            System.err.println("Port " + port + " is already in use. Intervue may already be running: open http://localhost:" + port);
            System.err.println("To use another port, start with:  --port 4488");
            System.exit(3);
            return;
        }
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", Server::handle);
        server.start();
        System.out.println("Intervue is running. Open http://localhost:" + port + " in your browser.");
        System.out.println("Java: " + System.getProperty("java.version") + "   Claude Code CLI: "
                + (Boolean.TRUE.equals(claudeInfo.get("available")) ? claudeInfo.get("version") : "not found (chat is disabled)"));
        System.out.println("Press Ctrl+C to stop.");
        if (openBrowser) {
            try {
                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://localhost:" + port));
                }
            } catch (Exception | Error ignored) {
                // no desktop available; the address is printed above
            }
        }
    }

    // ------------------------------------------------------------------ routing

    static void handle(HttpExchange ex) throws IOException {
        try {
            String host = ex.getRequestHeaders().getFirst("Host");
            if (host == null || !(host.equals("localhost:" + port) || host.equals("127.0.0.1:" + port))) {
                send(ex, 403, "text/plain", "Forbidden");
                return;
            }
            String method = ex.getRequestMethod();
            String path = ex.getRequestURI().getPath();
            if (!path.startsWith("/api/")) {
                serveStatic(ex, path);
                return;
            }
            // Requests that change anything must come from this page, not from another site.
            if (!method.equals("GET") && !"1".equals(ex.getRequestHeaders().getFirst("X-Intervue"))) {
                send(ex, 403, "text/plain", "Forbidden");
                return;
            }
            String[] seg = path.substring(5).split("/");
            if (seg.length == 1 && seg[0].equals("state") && method.equals("GET")) {
                json(ex, 200, state());
            } else if (seg.length == 1 && seg[0].equals("sessions") && method.equals("POST")) {
                json(ex, 200, Sessions.create(str(body(ex), "problemId")));
            } else if (seg.length >= 2 && seg[0].equals("sessions")) {
                String id = seg[1];
                if (!id.matches("[a-z0-9-]{3,80}") || !Files.isDirectory(root.resolve("sessions").resolve(id))) {
                    json(ex, 404, Map.of("error", "No such session."));
                    return;
                }
                String action = seg.length > 2 ? seg[2] : "";
                Object lock = LOCKS.computeIfAbsent(id, k -> new Object());
                switch (method + " " + action) {
                    case "GET " -> json(ex, 200, Sessions.view(id));
                    case "GET file" -> json(ex, 200, Sessions.readFile(id, query(ex, "path")));
                    case "PUT file" -> {
                        synchronized (lock) {
                            json(ex, 200, Sessions.writeFile(id, body(ex)));
                        }
                    }
                    case "POST files" -> {
                        synchronized (lock) {
                            json(ex, 200, Sessions.createFile(id, str(body(ex), "path")));
                        }
                    }
                    case "POST reset-file" -> {
                        synchronized (lock) {
                            json(ex, 200, Sessions.resetFile(id, str(body(ex), "path")));
                        }
                    }
                    case "POST run" -> {
                        synchronized (lock) {
                            json(ex, 200, Runner.run(id, str(body(ex), "target"), true));
                        }
                    }
                    case "POST unlock" -> {
                        synchronized (lock) {
                            json(ex, 200, Sessions.unlock(id));
                        }
                    }
                    case "POST notes" -> {
                        synchronized (lock) {
                            json(ex, 200, Sessions.saveNotes(id, str(body(ex), "notes")));
                        }
                    }
                    case "POST finish" -> {
                        synchronized (lock) {
                            json(ex, 200, Sessions.finish(id));
                        }
                    }
                    case "POST chat" -> Chat.chat(ex, id, body(ex), lock);
                    case "POST feedback" -> Chat.feedback(ex, id, body(ex), lock);
                    default -> json(ex, 404, Map.of("error", "Unknown request."));
                }
            } else {
                json(ex, 404, Map.of("error", "Unknown request."));
            }
        } catch (UserError e) {
            json(ex, 400, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                json(ex, 500, Map.of("error", "The server hit an unexpected problem: " + e));
            } catch (IOException ignored) {
                // response already started
            }
        } finally {
            ex.close();
        }
    }

    static class UserError extends RuntimeException {
        UserError(String message) {
            super(message);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> state() throws IOException {
        Map<String, Object> out = new LinkedHashMap<>();
        List<Object> problems = new ArrayList<>();
        for (Path dir : listDirs(root.resolve("problems"))) {
            Path meta = dir.resolve("problem.json");
            if (Files.exists(meta)) {
                Map<String, Object> p = Json.object(Files.readString(meta));
                Map<String, Object> slim = new LinkedHashMap<>();
                for (String k : List.of("id", "title", "difficulty", "category", "minutes", "summary")) {
                    slim.put(k, p.get(k));
                }
                slim.put("phaseCount", ((List<?>) p.get("phases")).size());
                problems.add(slim);
            }
        }
        out.put("problems", problems);
        List<Object> sessions = new ArrayList<>();
        List<Path> dirs = listDirs(root.resolve("sessions"));
        Collections.reverse(dirs);
        for (Path dir : dirs) {
            Path meta = dir.resolve("session.json");
            if (Files.exists(meta)) {
                Map<String, Object> s = Json.object(Files.readString(meta));
                Map<String, Object> slim = new LinkedHashMap<>();
                for (String k : List.of("id", "problemId", "title", "startedAt", "finishedAt", "phasesPassed", "minutes")) {
                    slim.put(k, s.get(k));
                }
                slim.put("phaseCount", ((List<?>) s.get("phases")).size());
                if (s.get("finishedAt") != null) {
                    Map<String, Object> score = (Map<String, Object>) Sessions.report(s).get("score");
                    slim.put("score", score.get("total"));
                    slim.put("solved", score.get("solved"));
                }
                sessions.add(slim);
            }
        }
        sessions.sort((x, y) -> Long.compare(num((Map<String, Object>) y, "startedAt"), num((Map<String, Object>) x, "startedAt")));
        out.put("sessions", sessions);
        out.put("claude", claudeInfo);
        out.put("java", System.getProperty("java.version"));
        out.put("now", System.currentTimeMillis());
        return out;
    }

    static List<Path> listDirs(Path parent) throws IOException {
        try (Stream<Path> s = Files.list(parent)) {
            return new ArrayList<>(s.filter(Files::isDirectory).sorted().toList());
        }
    }

    // ------------------------------------------------------------------ static files

    static void serveStatic(HttpExchange ex, String path) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }
        Path web = root.resolve("web").normalize();
        Path file = web.resolve(path.substring(1)).normalize();
        if (!file.startsWith(web) || !Files.isRegularFile(file)) {
            send(ex, 404, "text/plain", "Not found");
            return;
        }
        String name = file.getFileName().toString();
        String type = name.endsWith(".html") ? "text/html; charset=utf-8"
                : name.endsWith(".js") ? "text/javascript; charset=utf-8"
                : name.endsWith(".css") ? "text/css; charset=utf-8"
                : name.endsWith(".woff2") ? "font/woff2"
                : name.endsWith(".svg") ? "image/svg+xml"
                : "application/octet-stream";
        byte[] bytes = Files.readAllBytes(file);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("Cache-Control", name.endsWith(".woff2") ? "max-age=86400" : "no-store");
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
    }

    // ------------------------------------------------------------------ http helpers

    static Map<String, Object> body(HttpExchange ex) throws IOException {
        byte[] bytes = ex.getRequestBody().readAllBytes();
        if (bytes.length == 0) {
            return new LinkedHashMap<>();
        }
        return Json.object(new String(bytes, StandardCharsets.UTF_8));
    }

    static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? "" : v.toString();
    }

    static String query(HttpExchange ex, String key) {
        String q = ex.getRequestURI().getQuery();
        if (q != null) {
            for (String pair : q.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0 && pair.substring(0, eq).equals(key)) {
                    return pair.substring(eq + 1);
                }
            }
        }
        return "";
    }

    static void json(HttpExchange ex, int status, Object value) throws IOException {
        send(ex, status, "application/json; charset=utf-8", Json.write(value));
    }

    static void send(HttpExchange ex, int status, String type, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
    }

    // ------------------------------------------------------------------ sessions

    static final class Sessions {

        static Path dir(String id) {
            return root.resolve("sessions").resolve(id);
        }

        static Path work(String id) {
            return dir(id).resolve("work");
        }

        static Map<String, Object> load(String id) throws IOException {
            return Json.object(Files.readString(dir(id).resolve("session.json")));
        }

        static void save(String id, Map<String, Object> session) throws IOException {
            Path target = dir(id).resolve("session.json");
            Path temp = dir(id).resolve("session.json.tmp");
            Files.writeString(temp, Json.write(session));
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }

        static Map<String, Object> problem(String problemId) throws IOException {
            if (!problemId.matches("[a-z0-9-]{1,60}")) {
                throw new UserError("Unknown problem.");
            }
            Path meta = root.resolve("problems").resolve(problemId).resolve("problem.json");
            if (!Files.exists(meta)) {
                throw new UserError("Unknown problem.");
            }
            return Json.object(Files.readString(meta));
        }

        static Map<String, Object> create(String problemId) throws IOException {
            Map<String, Object> problem = problem(problemId);
            String id = problemId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Path work = work(id);
            Files.createDirectories(work);
            copyTree(root.resolve("problems").resolve(problemId).resolve("starter"), work);
            Map<String, Object> session = new LinkedHashMap<>();
            session.put("version", 1L);
            session.put("id", id);
            session.put("problemId", problemId);
            session.put("title", problem.get("title"));
            session.put("difficulty", problem.get("difficulty"));
            session.put("minutes", problem.get("minutes"));
            session.put("startedAt", System.currentTimeMillis());
            session.put("finishedAt", null);
            session.put("phases", problem.get("phases"));
            session.put("unlocked", 1L);
            session.put("phasesPassed", new LinkedHashMap<String, Object>());
            session.put("events", new ArrayList<>());
            session.put("chat", new ArrayList<>());
            session.put("notes", "");
            session.put("feedback", "");
            save(id, session);
            return view(id);
        }

        /** Everything the page needs to draw a session. Never includes the reference solution. */
        static Map<String, Object> view(String id) throws IOException {
            Map<String, Object> session = load(id);
            Map<String, Object> problem = problem(str(session, "problemId"));
            Map<String, Object> out = new LinkedHashMap<>(session);
            out.put("now", System.currentTimeMillis());
            out.put("category", problem.get("category"));
            out.put("files", listFiles(id, problem));
            out.put("brief", brief(str(session, "problemId"), num(session, "unlocked"), session.get("finishedAt") != null));
            if (session.get("finishedAt") != null) {
                out.put("followUps", problem.get("followUps"));
                out.put("report", report(session));
                Path walkthrough = root.resolve("problems").resolve(str(session, "problemId")).resolve("walkthrough.md");
                out.put("walkthrough", Files.exists(walkthrough) ? Files.readString(walkthrough) : "");
            }
            return out;
        }

        static List<Object> brief(String problemId, long unlocked, boolean all) throws IOException {
            Path dir = root.resolve("problems").resolve(problemId).resolve("brief");
            List<Object> parts = new ArrayList<>();
            parts.add(Files.readString(dir.resolve("intro.md")));
            for (int phase = 1; Files.exists(dir.resolve("phase" + phase + ".md")); phase++) {
                parts.add(all || phase <= unlocked ? Files.readString(dir.resolve("phase" + phase + ".md")) : null);
            }
            return parts;
        }

        static List<Object> listFiles(String id, Map<String, Object> problem) throws IOException {
            List<?> readOnly = (List<?>) problem.get("readOnly");
            Path work = work(id);
            List<Object> files = new ArrayList<>();
            try (Stream<Path> walk = Files.walk(work, 3)) {
                for (Path p : walk.filter(Files::isRegularFile).sorted().toList()) {
                    String rel = work.relativize(p).toString().replace('\\', '/');
                    Map<String, Object> f = new LinkedHashMap<>();
                    f.put("path", rel);
                    f.put("readOnly", readOnly.contains(rel));
                    files.add(f);
                }
            }
            return files;
        }

        static Path safe(String id, String rel) {
            if (rel == null || rel.isBlank() || !rel.matches("[A-Za-z0-9_./-]{1,120}") || rel.contains("..") || rel.startsWith("/")) {
                throw new UserError("That file name is not allowed.");
            }
            Path work = work(id).normalize();
            Path file = work.resolve(rel).normalize();
            if (!file.startsWith(work)) {
                throw new UserError("That file name is not allowed.");
            }
            return file;
        }

        static Map<String, Object> readFile(String id, String rel) throws IOException {
            Path file = safe(id, rel);
            if (!Files.isRegularFile(file)) {
                throw new UserError("No such file: " + rel);
            }
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("path", rel);
            out.put("content", Files.readString(file));
            return out;
        }

        static Map<String, Object> writeFile(String id, Map<String, Object> body) throws IOException {
            String rel = str(body, "path");
            Path file = safe(id, rel);
            Map<String, Object> session = load(id);
            if (session.get("finishedAt") != null) {
                throw new UserError("This session is finished. Files are read-only.");
            }
            List<?> readOnly = (List<?>) problem(str(session, "problemId")).get("readOnly");
            if (readOnly.contains(rel)) {
                throw new UserError(rel + " is read-only.");
            }
            if (!Files.isRegularFile(file)) {
                throw new UserError("No such file: " + rel);
            }
            Files.writeString(file, str(body, "content"));
            return Map.of("ok", true);
        }

        static Map<String, Object> createFile(String id, String rel) throws IOException {
            Path file = safe(id, rel);
            if (!rel.matches(".*\\.(java|txt|md|csv)")) {
                throw new UserError("New files must end in .java, .txt, .md or .csv.");
            }
            if (Files.exists(file)) {
                throw new UserError(rel + " already exists.");
            }
            Files.createDirectories(file.getParent());
            String name = file.getFileName().toString();
            Files.writeString(file, name.endsWith(".java") ? "public class " + name.substring(0, name.length() - 5) + " {\n}\n" : "");
            Map<String, Object> session = load(id);
            return Map.of("files", listFiles(id, problem(str(session, "problemId"))));
        }

        static Map<String, Object> resetFile(String id, String rel) throws IOException {
            Path file = safe(id, rel);
            Map<String, Object> session = load(id);
            Path original = root.resolve("problems").resolve(str(session, "problemId")).resolve("starter").resolve(rel);
            if (!Files.isRegularFile(original)) {
                throw new UserError(rel + " is not part of the original project.");
            }
            Files.copy(original, file, StandardCopyOption.REPLACE_EXISTING);
            event(session, "reset", Map.of("path", rel));
            save(id, session);
            return readFile(id, rel);
        }

        static Map<String, Object> unlock(String id) throws IOException {
            Map<String, Object> session = load(id);
            long unlocked = num(session, "unlocked");
            int count = ((List<?>) session.get("phases")).size();
            if (unlocked < count) {
                session.put("unlocked", unlocked + 1);
                event(session, "skip", Map.of("phase", unlocked + 1));
                save(id, session);
            }
            return view(id);
        }

        static Map<String, Object> saveNotes(String id, String notes) throws IOException {
            Map<String, Object> session = load(id);
            session.put("notes", notes);
            save(id, session);
            return Map.of("ok", true);
        }

        @SuppressWarnings("unchecked")
        static Map<String, Object> finish(String id) throws IOException {
            Map<String, Object> session = load(id);
            if (session.get("finishedAt") == null) {
                Map<String, Object> last = Runner.run(id, "all", false);
                session = load(id);
                session.put("finalResults", last.get("sections"));
                session.put("finalCompileError", last.get("compileError"));
                // A phase that first passes in this final run still counts as passed, at the finish time.
                markPassed(session, (List<Map<String, Object>>) (List<?>) session.get("phases"), (List<Map<String, Object>>) (List<?>) last.get("sections"));
                session.put("finishedAt", System.currentTimeMillis());
                event(session, "finish", Map.of());
                save(id, session);
            }
            return view(id);
        }

        /** Records every phase whose tests are all green for the first time; returns those phase numbers. */
        @SuppressWarnings("unchecked")
        static List<Object> markPassed(Map<String, Object> session, List<Map<String, Object>> phases, List<Map<String, Object>> sections) {
            Map<String, Object> passedMap = (Map<String, Object>) session.get("phasesPassed");
            List<Object> newlyPassed = new ArrayList<>();
            for (int i = 0; i < phases.size(); i++) {
                String key = String.valueOf(i + 1);
                for (Map<String, Object> section : sections) {
                    boolean green = num(section, "failed") == 0 && num(section, "passed") > 0;
                    if (str(phases.get(i), "tests").equals(str(section, "name")) && green && !passedMap.containsKey(key)) {
                        passedMap.put(key, System.currentTimeMillis() - num(session, "startedAt"));
                        event(session, "phasePassed", Map.of("phase", (long) (i + 1)));
                        newlyPassed.add((long) (i + 1));
                    }
                }
            }
            return newlyPassed;
        }

        @SuppressWarnings("unchecked")
        static void event(Map<String, Object> session, String type, Map<String, Object> data) {
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("t", System.currentTimeMillis() - num(session, "startedAt"));
            e.put("type", type);
            e.putAll(data);
            ((List<Object>) session.get("events")).add(e);
        }

        /** Numbers that describe how the session went; shown on the finish screen and given to the reviewer. */
        @SuppressWarnings("unchecked")
        static Map<String, Object> report(Map<String, Object> session) {
            List<Map<String, Object>> events = (List<Map<String, Object>>) (List<?>) session.get("events");
            long end = session.get("finishedAt") == null ? System.currentTimeMillis() : num(session, "finishedAt");
            long total = end - num(session, "startedAt");
            int runs = 0;
            int compileErrors = 0;
            int prompts = 0;
            long firstRun = -1;
            long firstPrompt = -1;
            long lastRun = 0;
            long longestGap = 0;
            int promptStreak = 0;
            int longestPromptStreak = 0;
            int skipped = 0;
            for (Map<String, Object> e : events) {
                long t = num(e, "t");
                switch (str(e, "type")) {
                    case "run" -> {
                        runs++;
                        if (Boolean.TRUE.equals(e.get("compileError"))) {
                            compileErrors++;
                        }
                        if (firstRun < 0) {
                            firstRun = t;
                        }
                        longestGap = Math.max(longestGap, t - lastRun);
                        lastRun = t;
                        promptStreak = 0;
                    }
                    case "chat" -> {
                        prompts++;
                        if (firstPrompt < 0) {
                            firstPrompt = t;
                        }
                        promptStreak++;
                        longestPromptStreak = Math.max(longestPromptStreak, promptStreak);
                    }
                    case "skip" -> skipped++;
                    default -> { }
                }
            }
            longestGap = Math.max(longestGap, total - lastRun);
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("totalMs", total);
            r.put("runs", runs);
            r.put("compileErrors", compileErrors);
            r.put("prompts", prompts);
            r.put("firstRunMs", firstRun);
            r.put("firstPromptMs", firstPrompt);
            r.put("longestGapWithoutRunMs", longestGap);
            r.put("longestPromptStreak", longestPromptStreak);
            r.put("phasesSkipped", skipped);
            if (session.get("finishedAt") != null) {
                r.put("score", score(session, total, runs, prompts, longestGap, longestPromptStreak, skipped));
            }
            return r;
        }

        /**
         * A score out of 100 from what the tool can observe. Correctness 70 (final test run: phase 1 all or
         * nothing because most of its tests pass before any change; later phases in proportion), time 10,
         * working rhythm 20. Communication cannot be observed and is not scored.
         */
        @SuppressWarnings("unchecked")
        static Map<String, Object> score(Map<String, Object> session, long totalMs, int runs, int prompts,
                long longestGap, int promptStreak, int skipped) {
            List<Map<String, Object>> phases = (List<Map<String, Object>>) (List<?>) session.get("phases");
            List<Map<String, Object>> results = session.get("finalResults") instanceof List<?> l
                    ? (List<Map<String, Object>>) (List<?>) l : List.of();
            double[] weights = phases.size() == 3 ? new double[] {20, 30, 20} : new double[phases.size()];
            if (phases.size() != 3) {
                Arrays.fill(weights, 70.0 / Math.max(1, phases.size()));
            }
            List<Object> parts = new ArrayList<>();
            double correctness = 0;
            boolean solved = !phases.isEmpty();
            boolean phaseOne = false;
            for (int i = 0; i < phases.size(); i++) {
                long passed = 0;
                long failed = 0;
                for (Map<String, Object> section : results) {
                    if (str(section, "name").equals(str(phases.get(i), "tests"))) {
                        passed = num(section, "passed");
                        failed = num(section, "failed");
                    }
                }
                boolean green = passed > 0 && failed == 0;
                double fraction = passed + failed == 0 ? 0 : (double) passed / (passed + failed);
                double points = i == 0 ? (green ? weights[i] : 0) : weights[i] * fraction;
                if (i == 0) {
                    phaseOne = green;
                }
                solved &= green;
                correctness += points;
                parts.add(part("Phase " + (i + 1) + ": " + str(phases.get(i), "title"), points, weights[i],
                        passed + " of " + (passed + failed) + " tests pass in the final run" + (i == 0 && !green ? "; this phase scores only when all pass" : "")));
            }
            long limit = num(session, "minutes") * 60_000;
            double timeBase = totalMs <= limit ? 10 : Math.max(0, 10 - Math.ceil((totalMs - limit) / 120_000.0));
            double time = timeBase * correctness / 70.0;
            parts.add(part("Time", time, 10, totalMs <= limit ? "Finished inside the limit; scaled by how much was solved"
                    : "Over the limit; one point lost for every two minutes"));
            double rhythm = 0;
            String rhythmNote;
            if (!phaseOne || runs == 0) {
                rhythmNote = "Counted once phase 1 passes";
            } else {
                rhythm += prompts == 0 || promptStreak <= 2 ? 8 : promptStreak == 3 ? 4 : 0;
                rhythm += longestGap <= 8 * 60_000 ? 8 : longestGap <= 12 * 60_000 ? 4 : 0;
                rhythm += skipped == 0 ? 4 : 0;
                rhythmNote = "8 for never sending more than two prompts without a run, 8 for never going more than 8 minutes without a run, 4 for not skipping a phase";
            }
            parts.add(part("Working rhythm", rhythm, 20, rhythmNote));
            long total = Math.round(correctness + time + rhythm);
            Map<String, Object> score = new LinkedHashMap<>();
            score.put("total", total);
            // "Strong" is reserved for sessions where every phase passes.
            score.put("band", total >= 85 && solved ? "Strong" : total >= 70 ? "Solid" : total >= 50 ? "Partial" : "Needs work");
            score.put("solved", solved);
            score.put("parts", parts);
            return score;
        }

        static Map<String, Object> part(String label, double points, double max, String note) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("label", label);
            p.put("points", Math.round(points * 10) / 10.0);
            p.put("max", Math.round(max));
            p.put("note", note);
            return p;
        }

        static void copyTree(Path from, Path to) throws IOException {
            Files.walkFileTree(from, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path d, BasicFileAttributes a) throws IOException {
                    Files.createDirectories(to.resolve(from.relativize(d).toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path f, BasicFileAttributes a) throws IOException {
                    Files.copy(f, to.resolve(from.relativize(f).toString()), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    static long num(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v instanceof Number n ? n.longValue() : 0;
    }

    // ------------------------------------------------------------------ compile and run

    static final class Runner {

        @SuppressWarnings("unchecked")
        static Map<String, Object> run(String id, String target, boolean record) throws IOException {
            Map<String, Object> session = Sessions.load(id);
            Map<String, Object> problem = Sessions.problem(str(session, "problemId"));
            List<Map<String, Object>> phases = (List<Map<String, Object>>) (List<?>) problem.get("phases");
            Path work = Sessions.work(id);
            Path starter = root.resolve("problems").resolve(str(session, "problemId")).resolve("starter");

            // Tests and data generators are restored before every run, so they cannot be weakened.
            for (Object ro : (List<?>) problem.get("readOnly")) {
                Files.copy(starter.resolve(ro.toString()), work.resolve(ro.toString()), StandardCopyOption.REPLACE_EXISTING);
            }

            List<String> classes = new ArrayList<>();
            long timeoutSec = 20;
            boolean isMain = target.equals("main");
            if (target.equals("all")) {
                timeoutSec = 0;
                for (Map<String, Object> p : phases) {
                    classes.add(str(p, "tests"));
                    timeoutSec += num(p, "timeoutSec");
                }
            } else if (target.matches("phase[0-9]")) {
                int n = target.charAt(5) - '0';
                if (n < 1 || n > phases.size()) {
                    throw new UserError("Unknown phase.");
                }
                classes.add(str(phases.get(n - 1), "tests"));
                timeoutSec = num(phases.get(n - 1), "timeoutSec");
            } else if (!isMain) {
                throw new UserError("Unknown run target.");
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("target", target);
            long started = System.currentTimeMillis();

            Path out = Sessions.dir(id).resolve("out");
            deleteTree(out);
            Files.createDirectories(out);
            List<String> compile = new ArrayList<>(List.of(javaTool("javac"), "-encoding", "UTF-8", "-nowarn", "-d", out.toString()));
            try (Stream<Path> kit = Files.list(root.resolve("kit").resolve("kit"))) {
                kit.filter(p -> p.toString().endsWith(".java")).sorted().forEach(p -> compile.add(p.toString()));
            }
            try (Stream<Path> src = Files.list(work)) {
                src.filter(p -> p.toString().endsWith(".java")).sorted().forEach(p -> compile.add(p.getFileName().toString()));
            }
            Exec compiled = exec(compile, work, 90, null);
            if (compiled.exit != 0) {
                String text = compiled.timedOut ? "The compiler did not finish within 90 seconds.\n" : clean(compiled.output, work);
                result.put("compileError", true);
                result.put("output", "Compilation failed.\n\n" + text);
                result.put("sections", List.of());
            } else {
                List<String> cmd = new ArrayList<>(List.of(javaTool("java"), maxHeap(), "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-cp", out.toString()));
                if (isMain) {
                    cmd.add(str(problem, "main"));
                } else {
                    cmd.add("kit.TestRunner");
                    cmd.addAll(classes);
                }
                Exec ran = exec(cmd, work, timeoutSec, null);
                String text = clean(ran.output, work);
                if (ran.timedOut) {
                    text += "\n\nStopped: the run did not finish within " + timeoutSec + " seconds.\n";
                }
                result.put("compileError", false);
                result.put("output", text);
                result.put("exitCode", ran.exit);
                result.put("sections", isMain ? List.of() : sections(text, classes));
            }
            result.put("durationMs", System.currentTimeMillis() - started);

            if (record && session.get("finishedAt") == null) {
                List<Map<String, Object>> sections = (List<Map<String, Object>>) (List<?>) result.get("sections");
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("target", target);
                data.put("compileError", result.get("compileError"));
                data.put("passed", sections.stream().mapToLong(s -> num(s, "passed")).sum());
                data.put("failed", sections.stream().mapToLong(s -> num(s, "failed")).sum());
                Sessions.event(session, "run", data);
                List<Object> newlyPassed = Sessions.markPassed(session, phases, sections);
                // A phase opens when every phase before it has passed.
                Map<String, Object> passedMap = (Map<String, Object>) session.get("phasesPassed");
                long unlocked = num(session, "unlocked");
                while (unlocked < phases.size() && passedMap.containsKey(String.valueOf(unlocked))) {
                    unlocked++;
                }
                session.put("unlocked", unlocked);
                Sessions.save(id, session);
                result.put("newlyPassed", newlyPassed);
            }
            result.put("phasesPassed", session.get("phasesPassed"));
            result.put("unlocked", session.get("unlocked"));
            result.put("brief", Sessions.brief(str(session, "problemId"), num(session, "unlocked"), session.get("finishedAt") != null));
            result.put("events", session.get("events"));
            return result;
        }

        /** Splits runner output into one summary per test class, with one entry per test. */
        @SuppressWarnings("unchecked")
        static List<Object> sections(String text, List<String> classes) {
            List<Object> sections = new ArrayList<>();
            Map<String, Object> current = null;
            for (String line : text.split("\n")) {
                if (line.startsWith("== ")) {
                    current = new LinkedHashMap<>();
                    current.put("name", line.substring(3).split(" ")[0]);
                    current.put("passed", 0L);
                    current.put("failed", 0L);
                    current.put("tests", new ArrayList<>());
                    sections.add(current);
                    continue;
                }
                if (current == null) {
                    continue;
                }
                String status = line.startsWith("PASS  ") ? "pass" : line.startsWith("FAIL  ") ? "fail" : line.startsWith("TIMEOUT ") ? "timeout" : null;
                if (status == null) {
                    continue;
                }
                String rest = line.substring(status.equals("timeout") ? 8 : 6);
                int cut = status.equals("fail") ? rest.indexOf(':') : rest.indexOf(" (");
                Map<String, Object> test = new LinkedHashMap<>();
                test.put("name", cut < 0 ? rest : rest.substring(0, cut));
                test.put("status", status);
                String detail = cut < 0 ? "" : rest.substring(cut).replaceFirst("^[: ]+", "");
                test.put("detail", detail.length() > 300 ? detail.substring(0, 300) + "..." : detail);
                ((List<Object>) current.get("tests")).add(test);
                String key = status.equals("pass") ? "passed" : "failed";
                current.put(key, num(current, key) + 1);
            }
            return sections;
        }

        /** A quarter of the machine's memory, between 512 MB and 4 GB, so a runaway test swaps itself and not the laptop. */
        static String maxHeap() {
            long total = ((com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize();
            long mb = Math.max(512, Math.min(4096, total / 4 / (1024 * 1024)));
            return "-Xmx" + mb + "m";
        }

        static String javaTool(String name) {
            Path bin = Path.of(System.getProperty("java.home"), "bin");
            Path tool = bin.resolve(isWindows() ? name + ".exe" : name);
            return Files.exists(tool) ? tool.toString() : name;
        }

        static String clean(String output, Path work) {
            StringBuilder sb = new StringBuilder();
            String prefix = work.toString() + java.io.File.separator;
            for (String line : output.split("\n", -1)) {
                if (line.startsWith("Picked up JAVA_TOOL_OPTIONS") || line.startsWith("Picked up _JAVA_OPTIONS")) {
                    continue;
                }
                sb.append(line.replace(prefix, "").replace("\r", "")).append('\n');
            }
            return sb.toString().stripTrailing() + "\n";
        }

        static void deleteTree(Path dir) throws IOException {
            if (!Files.exists(dir)) {
                return;
            }
            try (Stream<Path> walk = Files.walk(dir)) {
                for (Path p : walk.sorted(Collections.reverseOrder()).toList()) {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException stillInUse) {
                        // Windows keeps a file locked for a moment after a killed run; the next compile overwrites it.
                    }
                }
            }
        }
    }

    record Exec(int exit, String output, boolean timedOut) {}

    static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    /** Runs a command, merging stderr into stdout, and kills it (with its children) on timeout. */
    static Exec exec(List<String> command, Path cwd, long timeoutSec, String stdin) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command).directory(cwd.toFile()).redirectErrorStream(true);
        Process process = pb.start();
        if (stdin != null) {
            try (OutputStream os = process.getOutputStream()) {
                os.write(stdin.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            process.getOutputStream().close();
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Thread reader = new Thread(() -> {
            try (InputStream in = process.getInputStream()) {
                byte[] chunk = new byte[8192];
                int n;
                while ((n = in.read(chunk)) > 0) {
                    synchronized (buffer) {
                        if (buffer.size() < 400_000) {
                            buffer.write(chunk, 0, n);
                        }
                    }
                }
            } catch (IOException ignored) {
                // process ended
            }
        });
        reader.setDaemon(true);
        reader.start();
        boolean finished;
        try {
            finished = process.waitFor(timeoutSec, TimeUnit.SECONDS);
            if (!finished) {
                process.descendants().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
            }
            reader.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            return new Exec(-1, "Interrupted.", true);
        }
        String text;
        synchronized (buffer) {
            text = buffer.toString(StandardCharsets.UTF_8);
            if (buffer.size() >= 400_000) {
                text += "\n... output cut off (too long) ...\n";
            }
        }
        return new Exec(finished ? process.exitValue() : -1, text, !finished);
    }

    // ------------------------------------------------------------------ Claude Code CLI

    static final class Claude {
        static final String SYSTEM = "You are the coding assistant built into a timed interview practice tool. "
                + "You answer in a chat panel. You cannot edit files, run code or use tools. Follow the instructions in the message.";

        static Map<String, Object> detect() {
            Map<String, Object> info = new LinkedHashMap<>();
            String exe = locate();
            info.put("available", false);
            if (exe == null) {
                info.put("problem", "The Claude Code CLI was not found on this computer's PATH.");
                return info;
            }
            try {
                Exec v = exec(wrap(exe, List.of("--version")), root, 20, null);
                if (v.exit == 0) {
                    info.put("available", true);
                    info.put("version", v.output.strip().lines().reduce((a, b) -> b).orElse(""));
                    info.put("path", exe);
                } else {
                    info.put("problem", "The Claude Code CLI was found at " + exe + " but did not start: " + v.output.strip());
                }
            } catch (IOException e) {
                info.put("problem", "The Claude Code CLI could not be started: " + e.getMessage());
            }
            return info;
        }

        /** INTERVUE_CLAUDE wins; otherwise the first claude executable on PATH or in the usual install folders. */
        static String locate() {
            String override = System.getenv("INTERVUE_CLAUDE");
            if (override != null && !override.isBlank()) {
                return override;
            }
            List<String> names = isWindows() ? List.of("claude.exe", "claude.cmd", "claude.bat") : List.of("claude");
            List<Path> dirs = new ArrayList<>();
            String pathVar = System.getenv("PATH");
            if (pathVar != null) {
                for (String d : pathVar.split(java.io.File.pathSeparator)) {
                    if (!d.isBlank()) {
                        try {
                            dirs.add(Path.of(d.replace("\"", "")));
                        } catch (RuntimeException ignored) {
                            // unreadable PATH entry
                        }
                    }
                }
            }
            String home = System.getProperty("user.home");
            dirs.add(Path.of(home, ".local", "bin"));
            dirs.add(Path.of(home, ".claude", "local"));
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                dirs.add(Path.of(appData, "npm"));
            }
            for (Path d : dirs) {
                for (String n : names) {
                    Path candidate = d.resolve(n);
                    if (Files.isRegularFile(candidate)) {
                        return candidate.toString();
                    }
                }
            }
            return null;
        }

        static boolean isScript(String exe) {
            String lower = exe.toLowerCase(Locale.ROOT);
            return isWindows() && (lower.endsWith(".cmd") || lower.endsWith(".bat"));
        }

        static List<String> wrap(String exe, List<String> args) {
            List<String> cmd = new ArrayList<>();
            if (isScript(exe)) {
                cmd.addAll(List.of("cmd.exe", "/c"));
            }
            cmd.add(exe);
            cmd.addAll(args);
            return cmd;
        }

        interface Sink {
            void delta(String text) throws IOException;
        }

        /**
         * Sends one prompt on stdin and streams the reply. The CLI runs in an empty scratch folder with its
         * tools switched off, so it can only talk. Returns the full reply text; throws UserError with a
         * readable reason when the CLI fails.
         */
        static String ask(String prompt, String model, Sink sink) throws IOException {
            if (!Boolean.TRUE.equals(claudeInfo.get("available"))) {
                throw new UserError(str(claudeInfo, "problem") + " Install Claude Code, sign in once by running `claude` in a terminal, then restart Intervue.");
            }
            String exe = str(claudeInfo, "path");
            Path scratch = root.resolve("sessions").resolve(".assistant-scratch");
            Files.createDirectories(scratch);
            List<String> args = new ArrayList<>(List.of("-p", "--model", model, "--output-format", "stream-json", "--verbose",
                    "--include-partial-messages", "--no-session-persistence", "--strict-mcp-config"));
            if (isScript(exe)) {
                // Arguments pass through cmd.exe here, so nothing that needs quoting.
                args.addAll(List.of("--disallowedTools", "Bash,Edit,Write,NotebookEdit,WebFetch,WebSearch,Read,Glob,Grep,Task,Agent"));
            } else {
                args.addAll(List.of("--tools", "", "--system-prompt", SYSTEM));
            }
            ProcessBuilder pb = new ProcessBuilder(wrap(exe, args)).directory(scratch.toFile());
            Process process = pb.start();
            StringBuilder stderr = new StringBuilder();
            Thread errReader = new Thread(() -> {
                try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        if (stderr.length() < 4000) {
                            stderr.append(line).append('\n');
                        }
                    }
                } catch (IOException ignored) {
                    // process ended
                }
            });
            errReader.setDaemon(true);
            errReader.start();
            Thread watchdog = new Thread(() -> {
                try {
                    if (!process.waitFor(240, TimeUnit.SECONDS)) {
                        process.descendants().forEach(ProcessHandle::destroyForcibly);
                        process.destroyForcibly();
                    }
                } catch (InterruptedException ignored) {
                    // finished normally
                }
            });
            watchdog.setDaemon(true);
            watchdog.start();
            try (OutputStream os = process.getOutputStream()) {
                os.write(prompt.getBytes(StandardCharsets.UTF_8));
            }
            StringBuilder reply = new StringBuilder();
            String errorText = null;
            StringBuilder unparsed = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    Map<String, Object> msg;
                    try {
                        msg = Json.object(line);
                    } catch (RuntimeException e) {
                        if (unparsed.length() < 2000) {
                            unparsed.append(line).append('\n');
                        }
                        continue;
                    }
                    String type = str(msg, "type");
                    if (type.equals("stream_event") && msg.get("event") instanceof Map<?, ?> event
                            && "content_block_delta".equals(event.get("type")) && event.get("delta") instanceof Map<?, ?> delta
                            && "text_delta".equals(delta.get("type"))) {
                        String text = String.valueOf(delta.get("text"));
                        reply.append(text);
                        sink.delta(text);
                    } else if (type.equals("result")) {
                        String result = str(msg, "result");
                        if (Boolean.TRUE.equals(msg.get("is_error"))) {
                            errorText = result.isBlank() ? "The Claude Code CLI reported an error." : result;
                        } else if (reply.length() == 0 && !result.isBlank()) {
                            reply.append(result);
                            sink.delta(result);
                        }
                    }
                }
            }
            try {
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            watchdog.interrupt();
            if (errorText != null) {
                throw new UserError(explain(errorText));
            }
            if (reply.length() == 0) {
                String detail = (stderr + "\n" + unparsed).strip();
                throw new UserError(explain(detail.isEmpty() ? "The Claude Code CLI returned nothing." : detail));
            }
            return reply.toString();
        }

        static String explain(String raw) {
            String lower = raw.toLowerCase(Locale.ROOT);
            if (lower.contains("login") || lower.contains("api key") || lower.contains("authenticat") || lower.contains("oauth")) {
                return "Claude Code is not signed in. Run `claude` in a terminal, sign in, then try again. (" + firstLine(raw) + ")";
            }
            if (lower.contains("unknown option") || lower.contains("unknown argument")) {
                return "This version of Claude Code does not accept an option Intervue uses. Update it with `claude update`. (" + firstLine(raw) + ")";
            }
            if (lower.contains("limit")) {
                return "Claude Code reports a usage limit. (" + firstLine(raw) + ")";
            }
            return "The assistant could not answer: " + firstLine(raw);
        }

        static String firstLine(String s) {
            String line = s.strip().lines().findFirst().orElse("");
            return line.length() > 300 ? line.substring(0, 300) + "..." : line;
        }
    }

    // ------------------------------------------------------------------ chat and feedback

    static final class Chat {
        static final List<String> MODELS = List.of("sonnet", "opus", "haiku");

        static void startStream(HttpExchange ex) throws IOException {
            ex.getResponseHeaders().set("Content-Type", "application/x-ndjson; charset=utf-8");
            ex.getResponseHeaders().set("Cache-Control", "no-store");
            ex.getResponseHeaders().set("X-Accel-Buffering", "no");
            ex.sendResponseHeaders(200, 0);
        }

        static void line(HttpExchange ex, Map<String, Object> value) throws IOException {
            OutputStream os = ex.getResponseBody();
            os.write((Json.write(value) + "\n").getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        @SuppressWarnings("unchecked")
        static void chat(HttpExchange ex, String id, Map<String, Object> body, Object lock) throws IOException {
            String message = str(body, "message").strip();
            String model = MODELS.contains(str(body, "model")) ? str(body, "model") : "sonnet";
            boolean restricted = !str(body, "mode").equals("helpful");
            if (message.isEmpty()) {
                throw new UserError("Type a message first.");
            }
            String prompt;
            synchronized (lock) {
                Map<String, Object> session = Sessions.load(id);
                if (session.get("finishedAt") != null) {
                    throw new UserError("This session is finished.");
                }
                prompt = chatPrompt(id, session, message, restricted);
                Map<String, Object> turn = new LinkedHashMap<>();
                turn.put("role", "user");
                turn.put("text", message);
                turn.put("t", System.currentTimeMillis() - num(session, "startedAt"));
                ((List<Object>) session.get("chat")).add(turn);
                Sessions.event(session, "chat", Map.of("model", model, "mode", restricted ? "interview" : "helpful", "chars", (long) message.length()));
                Sessions.save(id, session);
            }
            startStream(ex);
            String reply;
            String error = null;
            try {
                reply = Claude.ask(prompt, model, text -> line(ex, Map.of("t", "delta", "text", text)));
            } catch (UserError e) {
                reply = "";
                error = e.getMessage();
            }
            synchronized (lock) {
                Map<String, Object> session = Sessions.load(id);
                Map<String, Object> turn = new LinkedHashMap<>();
                turn.put("role", error == null ? "assistant" : "error");
                turn.put("text", error == null ? reply : error);
                turn.put("model", model);
                turn.put("t", System.currentTimeMillis() - num(session, "startedAt"));
                ((List<Object>) session.get("chat")).add(turn);
                Sessions.save(id, session);
                line(ex, error == null ? Map.of("t", "done", "events", session.get("events")) : Map.of("t", "error", "message", error, "events", session.get("events")));
            }
        }

        @SuppressWarnings("unchecked")
        static String chatPrompt(String id, Map<String, Object> session, String message, boolean restricted) throws IOException {
            StringBuilder p = new StringBuilder();
            p.append("You are the AI assistant built into a timed coding-interview practice environment. ");
            p.append("A candidate is working on a small multi-file Java project and talks to you in a chat panel.\n\n");
            p.append("Hard rules:\n");
            p.append("- You cannot edit files, run code or use tools. You only reply in chat; the candidate copies code by hand.\n");
            p.append("- The current content of every project file is included below. Never ask the candidate to paste a file.\n");
            p.append("- Be concise. Put code in fenced blocks with a language tag. Plain Java ").append(Runtime.version().feature()).append(", no external libraries.\n");
            p.append("- Tests, Datasets and the files under data/ are read-only; never suggest changing them.\n\n");
            if (restricted) {
                p.append("Interview mode is ON. The interviewer has limited what you may volunteer:\n");
                p.append("- Do not point out bugs, defects or missing cases unless the candidate asks about that specific code or behaviour. ");
                p.append("When asked to describe or review code in general terms, describe what it does and how it is structured, without diagnosing it.\n");
                p.append("- Do not give a complete solution to a phase unprompted. If asked broadly to 'solve', 'fix it' or 'make the tests pass', ");
                p.append("ask which approach they want, or offer two or three options in one line each, and stop.\n");
                p.append("- When the candidate states an approach or asks for a specific, well-defined piece (a named method, a data structure, a test, ");
                p.append("a complexity estimate, an explanation of a named piece of code), do exactly that piece well, and nothing beyond it.\n");
                p.append("- Do not suggest designs, optimisations or pitfalls for work the candidate has not asked about, including later phases.\n");
                p.append("- If the candidate asks a direct, specific question (for example 'is the visited check in fewestHops correct?'), answer it honestly.\n\n");
            } else {
                p.append("Helpful mode: answer as a capable coding assistant would, including pointing out problems you notice.\n\n");
            }
            p.append("## Task brief (what the candidate can currently see)\n\n");
            for (Object part : Sessions.brief(str(session, "problemId"), num(session, "unlocked"), false)) {
                if (part != null) {
                    p.append(part).append("\n");
                }
            }
            p.append("\n## Project files (current saved state)\n\n");
            Path work = Sessions.work(id);
            try (Stream<Path> walk = Files.walk(work, 3)) {
                for (Path f : walk.filter(Files::isRegularFile).sorted().toList()) {
                    String rel = work.relativize(f).toString().replace('\\', '/');
                    String content = Files.readString(f);
                    if (content.length() > 30_000) {
                        content = content.substring(0, 30_000) + "\n... (cut)\n";
                    }
                    p.append("### ").append(rel).append("\n```").append(rel.endsWith(".java") ? "java" : "").append("\n").append(content);
                    if (!content.endsWith("\n")) {
                        p.append('\n');
                    }
                    p.append("```\n\n");
                }
            }
            List<Map<String, Object>> history = (List<Map<String, Object>>) (List<?>) session.get("chat");
            List<Map<String, Object>> usable = history.stream().filter(t -> !str(t, "role").equals("error")).toList();
            int from = Math.max(0, usable.size() - 16);
            if (from < usable.size()) {
                p.append("## Conversation so far\n\n");
                for (Map<String, Object> turn : usable.subList(from, usable.size())) {
                    String text = str(turn, "text");
                    if (text.length() > 6000) {
                        text = text.substring(0, 6000) + "\n... (cut)";
                    }
                    p.append(str(turn, "role").equals("user") ? "Candidate: " : "Assistant: ").append(text).append("\n\n");
                }
            }
            p.append("## The candidate's new message\n\n").append(message).append("\n");
            return p.toString();
        }

        @SuppressWarnings("unchecked")
        static void feedback(HttpExchange ex, String id, Map<String, Object> body, Object lock) throws IOException {
            String model = MODELS.contains(str(body, "model")) ? str(body, "model") : "sonnet";
            String prompt;
            synchronized (lock) {
                Map<String, Object> session = Sessions.load(id);
                if (session.get("finishedAt") == null) {
                    throw new UserError("Finish the session first.");
                }
                prompt = feedbackPrompt(id, session);
            }
            startStream(ex);
            try {
                String reply = Claude.ask(prompt, model, text -> line(ex, Map.of("t", "delta", "text", text)));
                synchronized (lock) {
                    Map<String, Object> session = Sessions.load(id);
                    session.put("feedback", reply);
                    Sessions.save(id, session);
                }
                line(ex, Map.of("t", "done"));
            } catch (UserError e) {
                line(ex, Map.of("t", "error", "message", e.getMessage()));
            }
        }

        @SuppressWarnings("unchecked")
        static String feedbackPrompt(String id, Map<String, Object> session) throws IOException {
            String problemId = str(session, "problemId");
            Path problemDir = root.resolve("problems").resolve(problemId);
            Map<String, Object> problem = Sessions.problem(problemId);
            StringBuilder p = new StringBuilder();
            p.append("You are an experienced interviewer grading a practice session for an AI-assisted coding interview ");
            p.append("(structured format: multi-file Java codebase, chat-only AI assistant, three phases: fix a bug, implement a feature, handle large inputs).\n\n");
            p.append("Grade the candidate strictly and specifically from the evidence below. You have the brief, the grader notes with the intended ");
            p.append("solution, the timeline of runs and prompts, the full chat with the assistant, the final test results and the final code. ");
            p.append("You have no audio, so judge communication only from how the candidate wrote prompts and their own notes; say so.\n\n");
            p.append("Write the review in Markdown with exactly these sections:\n");
            p.append("1. `## Verdict` - one of Strong hire, Hire, Lean no hire, No hire, plus two sentences of why.\n");
            p.append("2. `## Scores` - a table with a 1-4 score and one line of evidence for each: Problem solving and approach; Control of the AI ");
            p.append("(did the candidate make the decisions?); Verification (run rhythm, checking output); Code quality; Communication (from prompts and notes only).\n");
            p.append("3. `## What the assistant got wrong` - mistakes or gaps in the assistant's replies, and for each whether the candidate caught it. Say 'none found' if so.\n");
            p.append("4. `## Phase by phase` - for each phase: what happened, what a strong candidate would have done differently. Quote prompts and times.\n");
            p.append("5. `## Two changes for next time` - the two highest-leverage habits to change, concrete.\n");
            p.append("6. `## Follow-up answers` - a short model answer (2-3 sentences) for each follow-up question listed below.\n\n");
            p.append("Be direct. Do not praise by default. Times are minutes:seconds from the start. Session length allowed: ")
                    .append(num(session, "minutes")).append(" minutes.\n\n");

            p.append("## Brief\n\n");
            for (Object part : Sessions.brief(problemId, 99, true)) {
                p.append(part).append("\n");
            }
            p.append("\n## Grader notes (never shown to the candidate)\n\n").append(Files.readString(problemDir.resolve("solution").resolve("NOTES.md"))).append("\n");
            p.append("\n## Follow-up questions\n\n");
            for (Object q : (List<?>) problem.get("followUps")) {
                p.append("- ").append(q).append("\n");
            }

            p.append("\n## Summary numbers\n\n").append(Json.write(Sessions.report(session))).append("\n");
            p.append("Phases first passed at (ms from start): ").append(Json.write(session.get("phasesPassed"))).append("\n");
            p.append("Final test results per class: ").append(Json.write(session.get("finalResults")));
            if (Boolean.TRUE.equals(session.get("finalCompileError"))) {
                p.append(" (the final code did not compile)");
            }
            p.append("\n\n## Timeline\n\n");
            for (Map<String, Object> e : (List<Map<String, Object>>) (List<?>) session.get("events")) {
                p.append(clock(num(e, "t"))).append("  ").append(str(e, "type"));
                switch (str(e, "type")) {
                    case "run" -> p.append(" ").append(str(e, "target")).append(Boolean.TRUE.equals(e.get("compileError")) ? " -> compile error"
                            : " -> " + num(e, "passed") + " passed, " + num(e, "failed") + " failed");
                    case "chat" -> p.append(" (").append(str(e, "model")).append(", ").append(str(e, "mode")).append(" mode)");
                    case "phasePassed", "skip" -> p.append(" phase ").append(num(e, "phase"));
                    case "reset" -> p.append(" ").append(str(e, "path"));
                    default -> { }
                }
                p.append("\n");
            }
            p.append("\n## Chat with the assistant\n\n");
            List<Map<String, Object>> chat = (List<Map<String, Object>>) (List<?>) session.get("chat");
            if (chat.isEmpty()) {
                p.append("(The candidate never used the assistant.)\n");
            }
            for (Map<String, Object> turn : chat) {
                String text = str(turn, "text");
                boolean user = str(turn, "role").equals("user");
                if (!user && text.length() > 2500) {
                    text = text.substring(0, 2500) + "\n... (cut)";
                }
                p.append("[").append(clock(num(turn, "t"))).append("] ").append(user ? "CANDIDATE" : str(turn, "role").toUpperCase(Locale.ROOT)).append(": ").append(text).append("\n\n");
            }
            p.append("## Candidate's own notes after the session\n\n").append(str(session, "notes").isBlank() ? "(none)" : str(session, "notes")).append("\n");

            p.append("\n## Final code (files that differ from the starting project, or are new)\n\n");
            Path work = Sessions.work(id);
            Path starter = problemDir.resolve("starter");
            boolean any = false;
            try (Stream<Path> walk = Files.walk(work, 3)) {
                for (Path f : walk.filter(Files::isRegularFile).sorted().toList()) {
                    String rel = work.relativize(f).toString().replace('\\', '/');
                    Path original = starter.resolve(rel);
                    String content = Files.readString(f);
                    if (Files.exists(original) && Files.readString(original).equals(content)) {
                        continue;
                    }
                    any = true;
                    if (content.length() > 30_000) {
                        content = content.substring(0, 30_000) + "\n... (cut)\n";
                    }
                    p.append("### ").append(rel).append(Files.exists(original) ? "" : " (new file)").append("\n```java\n").append(content).append("\n```\n\n");
                }
            }
            if (!any) {
                p.append("(No file was changed.)\n");
            }
            return p.toString();
        }

        static String clock(long ms) {
            long s = ms / 1000;
            return String.format("%02d:%02d", s / 60, s % 60);
        }
    }

    // ------------------------------------------------------------------ JSON

    /** A small JSON reader and writer: objects become LinkedHashMap, arrays ArrayList, integers Long. */
    static final class Json {
        private final String s;
        private int i;

        private Json(String s) {
            this.s = s;
        }

        @SuppressWarnings("unchecked")
        static Map<String, Object> object(String text) {
            Object v = parse(text);
            if (!(v instanceof Map)) {
                throw new IllegalArgumentException("expected a JSON object");
            }
            return (Map<String, Object>) v;
        }

        static Object parse(String text) {
            Json j = new Json(text);
            j.ws();
            Object v = j.value();
            j.ws();
            if (j.i != text.length()) {
                throw new IllegalArgumentException("unexpected text after JSON value at " + j.i);
            }
            return v;
        }

        private void ws() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }

        private Object value() {
            if (i >= s.length()) {
                throw new IllegalArgumentException("unexpected end of JSON");
            }
            char c = s.charAt(i);
            if (c == '{') {
                i++;
                Map<String, Object> map = new LinkedHashMap<>();
                ws();
                if (s.charAt(i) == '}') {
                    i++;
                    return map;
                }
                while (true) {
                    ws();
                    String key = string();
                    ws();
                    expect(':');
                    ws();
                    map.put(key, value());
                    ws();
                    if (s.charAt(i) == ',') {
                        i++;
                    } else {
                        expect('}');
                        return map;
                    }
                }
            }
            if (c == '[') {
                i++;
                List<Object> list = new ArrayList<>();
                ws();
                if (s.charAt(i) == ']') {
                    i++;
                    return list;
                }
                while (true) {
                    ws();
                    list.add(value());
                    ws();
                    if (s.charAt(i) == ',') {
                        i++;
                    } else {
                        expect(']');
                        return list;
                    }
                }
            }
            if (c == '"') {
                return string();
            }
            if (s.startsWith("true", i)) {
                i += 4;
                return Boolean.TRUE;
            }
            if (s.startsWith("false", i)) {
                i += 5;
                return Boolean.FALSE;
            }
            if (s.startsWith("null", i)) {
                i += 4;
                return null;
            }
            int start = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) {
                i++;
            }
            String number = s.substring(start, i);
            if (number.isEmpty()) {
                throw new IllegalArgumentException("unexpected character in JSON at " + i);
            }
            if (number.contains(".") || number.contains("e") || number.contains("E")) {
                return Double.parseDouble(number);
            }
            return Long.parseLong(number);
        }

        private void expect(char c) {
            if (i >= s.length() || s.charAt(i) != c) {
                throw new IllegalArgumentException("expected '" + c + "' in JSON at " + i);
            }
            i++;
        }

        private String string() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char e = s.charAt(i++);
                switch (e) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        i += 4;
                    }
                    default -> sb.append(e);
                }
            }
        }

        static String write(Object value) {
            StringBuilder sb = new StringBuilder();
            write(sb, value);
            return sb.toString();
        }

        private static void write(StringBuilder sb, Object v) {
            if (v == null) {
                sb.append("null");
            } else if (v instanceof String str) {
                sb.append('"');
                for (int k = 0; k < str.length(); k++) {
                    char c = str.charAt(k);
                    switch (c) {
                        case '"' -> sb.append("\\\"");
                        case '\\' -> sb.append("\\\\");
                        case '\n' -> sb.append("\\n");
                        case '\r' -> sb.append("\\r");
                        case '\t' -> sb.append("\\t");
                        default -> {
                            if (c < 0x20 || c == 0x2028 || c == 0x2029) {
                                sb.append(String.format("\\u%04x", (int) c));
                            } else {
                                sb.append(c);
                            }
                        }
                    }
                }
                sb.append('"');
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(v);
            } else if (v instanceof Map<?, ?> map) {
                sb.append('{');
                boolean first = true;
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    if (!first) {
                        sb.append(',');
                    }
                    first = false;
                    write(sb, String.valueOf(e.getKey()));
                    sb.append(':');
                    write(sb, e.getValue());
                }
                sb.append('}');
            } else if (v instanceof Iterable<?> list) {
                sb.append('[');
                boolean first = true;
                for (Object o : list) {
                    if (!first) {
                        sb.append(',');
                    }
                    first = false;
                    write(sb, o);
                }
                sb.append(']');
            } else if (v instanceof Object[] array) {
                write(sb, Arrays.asList(array));
            } else {
                write(sb, v.toString());
            }
        }
    }
}
