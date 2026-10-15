package si.intervue.engine.content;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import si.intervue.problemformat.ProblemFolder;

/**
 * Development source: every valid problem folder under the content roots ({@code content/problems},
 * and {@code content/dsa} when it exists). The free list in the first root's sibling {@code
 * free/free.json} marks problems free when it exists. An id in two roots is a loud error.
 */
public final class PlainFolderSource implements ContentStore {

  private final List<Path> roots;
  private final List<ProblemDirectory> problems;
  private final List<String> free;

  public PlainFolderSource(Path root) {
    this(List.of(root));
  }

  public PlainFolderSource(List<Path> roots) {
    this.roots = List.copyOf(roots);
    this.problems = this.roots.stream().map(ProblemDirectory::new).toList();
    Path freeJson = this.roots.getFirst().resolveSibling("free").resolve("free.json");
    this.free = Files.isRegularFile(freeJson) ? FreeProblemSource.readIds(freeJson) : List.of();
    Set<String> seen = new HashSet<>();
    for (Path root : this.roots) {
      for (String id : folderNames(root)) {
        if (!seen.add(id)) {
          throw new ContentException("Problem id " + id + " exists in more than one content root");
        }
      }
    }
  }

  @Override
  public boolean isFree(String id) {
    return free.contains(id);
  }

  @Override
  public List<String> ids() {
    List<String> ids = new ArrayList<>();
    for (int i = 0; i < roots.size(); i++) {
      for (String id : folderNames(roots.get(i))) {
        if (problems.get(i).isValid(id)) {
          ids.add(id);
        }
      }
    }
    return ids;
  }

  @Override
  public ProblemFolder load(String id) {
    for (int i = 0;
        id != null && i < roots.size();
        i++) { // a null id gets the "not a problem id" error below
      if (Files.isDirectory(roots.get(i).resolve(id))) {
        return problems.get(i).load(id);
      }
    }
    return problems.getFirst().load(id); // not found: the usual "no such problem" error
  }

  /** Sorted folder names of a root; a root that does not exist (content/dsa) has none. */
  private static List<String> folderNames(Path root) {
    if (!Files.isDirectory(root)) {
      return List.of();
    }
    try (Stream<Path> dirs = Files.list(root)) {
      return dirs.filter(Files::isDirectory).map(d -> d.getFileName().toString()).sorted().toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
