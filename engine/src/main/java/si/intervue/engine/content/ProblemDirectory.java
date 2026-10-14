package si.intervue.engine.content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import si.intervue.problemformat.ProblemFolder;
import si.intervue.problemformat.ProblemFormatException;
import si.intervue.problemformat.ProblemReader;
import si.intervue.problemformat.ProblemValidator;

/** Reads and validates problem folders under one root; shared by the folder-based sources. */
final class ProblemDirectory {

  // Problem ids are lowercase words joined by dashes, so an id can never climb out of the root.
  private static final Pattern ID = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

  private final Path root;

  ProblemDirectory(Path root) {
    this.root = root;
  }

  boolean isValid(String id) {
    return problems(id).isEmpty();
  }

  ProblemFolder load(String id) {
    List<String> problems = problems(id);
    if (!problems.isEmpty()) {
      throw new ContentException(
          "Problem " + id + " cannot be used: " + String.join("; ", problems));
    }
    return ProblemReader.read(root.resolve(id));
  }

  /** Why {@code id} cannot be used; empty when it can. */
  private List<String> problems(String id) {
    if (id == null || !ID.matcher(id).matches()) {
      return List.of("not a problem id");
    }
    Path dir = root.resolve(id);
    if (!Files.isRegularFile(dir.resolve("problem.json"))) {
      return List.of("no such problem");
    }
    try {
      return ProblemValidator.validate(ProblemReader.read(dir));
    } catch (ProblemFormatException e) {
      return List.of(e.getMessage());
    }
  }
}
