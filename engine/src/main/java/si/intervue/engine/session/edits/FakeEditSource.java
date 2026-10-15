package si.intervue.engine.session.edits;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Recorded Write code replies bundled in the engine, replayed by id, so edits, decorations and undo
 * can be tried before Phase 1b connects the backend. Only bundled streams can be replayed: an id is
 * a plain name, never a path. Delete this class when the backend's Write code channel lands.
 */
public final class FakeEditSource {

  private static final Pattern ID = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");
  private static final List<String> BUNDLED = List.of("hold-exposure-phase1");

  private FakeEditSource() {}

  public static List<String> ids() {
    return BUNDLED;
  }

  public static List<String> lines(String id) {
    if (id == null || !ID.matcher(id).matches() || !BUNDLED.contains(id)) {
      throw new IllegalArgumentException("No recorded reply named " + id);
    }
    try (InputStream in =
        FakeEditSource.class.getResourceAsStream("/fake-edits/" + id + ".jsonl")) {
      if (in == null) {
        throw new IllegalStateException("Recorded reply " + id + " is missing from the engine");
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
