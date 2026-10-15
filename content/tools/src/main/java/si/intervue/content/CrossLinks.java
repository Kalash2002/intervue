package si.intervue.content;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import si.intervue.problemformat.ProblemFormatException;
import si.intervue.problemformat.ProblemReader;

/**
 * Proves the links between content/problems and content/dsa in both directions, plus that ids do
 * not collide and no Markdown links to a question site. It only reads; it must not rewrite content
 * or judge a single problem (that is {@link ProblemVerifier}).
 */
final class CrossLinks {

  // Only the Markdown link form counts, so prose and backticked ids (as in INDEX.md) never match.
  private static final Pattern DSA_LINK = Pattern.compile("\\]\\(dsa://([a-z0-9-]+)\\)");
  private static final Pattern FORBIDDEN_SITE =
      Pattern.compile("https?://(www\\.)?(neetcode\\.io|leetcode\\.com)");

  private CrossLinks() {}

  /** Error messages, empty when every link holds. {@code dsaRoot} may not exist yet. */
  static List<String> check(Path problemsRoot, Path dsaRoot) {
    List<String> errors = new ArrayList<>();
    List<String> problemIds = folderNames(problemsRoot);
    List<String> dsaIds = folderNames(dsaRoot);
    for (String id : dsaIds) {
      if (problemIds.contains(id)) {
        errors.add(id + " is a folder in both content/problems and content/dsa");
      }
      for (String target : coveredBy(dsaRoot.resolve(id))) {
        if (!problemIds.contains(target)) {
          errors.add(
              "content/dsa/"
                  + id
                  + " is coveredBy "
                  + target
                  + " which is not in content/problems");
        }
      }
    }
    for (Path root : List.of(problemsRoot, dsaRoot)) {
      for (Path md : markdownFiles(root)) {
        String text = read(md);
        var links = DSA_LINK.matcher(text);
        while (links.find()) {
          if (!dsaIds.contains(links.group(1))) {
            errors.add(
                md + " links dsa://" + links.group(1) + " but content/dsa has no such folder");
          }
        }
        var site = FORBIDDEN_SITE.matcher(text);
        if (site.find()) {
          errors.add(md + " links to " + site.group(2) + "; link dsa:// questions instead");
        }
      }
    }
    return errors;
  }

  private static List<String> coveredBy(Path dir) {
    try {
      return ProblemReader.read(dir).problem().coveredBy();
    } catch (ProblemFormatException e) {
      // The per-problem verify run reports the unreadable file; here it just has no links.
      return List.of();
    }
  }

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

  private static List<Path> markdownFiles(Path root) {
    if (!Files.isDirectory(root)) {
      return List.of();
    }
    try (Stream<Path> walk = Files.walk(root)) {
      return walk.filter(p -> p.toString().endsWith(".md")).sorted().toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String read(Path file) {
    try {
      return Files.readString(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
