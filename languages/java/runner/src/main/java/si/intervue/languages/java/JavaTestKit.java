package si.intervue.languages.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import si.intervue.engine.languages.TestKit;

/**
 * Copies the kit sources from the jar into {@code work/kit/}; they compile with the candidate's
 * code.
 */
final class JavaTestKit implements TestKit {

  private static final List<String> FILES =
      List.of("kit/Test.java", "kit/Check.java", "kit/TestRunner.java");

  @Override
  public void install(Path workDir) {
    for (String file : FILES) {
      Path target = workDir.resolve(file);
      try (InputStream in = JavaTestKit.class.getResourceAsStream("/" + file)) {
        if (in == null) {
          throw new IllegalStateException(file + " is missing from the Java package jar");
        }
        Files.createDirectories(target.getParent());
        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  @Override
  public List<String> files() {
    return FILES;
  }
}
