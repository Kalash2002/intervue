import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Checks a problem three ways: the starter fails its first phase, the reference solution passes every
 * test, and the naive solution passes every phase but the last and times out there.
 *
 * Run from the Dryrun folder:  java tools/Verify.java [problem-id ...]   (default: every problem)
 * Exit code 0 when every problem checked out.
 */
public class Verify {
    static final Path ROOT = Path.of("").toAbsolutePath();
    static final Path SCRATCH = ROOT.resolve("sessions").resolve(".verify");
    static final long RUN_CAP_SEC = 900;

    public static void main(String[] args) throws Exception {
        if (!Files.isDirectory(ROOT.resolve("problems")) || !Files.isDirectory(ROOT.resolve("kit"))) {
            System.err.println("Run this from the Dryrun folder: java tools/Verify.java");
            System.exit(2);
        }
        List<String> ids = new ArrayList<>(List.of(args));
        if (ids.isEmpty()) {
            try (Stream<Path> s = Files.list(ROOT.resolve("problems"))) {
                s.filter(Files::isDirectory).sorted().forEach(d -> ids.add(d.getFileName().toString()));
            }
        }
        boolean allOk = true;
        for (String id : ids) {
            allOk &= verify(id);
        }
        deleteTree(SCRATCH);
        System.out.println(allOk ? "All problems verified." : "Some problems FAILED verification.");
        System.exit(allOk ? 0 : 1);
    }

    static boolean verify(String id) throws Exception {
        Path problem = ROOT.resolve("problems").resolve(id);
        List<String> phases = new ArrayList<>();
        Matcher m = Pattern.compile("\"tests\"\\s*:\\s*\"([^\"]+)\"").matcher(Files.readString(problem.resolve("problem.json")));
        while (m.find()) {
            phases.add(m.group(1));
        }
        String first = phases.get(0);
        String last = phases.get(phases.size() - 1);
        System.out.println("## " + id + " (" + phases.size() + " phases)");
        boolean ok = check("starter", run(id, "starter", problem, null, phases),
                r -> r.failed(first) + r.timeouts(first) > 0, "the first phase must have a failing test");
        ok &= check("reference", run(id, "reference", problem, problem.resolve("solution"), phases),
                r -> r.totalFailed() == 0, "every test must pass");
        Path naive = problem.resolve("solution").resolve("naive");
        if (!Files.isDirectory(naive)) {
            System.out.println("  naive      MISSING: add solution/naive/ with a correct but slow solution");
            ok = false;
        } else {
            ok &= check("naive", run(id, "naive", problem, naive, phases),
                    r -> r.timeouts(last) > 0 && r.failedOutside(last) == 0,
                    "every phase but the last must pass and the last must have a TIMEOUT");
        }
        System.out.println(ok ? "OK      " + id : "FAILED  " + id);
        System.out.println();
        return ok;
    }

    static boolean check(String variant, Result r, Predicate<Result> rule, String ruleText) {
        boolean ok = !r.compileError && rule.test(r);
        StringBuilder line = new StringBuilder(String.format("  %-10s %-5s %6.1f s  ", variant, ok ? "ok" : "FAIL", r.durationMs / 1000.0));
        if (r.compileError) {
            line.append("did not compile: ").append(r.output.lines().filter(l -> l.contains("error")).findFirst().orElse(""));
        } else {
            r.perClass.forEach((cls, c) -> line.append(cls).append(' ').append(c[0]).append('/').append(c[0] + c[1] + c[2])
                    .append(c[2] > 0 ? " (" + c[2] + " timeout)" : "").append("  "));
            if (!ok) {
                line.append("<- ").append(ruleText);
            }
        }
        System.out.println(line);
        return ok;
    }

    /** Copies the starter, lays the overlay's .java files over it, compiles with the kit and runs every phase. */
    static Result run(String id, String variant, Path problem, Path overlay, List<String> phases) throws Exception {
        Path base = SCRATCH.resolve(id).resolve(variant);
        deleteTree(base);
        Path work = base.resolve("work");
        Path out = base.resolve("out");
        Files.createDirectories(out);
        copyTree(problem.resolve("starter"), work);
        if (overlay != null) {
            try (Stream<Path> s = Files.list(overlay)) {
                for (Path f : s.filter(p -> p.toString().endsWith(".java")).toList()) {
                    Files.copy(f, work.resolve(f.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        long started = System.currentTimeMillis();
        List<String> compile = new ArrayList<>(List.of(tool("javac"), "-encoding", "UTF-8", "-nowarn", "-d", out.toString()));
        try (Stream<Path> s = Files.list(ROOT.resolve("kit").resolve("kit"))) {
            s.filter(p -> p.toString().endsWith(".java")).sorted().forEach(p -> compile.add(p.toString()));
        }
        try (Stream<Path> s = Files.list(work)) {
            s.filter(p -> p.toString().endsWith(".java")).sorted().forEach(p -> compile.add(p.getFileName().toString()));
        }
        Exec compiled = exec(compile, work, 120);
        Result result = new Result();
        if (compiled.exit != 0) {
            result.compileError = true;
            result.output = compiled.output;
        } else {
            List<String> cmd = new ArrayList<>(List.of(tool("java"), "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-cp", out.toString(), "kit.TestRunner"));
            cmd.addAll(phases);
            Exec ran = exec(cmd, work, RUN_CAP_SEC);
            result.output = ran.output;
            result.parse(ran.output);
        }
        result.durationMs = System.currentTimeMillis() - started;
        return result;
    }

    static final class Result {
        boolean compileError;
        long durationMs;
        String output = "";
        final Map<String, int[]> perClass = new LinkedHashMap<>(); // passed, failed, timeout

        void parse(String text) {
            int[] current = null;
            for (String line : text.split("\n")) {
                if (line.startsWith("== ")) {
                    current = perClass.computeIfAbsent(line.substring(3).split(" ")[0], k -> new int[3]);
                } else if (current != null && line.startsWith("PASS  ")) {
                    current[0]++;
                } else if (current != null && line.startsWith("FAIL  ")) {
                    current[1]++;
                } else if (current != null && line.startsWith("TIMEOUT ")) {
                    current[2]++;
                }
            }
        }

        int failed(String cls) {
            return perClass.getOrDefault(cls, new int[3])[1];
        }

        int timeouts(String cls) {
            return perClass.getOrDefault(cls, new int[3])[2];
        }

        int totalFailed() {
            return perClass.values().stream().mapToInt(c -> c[1] + c[2]).sum();
        }

        int failedOutside(String cls) {
            return perClass.entrySet().stream().filter(e -> !e.getKey().equals(cls)).mapToInt(e -> e.getValue()[1] + e.getValue()[2]).sum();
        }
    }

    record Exec(int exit, String output) {}

    static Exec exec(List<String> command, Path cwd, long timeoutSec) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).directory(cwd.toFile()).redirectErrorStream(true).start();
        process.getOutputStream().close();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Thread reader = new Thread(() -> {
            try (InputStream in = process.getInputStream()) {
                in.transferTo(buffer);
            } catch (IOException ignored) {
                // process ended
            }
        });
        reader.start();
        boolean finished = process.waitFor(timeoutSec, TimeUnit.SECONDS);
        if (!finished) {
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
        }
        reader.join(5000);
        return new Exec(finished ? process.exitValue() : -1, buffer.toString(StandardCharsets.UTF_8));
    }

    static String tool(String name) {
        boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
        Path tool = Path.of(System.getProperty("java.home"), "bin", windows ? name + ".exe" : name);
        return Files.exists(tool) ? tool.toString() : name;
    }

    static void copyTree(Path from, Path to) throws IOException {
        try (Stream<Path> s = Files.walk(from)) {
            for (Path p : s.toList()) {
                Path target = to.resolve(from.relativize(p).toString());
                if (Files.isDirectory(p)) {
                    Files.createDirectories(target);
                } else {
                    Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    static void deleteTree(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> s = Files.walk(dir)) {
            for (Path p : s.sorted(Collections.reverseOrder()).toList()) {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException stillInUse) {
                    // Windows keeps a file locked for a moment after a killed run
                }
            }
        }
    }
}
