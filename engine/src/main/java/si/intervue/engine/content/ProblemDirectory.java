package si.intervue.engine.content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import si.intervue.problemformat.ProblemFolder;
import si.intervue.problemformat.ProblemFormatException;
import si.intervue.problemformat.ProblemReader;
import si.intervue.problemformat.ProblemValidator;

/**
 * Reads and validates problem folders under one root; shared by the folder-based sources. Content
 * is read-only while the engine runs, so each folder is read and validated once and the outcome
 * kept.
 */
final class ProblemDirectory {

  // Problem ids are lowercase words joined by dashes, so an id can never climb out of the root.
  private static final Pattern ID = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

  static boolean isProblemId(String id) {
    return id != null && ID.matcher(id).matches();
  }

  /** A folder that passed validation, or why it cannot be used. */
  private record Checked(ProblemFolder folder, List<String> problems) {}

  private final Path root;
  private final Map<String, Checked> checked = new ConcurrentHashMap<>();

  ProblemDirectory(Path root) {
    this.root = root;
  }

  boolean isValid(String id) {
    return check(id).problems().isEmpty();
  }

  ProblemFolder load(String id) {
    Checked c = check(id);
    if (!c.problems().isEmpty()) {
      throw new ContentException(
          "Problem " + id + " cannot be used: " + String.join("; ", c.problems()));
    }
    return c.folder();
  }

  private Checked check(String id) {
    if (!isProblemId(id)) {
      return new Checked(null, List.of("not a problem id"));
    }
    return checked.computeIfAbsent(id, this::read);
  }

  private Checked read(String id) {
    Path dir = root.resolve(id);
    if (!Files.isRegularFile(dir.resolve("problem.json"))) {
      return new Checked(null, List.of("no such problem"));
    }
    try {
      ProblemFolder folder = ProblemReader.read(dir);
      return new Checked(folder, ProblemValidator.validate(folder));
    } catch (ProblemFormatException e) {
      return new Checked(null, List.of(e.getMessage()));
    }
  }
}
