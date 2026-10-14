package si.intervue.engine.session;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * A session's copy of the project. Owns every rule about which paths exist, which are read-only,
 * and how files are restored from the problem's starter; the starter itself is never written.
 */
final class WorkFolder {

  private static final Pattern SAFE_PATH = Pattern.compile("[A-Za-z0-9_./-]{1,120}");
  private static final Set<String> TEXT_EXTENSIONS = Set.of(".txt", ".md", ".csv");

  private final Path work;
  private final Path starter;
  private final Set<String> readOnly;
  private final List<String> kitFiles;
  private final List<String> sourceExtensions;

  WorkFolder(
      Path work,
      Path starter,
      List<String> readOnly,
      List<String> kitFiles,
      List<String> sourceExtensions) {
    this.work = work;
    this.starter = starter;
    this.readOnly = Set.copyOf(readOnly);
    this.kitFiles = kitFiles;
    this.sourceExtensions = sourceExtensions;
  }

  Path root() {
    return work;
  }

  void createFromStarter() {
    copyTree(starter, work);
  }

  /** Project files for the file tree; the kit is part of the run, not of the project. */
  List<FileEntry> list() {
    try (Stream<Path> walk = Files.walk(work, 3)) {
      return walk.filter(Files::isRegularFile)
          .map(p -> work.relativize(p).toString().replace('\\', '/'))
          .filter(rel -> !isKit(rel))
          .sorted()
          .map(rel -> new FileEntry(rel, readOnly.contains(rel)))
          .toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  String read(String rel) {
    Path file = resolve(rel);
    if (!Files.isRegularFile(file) || isKit(rel)) {
      throw new SessionException("No such file: " + rel);
    }
    return readString(file);
  }

  void write(String rel, String content) {
    Path file = resolve(rel);
    requireEditable(rel);
    if (!Files.isRegularFile(file)) {
      throw new SessionException("No such file: " + rel);
    }
    writeString(file, content);
  }

  void create(String rel) {
    Path file = resolve(rel);
    if (isKit(rel) || rel.startsWith("data/")) {
      throw new SessionException(
          "New files cannot go in " + rel.substring(0, rel.indexOf('/') + 1));
    }
    String extension = rel.contains(".") ? rel.substring(rel.lastIndexOf('.')) : "";
    boolean source = sourceExtensions.contains(extension);
    if (!source && !TEXT_EXTENSIONS.contains(extension)) {
      throw new SessionException(
          "New files must end in " + String.join(", ", allowedExtensions()) + ".");
    }
    if (Files.exists(file)) {
      throw new SessionException(rel + " already exists.");
    }
    String name = file.getFileName().toString();
    writeString(
        file,
        extension.equals(".java")
            ? "public class " + name.substring(0, name.length() - 5) + " {\n}\n"
            : "");
  }

  /** Copies one file back from the starter; the starter is never modified. */
  String resetFile(String rel) {
    Path file = resolve(rel);
    Path original = starter.resolve(rel);
    if (!Files.isRegularFile(original)) {
      throw new SessionException(rel + " is not part of the original project.");
    }
    copy(original, file);
    return readString(file);
  }

  /** Throws away every project file and copies the starter again; the kit stays. */
  void resetProblem() {
    for (FileEntry entry : list()) {
      try {
        Files.delete(work.resolve(entry.path()));
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    copyTree(starter, work);
  }

  /** Tests and data generators come back before every run, so they cannot be weakened. */
  void restoreReadOnly() {
    for (String rel : readOnly) {
      copy(starter.resolve(rel), work.resolve(rel));
    }
  }

  boolean isEditable(String rel) {
    return !readOnly.contains(rel) && !isKit(rel) && !rel.startsWith("data/");
  }

  private void requireEditable(String rel) {
    if (!isEditable(rel)) {
      throw new SessionException(rel + " is read-only.");
    }
  }

  private boolean isKit(String rel) {
    return kitFiles.stream()
        .anyMatch(k -> rel.equals(k) || rel.startsWith(k.substring(0, k.lastIndexOf('/') + 1)));
  }

  /** Resolves a path the user sent, refusing anything that could leave the work folder. */
  Path resolve(String rel) {
    if (rel == null
        || !SAFE_PATH.matcher(rel).matches()
        || rel.contains("..")
        || rel.startsWith("/")) {
      throw new SessionException("That file name is not allowed.");
    }
    Path file = work.resolve(rel).normalize();
    if (!file.startsWith(work.normalize())) {
      throw new SessionException("That file name is not allowed.");
    }
    return file;
  }

  private List<String> allowedExtensions() {
    List<String> all = new ArrayList<>(sourceExtensions);
    all.addAll(TEXT_EXTENSIONS.stream().sorted().toList());
    return all;
  }

  private static String readString(Path file) {
    try {
      return Files.readString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void writeString(Path file, String content) {
    try {
      Files.createDirectories(file.getParent());
      Files.writeString(file, content, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void copy(Path from, Path to) {
    try {
      Files.createDirectories(to.getParent());
      Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void copyTree(Path from, Path to) {
    try (Stream<Path> walk = Files.walk(from)) {
      for (Path p : walk.sorted(Comparator.naturalOrder()).toList()) {
        Path target = to.resolve(from.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else {
          copy(p, target);
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
