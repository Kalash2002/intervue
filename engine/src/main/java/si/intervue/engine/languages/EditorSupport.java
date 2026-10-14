package si.intervue.engine.languages;

import java.nio.file.Path;
import java.util.List;

/** What the editor asks a language for: project symbols to complete, and compiler diagnostics. */
public interface EditorSupport {

  /** Classes, methods and fields declared in {@code files} (paths relative to the work folder). */
  List<SourceSymbol> symbols(Path workDir, List<String> files);

  /** Compiler errors and warnings for the whole project, without producing any output files. */
  List<SourceDiagnostic> diagnostics(Path workDir);
}
