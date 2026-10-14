package si.intervue.engine.ai;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

/** The command that runs {@code FakeClaude.java} for one scenario, writing into {@code dir}. */
public final class FakeCli {
  private FakeCli() {}

  public static List<String> command(String scenario, Path dir) {
    return List.of(java(), source().toString(), scenario, dir.toString());
  }

  private static String java() {
    return Path.of(System.getProperty("java.home"), "bin", "java").toString();
  }

  private static Path source() {
    try {
      return Path.of(FakeCli.class.getResource("/fake-claude/FakeClaude.java").toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
