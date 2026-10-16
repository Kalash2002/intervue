package si.intervue.problemformat;

import static si.intervue.problemformat.ProblemReader.nullToEmpty;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Owns the rules for problems whose section is {@code dsa-quick} (ADR 0012): the shape of the
 * statement brief, of the walkthrough answer, and of the starter files. {@link ProblemValidator}
 * calls it; every message is part of the validator's output.
 */
final class DsaShape {

  private DsaShape() {}

  /** The fixed answer headings of a DSA walkthrough; Better and Optimal carry a name after them. */
  static final List<String> DSA_ANSWER_HEADINGS =
      List.of(
          "## The question",
          "## How to think about it",
          "## Brute force",
          "## Better: ",
          "## Optimal: ",
          "## Complexity",
          "## Where it shows up",
          "## Follow-up answers");

  /** Approach sections that must show code and a stated cost: Brute force, Better, Optimal. */
  private static final List<String> DSA_APPROACH_PREFIXES = DSA_ANSWER_HEADINGS.subList(2, 5);

  private static final Pattern IMAGE = Pattern.compile("!\\[[^\\]]*\\]\\(figures/[^)\\s]+\\)");

  /** A heading line and the text up to the next "## " heading. */
  private record Section(String heading, String body) {}

  private static List<Section> sections(String text) {
    List<Section> out = new ArrayList<>();
    String heading = null;
    StringBuilder body = new StringBuilder();
    for (String line : text.split("\\r?\\n", -1)) {
      if (line.startsWith("## ")) {
        if (heading != null) {
          out.add(new Section(heading, body.toString()));
        }
        heading = line.stripTrailing();
        body = new StringBuilder();
      } else {
        body.append(line).append('\n');
      }
    }
    if (heading != null) {
      out.add(new Section(heading, body.toString()));
    }
    return out;
  }

  /** Headings in order (Better and Optimal named), code and cost per approach, one figure. */
  static void checkAnswer(String text, List<String> errors) {
    List<Section> all = sections(text);
    int at = 0;
    for (String want : DSA_ANSWER_HEADINGS) {
      int found = find(all, at, want);
      if (found < 0) {
        String shown = want.endsWith(": ") ? want + "<name>" : want;
        errors.add("walkthrough.md needs the heading \"" + shown + "\" after the previous ones");
        continue;
      }
      at = found + 1;
      // Further Better sections are consumed so Optimal is searched after all of them.
      while (want.equals("## Better: ")
          && at < all.size()
          && all.get(at).heading().startsWith(want)) {
        at++;
      }
    }
    checkApproachBlocks(all, errors);
    if (!IMAGE.matcher(text).find()) {
      errors.add("walkthrough.md needs at least one figure ![...](figures/...)");
    }
  }

  /** Index of the first section from {@code from} matching {@code want}, or -1. */
  private static int find(List<Section> all, int from, String want) {
    boolean named = want.endsWith(": ");
    for (int i = from; i < all.size(); i++) {
      String h = all.get(i).heading();
      if (named ? h.startsWith(want) && !h.substring(want.length()).isBlank() : h.equals(want)) {
        return i;
      }
    }
    return -1;
  }

  private static void checkApproachBlocks(List<Section> all, List<String> errors) {
    for (Section s : all) {
      if (DSA_APPROACH_PREFIXES.stream().noneMatch(s.heading()::startsWith)) {
        continue;
      }
      if (!s.body().contains("```java")) {
        errors.add("walkthrough.md \"" + s.heading() + "\" needs a ```java block");
      }
      if (s.body().lines().noneMatch(l -> l.startsWith("**Complexity:**"))) {
        errors.add("walkthrough.md \"" + s.heading() + "\" needs a \"**Complexity:**\" line");
      }
    }
  }

  /**
   * The phase-1 brief is a title, numbered examples, constraints and an optional follow-up, so the
   * room can lay it out.
   */
  static void checkStatement(ProblemFolder folder, List<String> errors) {
    List<Phase> phases = nullToEmpty(folder.problem().phases());
    if (phases.isEmpty() || phases.get(0).brief() == null) {
      return;
    }
    String name = phases.get(0).brief();
    Path file = folder.dir().resolve(name);
    if (!Files.isRegularFile(file)) {
      return;
    }
    String text = ProblemValidator.readString(file);
    String first = text.lines().filter(l -> !l.isBlank()).findFirst().orElse("");
    if (!first.startsWith("# ")) {
      errors.add(name + " needs a \"# \" title line first");
    }
    List<Section> all = sections(text);
    checkTail(all, checkExamples(all, name, errors), name, errors);
  }

  /** Checks the numbered examples and returns the index of the first section after them. */
  private static int checkExamples(List<Section> all, String name, List<String> errors) {
    int i = 0;
    int n = 1;
    while (i < all.size() && all.get(i).heading().equals("## Example " + n)) {
      for (String label : List.of("Input", "Output", "Explanation")) {
        if (!hasLabel(all.get(i).body(), label)) {
          errors.add(name + " Example " + n + " has no \"**" + label + ":**\" text");
        }
      }
      i++;
      n++;
    }
    if (n == 1) {
      errors.add(name + " needs \"## Example 1\"");
    }
    return i;
  }

  /** A line that starts with the bold label and has text after it. */
  private static boolean hasLabel(String body, String label) {
    String prefix = "**" + label + ":**";
    return body.lines()
        .anyMatch(l -> l.startsWith(prefix) && !l.substring(prefix.length()).isBlank());
  }

  /** Constraints right after the examples, then an optional Follow-up, then nothing else. */
  private static void checkTail(List<Section> all, int i, String name, List<String> errors) {
    if (i >= all.size() || !all.get(i).heading().equals("## Constraints")) {
      errors.add(
          name
              + " needs \"## Constraints\" right after the examples"
              + (i < all.size() ? " (found \"" + all.get(i).heading() + "\")" : ""));
      return;
    }
    if (all.get(i).body().lines().noneMatch(l -> l.startsWith("- "))) {
      errors.add(name + " \"## Constraints\" needs at least one \"- \" line");
    }
    i++;
    if (i < all.size() && all.get(i).heading().equals("## Follow-up")) {
      i++;
    }
    if (i < all.size()) {
      errors.add(name + " has an unexpected heading \"" + all.get(i).heading() + "\"");
    }
  }

  /** A DSA candidate's editable starter files import java.util.* so the first line compiles. */
  static void checkStarterImports(Path starter, Variant variant, List<String> errors) {
    if (!Files.isDirectory(starter)) {
      return;
    }
    Set<String> locked = Set.copyOf(nullToEmpty(variant.readOnly()));
    try (var files = Files.walk(starter)) {
      files
          .filter(f -> Files.isRegularFile(f) && f.toString().endsWith(".java"))
          .forEach(
              f -> {
                String rel = starter.relativize(f).toString().replace('\\', '/');
                if (!locked.contains(rel)
                    && !rel.equals(variant.main() + ".java")
                    && !ProblemValidator.readString(f).contains("import java.util.*;")) {
                  errors.add("starter/" + rel + " needs \"import java.util.*;\"");
                }
              });
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
