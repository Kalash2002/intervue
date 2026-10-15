package si.intervue.content;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import si.intervue.engine.content.PlainFolderSource;
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
    List<String> problemIds = PlainFolderSource.folderNames(problemsRoot);
    List<String> dsaIds = PlainFolderSource.folderNames(dsaRoot);
    Map<String, List<String>> coveredByOf = new LinkedHashMap<>();
    for (String id : dsaIds) {
      coveredByOf.put(id, coveredBy(dsaRoot.resolve(id)));
    }
    // Each Markdown file is read once: its dsa:// targets in order, and a forbidden-site error if
    // any.
    Map<Path, List<String>> linksByFile = new LinkedHashMap<>();
    List<String> siteErrors = new ArrayList<>();
    for (Path root : List.of(problemsRoot, dsaRoot)) {
      for (Path md : markdownFiles(root)) {
        String text = read(md);
        List<String> targets = new ArrayList<>();
        var links = DSA_LINK.matcher(text);
        while (links.find()) {
          targets.add(links.group(1));
        }
        linksByFile.put(md, targets);
        var site = FORBIDDEN_SITE.matcher(text);
        if (site.find()) {
          siteErrors.add(md + " links to " + site.group(2) + "; link dsa:// questions instead");
        }
      }
    }
    for (String id : dsaIds) {
      if (problemIds.contains(id)) {
        errors.add(id + " is a folder in both content/problems and content/dsa");
      }
      for (String target : coveredByOf.get(id)) {
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
    errors.addAll(agreement(problemsRoot, problemIds, dsaIds, coveredByOf, linksByFile));
    for (var file : linksByFile.entrySet()) {
      for (String target : file.getValue()) {
        if (!dsaIds.contains(target)) {
          errors.add(
              file.getKey() + " links dsa://" + target + " but content/dsa has no such folder");
        }
      }
    }
    errors.addAll(siteErrors);
    return errors;
  }

  /** A problem's walkthrough links dsa://X exactly when X's coveredBy lists that problem. */
  private static List<String> agreement(
      Path problemsRoot,
      List<String> problemIds,
      List<String> dsaIds,
      Map<String, List<String>> coveredByOf,
      Map<Path, List<String>> linksByFile) {
    List<String> errors = new ArrayList<>();
    for (String id : problemIds) {
      Path walkthrough = problemsRoot.resolve(id).resolve("walkthrough.md");
      Set<String> linked = new TreeSet<>(linksByFile.getOrDefault(walkthrough, List.of()));
      for (String target : linked) {
        // A missing target folder is reported by the link check.
        if (dsaIds.contains(target) && !coveredByOf.get(target).contains(id)) {
          errors.add(
              "content/problems/"
                  + id
                  + " links dsa://"
                  + target
                  + " but content/dsa/"
                  + target
                  + " does not list "
                  + id
                  + " in coveredBy");
        }
      }
      for (String x : dsaIds) {
        if (coveredByOf.get(x).contains(id) && !linked.contains(x)) {
          errors.add(
              "content/dsa/"
                  + x
                  + " is coveredBy "
                  + id
                  + " but content/problems/"
                  + id
                  + "/walkthrough.md does not link dsa://"
                  + x);
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
