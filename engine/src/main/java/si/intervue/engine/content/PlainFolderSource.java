package si.intervue.engine.content;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import si.intervue.problemformat.ProblemFolder;

/** Development source: every valid problem folder under {@code content/problems}. */
public final class PlainFolderSource implements ContentStore {

  private final Path root;
  private final ProblemDirectory problems;

  public PlainFolderSource(Path root) {
    this.root = root;
    this.problems = new ProblemDirectory(root);
  }

  @Override
  public List<String> ids() {
    try (Stream<Path> dirs = Files.list(root)) {
      return dirs.filter(Files::isDirectory)
          .map(d -> d.getFileName().toString())
          .sorted()
          .filter(problems::isValid)
          .toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public ProblemFolder load(String id) {
    return problems.load(id);
  }
}
