package si.intervue.languages.java;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import si.intervue.engine.languages.CompileResult;
import si.intervue.engine.languages.RunOutput;
import si.intervue.engine.languages.RunRequest;
import si.intervue.engine.languages.Runner;
import si.intervue.engine.languages.SourceDiagnostic;
import si.intervue.engine.runners.ProcessControl;

/**
 * Compiles a flat Java project plus the kit with the in-process compiler, and runs tests or the
 * demo entry point in a child JVM of the bundled runtime. Read-only files are the engine's concern.
 */
final class JavaRunner implements Runner {

  private static final String RESULTS = "results.jsonl";

  @Override
  public CompileResult compile(Path workDir, Path outDir) {
    recreate(outDir);
    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
    List<String> options =
        List.of("-encoding", "UTF-8", "-nowarn", "-proc:none", "-d", outDir.toString());
    boolean ok;
    try (StandardJavaFileManager files =
        javac.getStandardFileManager(collector, Locale.ROOT, StandardCharsets.UTF_8)) {
      ok =
          javac
              .getTask(
                  null,
                  files,
                  collector,
                  options,
                  null,
                  files.getJavaFileObjectsFromPaths(sources(workDir)))
              .call();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    List<SourceDiagnostic> diagnostics = new ArrayList<>();
    for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
      if (d.getSource() != null) {
        diagnostics.add(toDiagnostic(workDir, d));
      }
    }
    return new CompileResult(ok, ok ? "" : failureText(diagnostics), diagnostics);
  }

  /** Top-level sources plus the installed kit; starter projects are flat by the layout rule. */
  private static List<Path> sources(Path workDir) {
    List<Path> sources = new ArrayList<>();
    for (Path dir : List.of(workDir, workDir.resolve("kit"))) {
      if (!Files.isDirectory(dir)) {
        continue;
      }
      try (Stream<Path> list = Files.list(dir)) {
        list.filter(p -> p.toString().endsWith(".java")).sorted().forEach(sources::add);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    return sources;
  }

  private static SourceDiagnostic toDiagnostic(
      Path workDir, Diagnostic<? extends JavaFileObject> d) {
    String path =
        workDir
            .relativize(Path.of(d.getSource().toUri()))
            .toString()
            .replace(File.separatorChar, '/');
    String severity =
        switch (d.getKind()) {
          case ERROR -> "error";
          case WARNING, MANDATORY_WARNING -> "warning";
          default -> "note";
        };
    return new SourceDiagnostic(
        path, d.getLineNumber(), d.getColumnNumber(), severity, d.getMessage(Locale.ROOT));
  }

  private static String failureText(List<SourceDiagnostic> diagnostics) {
    StringBuilder text = new StringBuilder("Compilation failed.\n\n");
    long errors = 0;
    for (SourceDiagnostic d : diagnostics) {
      text.append(d.path()).append(':').append(d.line()).append(": ").append(d.severity());
      text.append(": ").append(d.message()).append('\n');
      errors += "error".equals(d.severity()) ? 1 : 0;
    }
    return text.append(errors).append(errors == 1 ? " error\n" : " errors\n").toString();
  }

  @Override
  public RunOutput run(RunRequest request, ProcessControl processes) {
    List<String> testClasses = testClasses(request);
    boolean isMain = request.target().equals("main");
    List<String> command =
        new ArrayList<>(List.of(javaExecutable(), "-Xmx" + request.limits().memoryMb() + "m"));
    command.addAll(
        List.of("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8"));
    // The child runs inside the work folder, so a relative out folder would resolve against it.
    Path outDir = request.outDir().toAbsolutePath();
    command.addAll(List.of("-cp", outDir.toString()));
    Path results = outDir.resolve(RESULTS);
    if (isMain) {
      command.add(request.variant().main());
    } else {
      command.addAll(List.of("kit.TestRunner", "--results", results.toString()));
      command.addAll(List.of("--factor", String.valueOf(request.calibrationFactor())));
      command.addAll(testClasses);
    }
    ProcessControl.Result ran =
        processes.run(
            command, request.workDir(), request.limits().wallMs(), request.limits().outputBytes());
    String text = clean(ran.output(), request.workDir());
    if (ran.outputCut()) {
      text += "\n... output cut off (too long) ...\n";
    }
    if (ran.timedOut()) {
      long seconds = (request.limits().wallMs() + 999) / 1000;
      text += "\n\nStopped: the run did not finish within " + seconds + " seconds.\n";
    }
    return new RunOutput(text, ran.exitCode(), ran.timedOut(), isMain ? null : results);
  }

  /** Test classes for the target, in phase order; refuses a target the variant does not have. */
  private static List<String> testClasses(RunRequest request) {
    String target = request.target();
    var tests = request.variant().tests();
    if (target.equals("main")) {
      return List.of();
    }
    if (target.equals("all")) {
      return tests.entrySet().stream()
          .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
          .map(Map.Entry::getValue)
          .toList();
    }
    if (target.startsWith("phase:") && tests.containsKey(target.substring(6))) {
      return List.of(tests.get(target.substring(6)));
    }
    throw new IllegalArgumentException("Unknown run target: " + target);
  }

  /** Hides the work folder's absolute path and JVM start-up chatter, as the prototype did. */
  private static String clean(String output, Path workDir) {
    String prefix = workDir.toString() + File.separator;
    StringBuilder text = new StringBuilder();
    for (String line : output.split("\n", -1)) {
      if (line.startsWith("Picked up JAVA_TOOL_OPTIONS")
          || line.startsWith("Picked up _JAVA_OPTIONS")) {
        continue;
      }
      text.append(line.replace(prefix, "").replace("\r", "")).append('\n');
    }
    return text.toString().stripTrailing() + "\n";
  }

  static String javaExecutable() {
    boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    Path tool = Path.of(System.getProperty("java.home"), "bin", windows ? "java.exe" : "java");
    return Files.exists(tool) ? tool.toString() : "java";
  }

  private static void recreate(Path dir) {
    try {
      if (Files.exists(dir)) {
        try (Stream<Path> walk = Files.walk(dir)) {
          for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) {
            Files.delete(p);
          }
        }
      }
      Files.createDirectories(dir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
