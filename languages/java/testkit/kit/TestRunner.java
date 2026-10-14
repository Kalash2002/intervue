package kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs every @Test method of the classes named on the command line and writes the results in the
 * run protocol (docs/contracts/run-protocol.md) to the file given with {@code --results}; stdout
 * gets the same outcomes for humans and is never parsed. The tests of a class run in a child JVM: a
 * test that hits its time limit ends that JVM at once, so the thread it left behind cannot slow the
 * tests after it, which continue in a fresh JVM. Every limit is multiplied by {@code --factor}.
 *
 * <p>Usage: {@code TestRunner --results <file> [--factor <f>] <TestClass>...}. The child's entry is
 * {@code --from <TestClass> <index>} with the same options.
 */
public final class TestRunner {
  static final String RUNNER = "java-kit/2.0";

  /** How long a child may outlive the limits of its tests before the parent kills it. */
  private static final long GRACE_MS = 10_000;

  private enum Outcome {
    PASS,
    FAIL,
    TIMEOUT
  }

  private record Options(Path results, double factor, String fromClass, int fromIndex, List<String> classes) {
    static Options parse(String[] args) {
      Path results = null;
      double factor = 1.0;
      String fromClass = null;
      int fromIndex = 0;
      List<String> classes = new ArrayList<>();
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "--results" -> results = Path.of(args[++i]);
          case "--factor" -> factor = Double.parseDouble(args[++i]);
          case "--from" -> {
            fromClass = args[++i];
            fromIndex = Integer.parseInt(args[++i]);
          }
          default -> classes.add(args[i]);
        }
      }
      if (results == null) {
        throw new IllegalArgumentException("--results <file> is required");
      }
      return new Options(results, factor, fromClass, fromIndex, classes);
    }

    List<String> childArgs(String className, int index) {
      return List.of("--results", results.toString(), "--factor", String.valueOf(factor), "--from", className, String.valueOf(index));
    }

    long scaled(long refMs) {
      return Math.round(refMs * factor);
    }
  }

  /** What one child JVM reported before it ended. */
  private record ChildCounts(int passed, int failed, int timeouts) {
    int reported() {
      return passed + failed + timeouts;
    }
  }

  private TestRunner() {}

  public static void main(String[] args) throws Exception {
    Options options = Options.parse(args);
    if (options.fromClass() != null) {
      System.exit(runChild(options) ? 0 : 1);
    }
    System.exit(runParent(options) ? 0 : 1);
  }

  private static boolean runParent(Options options) throws Exception {
    long startNs = System.nanoTime();
    Files.deleteIfExists(options.results());
    write(options, "{\"protocol\":1,\"t\":\"start\",\"runner\":" + quote(RUNNER) + ",\"language\":\"java\",\"factor\":" + options.factor() + "}");
    int passed = 0;
    int failed = 0;
    int timeouts = 0;
    for (String className : options.classes()) {
      ChildCounts counts = runClass(options, className);
      passed += counts.passed();
      failed += counts.failed();
      timeouts += counts.timeouts();
    }
    long wallMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
    write(options, "{\"t\":\"summary\",\"passed\":" + passed + ",\"failed\":" + failed + ",\"timeouts\":" + timeouts + ",\"compileError\":false,\"wallMs\":" + wallMs + "}");
    System.out.println();
    System.out.println("RESULT passed=" + passed + " failed=" + (failed + timeouts) + " total=" + (passed + failed + timeouts));
    return failed == 0 && timeouts == 0;
  }

  private static ChildCounts runClass(Options options, String className) throws Exception {
    List<Method> tests;
    try {
      // Not initialised here: static initialisers run candidate code, which belongs in the child.
      tests = tests(Class.forName(className, false, TestRunner.class.getClassLoader()));
    } catch (ClassNotFoundException | LinkageError e) {
      String detail = e instanceof ClassNotFoundException ? "test class not found" : "test class could not be loaded: " + e;
      write(options, "{\"t\":\"suite\",\"name\":" + quote(className) + ",\"tests\":0}");
      writeTest(options, className, className, "error", 0, detail, null, null);
      System.out.println("FAIL  " + className + ": " + detail);
      return new ChildCounts(0, 1, 0);
    }
    write(options, "{\"t\":\"suite\",\"name\":" + quote(className) + ",\"tests\":" + tests.size() + "}");
    System.out.println("== " + className + " (" + tests.size() + " tests)");
    int passed = 0;
    int failed = 0;
    int timeouts = 0;
    int next = 0;
    while (next < tests.size()) {
      ChildCounts counts = runInChild(options, className, tests, next);
      passed += counts.passed();
      failed += counts.failed();
      timeouts += counts.timeouts();
      next += counts.reported();
    }
    return new ChildCounts(passed, failed, timeouts);
  }

  private static List<Method> tests(Class<?> type) {
    List<Method> tests = new ArrayList<>();
    for (Method m : type.getDeclaredMethods()) {
      if (m.isAnnotationPresent(Test.class)) {
        tests.add(m);
      }
    }
    tests.sort(Comparator.comparingInt((Method m) -> m.getAnnotation(Test.class).order()).thenComparing(Method::getName));
    return tests;
  }

  /**
   * Runs the tests from {@code from} on in a JVM like this one, relaying its output. If the child
   * ends before reporting a test, the parent reports that test itself so the run always moves on.
   */
  private static ChildCounts runInChild(Options options, String className, List<Method> tests, int from) throws Exception {
    long budgetMs = GRACE_MS;
    for (Method test : tests.subList(from, tests.size())) {
      budgetMs += options.scaled(test.getAnnotation(Test.class).timeoutMs());
    }
    System.out.flush();
    long resultsBefore = Files.exists(options.results()) ? Files.size(options.results()) : 0;
    Process child = new ProcessBuilder(childCommand(options, className, from)).redirectErrorStream(true).start();
    child.getOutputStream().close();
    AtomicBoolean killed = new AtomicBoolean();
    Thread watchdog = startWatchdog(child, budgetMs, killed);
    relayChildOutput(child);
    child.waitFor();
    int[] counts = countChildResults(options, resultsBefore);
    watchdog.interrupt();
    ChildCounts reported = new ChildCounts(counts[0], counts[1], counts[2]);
    int current = from + reported.reported();
    boolean endedOnTimeout = counts[3] == 1;
    if (current >= tests.size() || endedOnTimeout) {
      return reported;
    }
    String name = tests.get(current).getName();
    if (killed.get()) {
      writeTest(options, className, name, "timeout", budgetMs, null, null, budgetMs);
      System.out.println("TIMEOUT " + name + " (killed after " + budgetMs + " ms)");
      return new ChildCounts(reported.passed(), reported.failed(), reported.timeouts() + 1);
    }
    String detail = "the test JVM ended early with exit code " + child.exitValue();
    writeTest(options, className, name, "error", 0, detail, null, null);
    System.out.println("FAIL  " + name + ": " + detail);
    return new ChildCounts(reported.passed(), reported.failed() + 1, reported.timeouts());
  }

  private static List<String> childCommand(Options options, String className, int from) {
    List<String> command = new ArrayList<>();
    command.add(javaExecutable());
    for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
      if (!arg.startsWith("-Dsun.java.") && !arg.startsWith("-Djava.class.path=")) {
        command.add(arg); // heap limit, encodings and whatever else this JVM was given
      }
    }
    command.addAll(List.of("-cp", System.getProperty("java.class.path"), TestRunner.class.getName()));
    command.addAll(options.childArgs(className, from));
    return command;
  }

  private static Thread startWatchdog(Process child, long budgetMs, AtomicBoolean killed) {
    Thread watchdog =
        new Thread(
            () -> {
              try {
                if (!child.waitFor(budgetMs, TimeUnit.MILLISECONDS)) {
                  killed.set(true);
                  child.descendants().forEach(ProcessHandle::destroyForcibly);
                  child.destroyForcibly();
                }
              } catch (InterruptedException e) {
                // The child finished in time; nothing to kill.
              }
            });
    watchdog.setDaemon(true);
    watchdog.start();
    return watchdog;
  }

  /** Relays child output to ours; it is for people only, since the candidate's prints are in it too. */
  private static void relayChildOutput(Process child) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream(), outputCharset()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    }
  }

  /**
   * Counts the test lines the child appended to the results file: {passed, failed, timeouts,
   * endedOnTimeout}. Only the kit writes that file, so candidate output cannot skew the counts.
   */
  private static int[] countChildResults(Options options, long from) throws IOException {
    int[] counts = new int[4];
    if (!Files.exists(options.results())) {
      return counts;
    }
    byte[] all = Files.readAllBytes(options.results());
    String appended = new String(all, (int) from, all.length - (int) from, StandardCharsets.UTF_8);
    for (String line : appended.split("\n")) {
      if (!line.startsWith("{\"t\":\"test\"")) {
        continue;
      }
      boolean timeout = line.contains("\"status\":\"timeout\"");
      counts[line.contains("\"status\":\"pass\"") ? 0 : timeout ? 2 : 1]++;
      counts[3] = timeout ? 1 : 0;
    }
    return counts;
  }

  private static boolean runChild(Options options) throws Exception {
    Class<?> type = Class.forName(options.fromClass());
    List<Method> tests = tests(type);
    boolean allPassed = true;
    for (Method test : tests.subList(options.fromIndex(), tests.size())) {
      Outcome outcome = runOne(options, type, test);
      allPassed &= outcome == Outcome.PASS;
      if (outcome == Outcome.TIMEOUT) {
        break; // the stuck thread dies with this JVM; the parent runs the rest in a new one
      }
    }
    System.out.flush();
    return allPassed;
  }

  private static Outcome runOne(Options options, Class<?> type, Method test) throws InterruptedException {
    long limitMs = options.scaled(test.getAnnotation(Test.class).timeoutMs());
    AtomicReference<Throwable> error = new AtomicReference<>();
    Thread worker = new Thread(() -> invoke(type, test, error), "test-" + test.getName());
    worker.setDaemon(true);
    long start = System.nanoTime();
    worker.start();
    worker.join(limitMs);
    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    String suite = type.getName();
    if (worker.isAlive()) {
      writeTest(options, suite, test.getName(), "timeout", tookMs, null, null, limitMs);
      System.out.println("TIMEOUT " + test.getName() + " (still running after " + limitMs + " ms)");
      return Outcome.TIMEOUT;
    }
    Throwable t = error.get();
    if (t == null) {
      writeTest(options, suite, test.getName(), "pass", tookMs, null, null, limitMs);
      System.out.println("PASS  " + test.getName() + " (" + tookMs + " ms)");
      return Outcome.PASS;
    }
    String detail = (t instanceof AssertionError ? "" : t.getClass().getSimpleName() + ": ") + t.getMessage();
    String at = firstCandidateFrame(t);
    writeTest(options, suite, test.getName(), "fail", tookMs, detail, at, limitMs);
    System.out.println("FAIL  " + test.getName() + ": " + detail);
    if (at != null) {
      System.out.println("        at " + at);
    }
    return Outcome.FAIL;
  }

  private static void invoke(Class<?> type, Method test, AtomicReference<Throwable> error) {
    try {
      Object instance = type.getDeclaredConstructor().newInstance();
      test.setAccessible(true);
      test.invoke(instance);
    } catch (InvocationTargetException e) {
      error.set(e.getCause());
    } catch (Throwable t) {
      // Errors too (ExceptionInInitializerError, StackOverflowError): an uncaught one would end the
      // worker silently and the test would be reported as passed.
      error.set(t);
    }
  }

  /** The first frame in the candidate's or the test's code, skipping the kit and the JDK. */
  private static String firstCandidateFrame(Throwable t) {
    for (StackTraceElement frame : t.getStackTrace()) {
      String cls = frame.getClassName();
      if (cls.startsWith("kit.") || cls.startsWith("java.") || cls.startsWith("jdk.") || cls.startsWith("sun.")) {
        continue;
      }
      // Built by hand: StackTraceElement.toString() prefixes the class loader name ("app//").
      return cls + "." + frame.getMethodName() + "(" + frame.getFileName() + ":" + frame.getLineNumber() + ")";
    }
    return null;
  }

  private static void writeTest(Options options, String suite, String name, String status, long ms, String detail, String at, Long limitMs) {
    StringBuilder line = new StringBuilder("{\"t\":\"test\",\"suite\":").append(quote(suite));
    line.append(",\"name\":").append(quote(name)).append(",\"status\":").append(quote(status)).append(",\"ms\":").append(ms);
    if (detail != null) {
      line.append(",\"detail\":").append(quote(detail));
    }
    if (at != null) {
      line.append(",\"at\":").append(quote(at));
    }
    if (limitMs != null) {
      line.append(",\"limitMs\":").append(limitMs);
    }
    write(options, line.append('}').toString());
  }

  // Parent and child append in turn (the parent waits for each child), so lines never interleave.
  private static void write(Options options, String line) {
    try {
      Files.writeString(options.results(), line + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** A JSON string literal; the kit has no dependencies, so it escapes by hand. */
  static String quote(String s) {
    StringBuilder out = new StringBuilder("\"");
    for (char c : s.toCharArray()) {
      switch (c) {
        case '"' -> out.append("\\\"");
        case '\\' -> out.append("\\\\");
        case '\n' -> out.append("\\n");
        case '\r' -> out.append("\\r");
        case '\t' -> out.append("\\t");
        default -> {
          if (c < 0x20) {
            out.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
          } else {
            out.append(c);
          }
        }
      }
    }
    return out.append('"').toString();
  }

  private static Charset outputCharset() {
    try {
      String name = System.getProperty("stdout.encoding");
      return name == null ? Charset.defaultCharset() : Charset.forName(name);
    } catch (RuntimeException e) {
      return Charset.defaultCharset();
    }
  }

  private static String javaExecutable() {
    boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    Path tool = Path.of(System.getProperty("java.home"), "bin", windows ? "java.exe" : "java");
    return Files.exists(tool) ? tool.toString() : "java";
  }
}
