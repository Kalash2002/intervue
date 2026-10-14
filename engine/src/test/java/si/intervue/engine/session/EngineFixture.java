package si.intervue.engine.session;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import si.intervue.engine.content.PlainFolderSource;
import si.intervue.engine.languages.LanguageRegistry;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.engine.sections.SectionRegistry;
import si.intervue.engine.session.edits.EditStore;
import si.intervue.engine.store.Database;
import si.intervue.engine.store.Settings;

/** Builds the session module over a temp folder with the tiny fixture problem and a test clock. */
final class EngineFixture implements AutoCloseable {

  final Path root;
  final Path problems;
  final TestClock clock = new TestClock();
  final Database db;
  final SessionStore store;
  final Settings settings;
  final Sessions sessions;

  EngineFixture(Path root) {
    this.root = root;
    this.problems = root.resolve("problems");
    copyTree(resource("/problems/tiny"), problems.resolve("tiny"));
    this.db = Database.open(root.resolve("intervue.db"));
    this.store = new SessionStore(db);
    this.settings = new Settings(db);
    this.sessions =
        new Sessions(
            store,
            new EditStore(db),
            settings,
            new PlainFolderSource(problems),
            LanguageRegistry.load(),
            SectionRegistry.load(),
            root.resolve("sessions"),
            clock,
            new ProcessControl());
  }

  Path source(String relative) {
    return problems.resolve("tiny/variants/java/starter").resolve(relative);
  }

  Path work(String sessionId) {
    return root.resolve("sessions").resolve(sessionId).resolve("work");
  }

  static final String FIXED_CALC =
      "public class Calc {\n  static int twice(int n) {\n    return n + n;\n  }\n\n"
          + "  static int sum(int[] values) {\n    return 0;\n  }\n}\n";

  static final String FULL_CALC =
      "public class Calc {\n  static int twice(int n) {\n    return n + n;\n  }\n\n"
          + "  static int sum(int[] values) {\n    int s = 0;\n    for (int v : values) {\n"
          + "      s += v;\n    }\n    return s;\n  }\n}\n";

  private static Path resource(String name) {
    try {
      return Path.of(EngineFixture.class.getResource(name).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  static void copyTree(Path from, Path to) {
    try (Stream<Path> walk = Files.walk(from)) {
      for (Path p : walk.toList()) {
        Path target = to.resolve(from.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else {
          Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    db.close();
  }
}
