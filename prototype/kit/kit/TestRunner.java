package kit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs every @Test method of the classes named on the command line and prints one line per test.
 * The tests of a class run in a child JVM. A test that hits its time limit ends that JVM at once, so the
 * thread it left behind cannot slow down the tests after it; those continue in a fresh JVM.
 * {@code --from Class index} is the child's entry point.
 */
public final class TestRunner {
    /** How long a child may outlive the time limits of its tests before the parent kills it. */
    private static final long GRACE_MS = 10_000;

    private enum Outcome { PASS, FAIL, TIMEOUT }

    private TestRunner() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 3 && args[0].equals("--from")) {
            Class<?> type = Class.forName(args[1]);
            List<Method> tests = tests(type);
            boolean allPassed = true;
            for (Method test : tests.subList(Integer.parseInt(args[2]), tests.size())) {
                Outcome outcome = runOne(type, test);
                allPassed &= outcome == Outcome.PASS;
                if (outcome == Outcome.TIMEOUT) {
                    break; // the stuck thread dies with this JVM; the parent runs the rest in a new one
                }
            }
            System.out.flush();
            System.exit(allPassed ? 0 : 1);
        }
        int passed = 0;
        int failed = 0;
        for (String className : args) {
            Class<?> type;
            try {
                type = Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.out.println("FAIL  " + className + ": test class not found");
                failed++;
                continue;
            }
            List<Method> tests = tests(type);
            System.out.println("== " + className + " (" + tests.size() + " tests)");
            int next = 0;
            while (next < tests.size()) {
                int[] counts = runInChild(className, tests, next);
                passed += counts[0];
                failed += counts[1];
                next += counts[0] + counts[1];
            }
        }
        System.out.println();
        System.out.println("RESULT passed=" + passed + " failed=" + failed + " total=" + (passed + failed));
        System.out.flush();
        System.exit(failed == 0 ? 0 : 1);
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
     * Runs the tests from {@code from} on in a JVM like this one, relaying its output line by line.
     * Returns {passed, failed} for the tests that got a result; the caller continues from there.
     */
    private static int[] runInChild(String className, List<Method> tests, int from) throws Exception {
        long budget = GRACE_MS;
        for (Method test : tests.subList(from, tests.size())) {
            budget += test.getAnnotation(Test.class).timeoutMs();
        }
        final long budgetMs = budget;
        List<String> command = new ArrayList<>();
        command.add(javaExecutable());
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (!arg.startsWith("-Dsun.java.") && !arg.startsWith("-Djava.class.path=")) {
                command.add(arg); // heap limit, encodings and whatever else this JVM was given
            }
        }
        command.addAll(List.of("-cp", System.getProperty("java.class.path"), TestRunner.class.getName(), "--from", className, String.valueOf(from)));
        System.out.flush();
        Process child = new ProcessBuilder(command).redirectErrorStream(true).start();
        child.getOutputStream().close();
        AtomicBoolean killed = new AtomicBoolean();
        Thread watchdog = new Thread(() -> {
            try {
                if (!child.waitFor(budgetMs, TimeUnit.MILLISECONDS)) {
                    killed.set(true);
                    child.descendants().forEach(ProcessHandle::destroyForcibly);
                    child.destroyForcibly();
                }
            } catch (InterruptedException ignored) {
                // child finished in time
            }
        });
        watchdog.setDaemon(true);
        watchdog.start();
        int passed = 0;
        int failed = 0;
        boolean endedOnTimeout = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream(), outputCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("PASS  ")) {
                    passed++;
                    endedOnTimeout = false;
                } else if (line.startsWith("FAIL  ")) {
                    failed++;
                    endedOnTimeout = false;
                } else if (line.startsWith("TIMEOUT ")) {
                    failed++;
                    endedOnTimeout = true;
                }
            }
        }
        child.waitFor();
        watchdog.interrupt();
        int current = from + passed + failed;
        if (current < tests.size() && !endedOnTimeout) {
            // The child ended before it reported this test: killed by the watchdog, or the JVM died under it.
            String name = tests.get(current).getName();
            System.out.println(killed.get()
                    ? "TIMEOUT " + name + " (killed after " + budgetMs + " ms)"
                    : "FAIL  " + name + ": the test JVM ended early with exit code " + child.exitValue());
            failed++;
        }
        return new int[] {passed, failed};
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

    private static Outcome runOne(Class<?> type, Method test) throws InterruptedException {
        long timeoutMs = test.getAnnotation(Test.class).timeoutMs();
        AtomicReference<Throwable> error = new AtomicReference<>();
        Thread worker = new Thread(() -> {
            try {
                Object instance = type.getDeclaredConstructor().newInstance();
                test.setAccessible(true);
                test.invoke(instance);
            } catch (InvocationTargetException e) {
                error.set(e.getCause());
            } catch (Throwable t) {
                error.set(t);
            }
        }, "test-" + test.getName());
        worker.setDaemon(true);
        long start = System.nanoTime();
        worker.start();
        worker.join(timeoutMs);
        long tookMs = (System.nanoTime() - start) / 1_000_000;
        if (worker.isAlive()) {
            System.out.println("TIMEOUT " + test.getName() + " (still running after " + timeoutMs + " ms)");
            return Outcome.TIMEOUT;
        }
        Throwable t = error.get();
        if (t == null) {
            System.out.println("PASS  " + test.getName() + " (" + tookMs + " ms)");
            return Outcome.PASS;
        }
        String kind = t instanceof AssertionError ? "" : t.getClass().getSimpleName() + ": ";
        System.out.println("FAIL  " + test.getName() + ": " + kind + t.getMessage());
        for (StackTraceElement frame : t.getStackTrace()) {
            String cls = frame.getClassName();
            if (cls.startsWith("kit.") || cls.startsWith("java.") || cls.startsWith("jdk.")) {
                continue;
            }
            System.out.println("        at " + frame);
            if (cls.equals(type.getName())) {
                break;
            }
        }
        return Outcome.FAIL;
    }
}
