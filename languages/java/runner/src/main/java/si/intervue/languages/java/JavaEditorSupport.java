package si.intervue.languages.java;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import si.intervue.engine.languages.EditorSupport;
import si.intervue.engine.languages.SourceDiagnostic;
import si.intervue.engine.languages.SourceSymbol;

/**
 * Editor tier 1 for Java (PLAN.md 2.8): declarations from the JDK's own syntax tree for completion,
 * and compiler diagnostics from an analyze-only pass that writes no class files.
 */
final class JavaEditorSupport implements EditorSupport {

  private static final List<String> OPTIONS =
      List.of("-proc:none", "-encoding", "UTF-8", "-Xlint:none");

  @Override
  public List<SourceSymbol> symbols(Path workDir, List<String> files) {
    List<Path> paths = files.stream().map(workDir::resolve).toList();
    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    // Parse errors are ignored on purpose: completion should keep working while a file is broken.
    DiagnosticCollector<JavaFileObject> ignored = new DiagnosticCollector<>();
    try (StandardJavaFileManager fm =
        javac.getStandardFileManager(ignored, Locale.ROOT, StandardCharsets.UTF_8)) {
      JavacTask task =
          (JavacTask)
              javac.getTask(
                  null, fm, ignored, OPTIONS, null, fm.getJavaFileObjectsFromPaths(paths));
      SourcePositions positions = Trees.instance(task).getSourcePositions();
      List<SourceSymbol> symbols = new ArrayList<>();
      for (CompilationUnitTree unit : task.parse()) {
        String path = JavaRunner.relative(workDir, Path.of(unit.getSourceFile().toUri()));
        new Declarations(unit, positions, path, symbols).scan(unit, "");
      }
      return symbols;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public List<SourceDiagnostic> diagnostics(Path workDir) {
    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
    try (StandardJavaFileManager fm =
        javac.getStandardFileManager(collector, Locale.ROOT, StandardCharsets.UTF_8)) {
      JavacTask task =
          (JavacTask)
              javac.getTask(
                  null,
                  fm,
                  collector,
                  OPTIONS,
                  null,
                  fm.getJavaFileObjectsFromPaths(JavaRunner.sources(workDir)));
      // parse() then analyze() in one task; call() after parse() throws "endPosTable already set".
      task.parse();
      task.analyze();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    List<SourceDiagnostic> diagnostics = new ArrayList<>();
    for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
      if (d.getSource() != null) {
        diagnostics.add(JavaRunner.toDiagnostic(workDir, d));
      }
    }
    return diagnostics;
  }

  /** Collects types, methods and fields; method bodies are skipped, so locals never appear. */
  private static final class Declarations extends TreeScanner<Void, String> {
    private final CompilationUnitTree unit;
    private final SourcePositions positions;
    private final String path;
    private final List<SourceSymbol> out;

    Declarations(
        CompilationUnitTree unit, SourcePositions positions, String path, List<SourceSymbol> out) {
      this.unit = unit;
      this.positions = positions;
      this.path = path;
      this.out = out;
    }

    @Override
    public Void visitClass(ClassTree tree, String container) {
      String kind =
          tree.getKind().name().toLowerCase(Locale.ROOT).replace("annotation_type", "annotation");
      add(
          tree.getSimpleName().toString(),
          kind,
          container,
          kind + " " + tree.getSimpleName(),
          tree);
      for (Tree member : tree.getMembers()) {
        scan(member, tree.getSimpleName().toString());
      }
      return null;
    }

    @Override
    public Void visitMethod(MethodTree tree, String container) {
      String name = tree.getName().toString();
      if (!name.equals("<init>")) {
        String params =
            tree.getParameters().stream()
                .map(p -> p.getType() + " " + p.getName())
                .collect(Collectors.joining(", "));
        add(
            name,
            "method",
            container,
            tree.getReturnType() + " " + name + "(" + params + ")",
            tree);
      }
      return null;
    }

    @Override
    public Void visitVariable(VariableTree tree, String container) {
      add(
          tree.getName().toString(),
          "field",
          container,
          tree.getType() + " " + tree.getName(),
          tree);
      return null;
    }

    private void add(String name, String kind, String container, String detail, Tree tree) {
      long start = positions.getStartPosition(unit, tree);
      long line = start < 0 ? 0 : unit.getLineMap().getLineNumber(start);
      long column = start < 0 ? 0 : unit.getLineMap().getColumnNumber(start);
      out.add(new SourceSymbol(name, kind, container, detail, path, line, column));
    }
  }
}
