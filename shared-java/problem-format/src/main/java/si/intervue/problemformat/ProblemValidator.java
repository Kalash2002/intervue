package si.intervue.problemformat;

import static si.intervue.problemformat.ProblemReader.nullToEmpty;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Checks a problem folder against docs/contracts/problem-format.md and returns every violation as a
 * readable message; an empty list means valid. It never changes the folder.
 */
public final class ProblemValidator {

  public static final int FORMAT_VERSION = 1;
  private static final int CORRECTNESS_POINTS = 70;
  // The DSA section is scored 80/20 and has no intro or per-phase split, so its one phase carries
  // the whole correctness share (docs/contracts/problem-format.md, "One-phase problems").
  private static final int DSA_CORRECTNESS_POINTS = 80;

  /** The section whose problems follow the DSA statement, answer and starter rules. */
  public static final String DSA_SECTION = "dsa-quick";

  private static final Set<String> DIFFICULTIES = Set.of("Easy", "Medium", "Hard");
  private static final Pattern ID = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

  private ProblemValidator() {}

  public static List<String> validate(ProblemFolder folder) {
    List<String> errors = new ArrayList<>();
    checkIdentity(folder, errors);
    checkFields(folder.problem(), errors);
    checkPhases(folder, errors);
    checkVariants(folder, errors);
    boolean dsa = isDsa(folder.problem());
    if (!dsa) {
      requireFile(folder.dir(), "brief/intro.md", errors);
    }
    requireFile(folder.dir(), "walkthrough.md", errors);
    requireFile(folder.dir(), "grading/NOTES.md", errors);
    if (Files.isRegularFile(folder.dir().resolve("walkthrough.md"))) {
      checkWalkthrough(folder.dir(), dsa, errors);
    }
    if (dsa) {
      DsaShape.checkStatement(folder, errors);
    }
    return errors;
  }

  private static boolean isDsa(Problem p) {
    return DSA_SECTION.equals(p.section());
  }

  private static void checkIdentity(ProblemFolder folder, List<String> errors) {
    Problem p = folder.problem();
    if (p.formatVersion() != FORMAT_VERSION) {
      errors.add("formatVersion " + p.formatVersion() + " is not supported");
    }
    String folderName = folder.dir().getFileName().toString();
    if (p.id() == null || !ID.matcher(p.id()).matches() || !p.id().equals(folderName)) {
      errors.add("id must be lowercase-with-dashes and equal the folder name " + folderName);
    }
    if (p.version() < 1) {
      errors.add("version must be 1 or more");
    }
  }

  private static void checkFields(Problem p, List<String> errors) {
    Map<String, String> required = new LinkedHashMap<>();
    required.put("section", p.section());
    required.put("title", p.title());
    required.put("summary", p.summary());
    required.put("domain", p.domain());
    required.forEach(
        (name, value) -> {
          if (value == null || value.isBlank()) {
            errors.add(name + " is empty");
          }
        });
    if (!DIFFICULTIES.contains(p.difficulty())) {
      errors.add("difficulty must be Easy, Medium or Hard");
    }
    if (nullToEmpty(p.techniques()).isEmpty()) {
      errors.add("techniques needs at least one track");
    }
    if (p.tags().stream().anyMatch(t -> t == null || !ID.matcher(t).matches())) {
      errors.add("tags must be lowercase letters, digits and dashes");
    }
    if (p.tags().stream().distinct().count() != p.tags().size()) {
      errors.add("tags must not repeat");
    }
    if (p.minutes() < 1) {
      errors.add("minutes must be positive");
    }
    if (p.coveredBy().stream().anyMatch(c -> c == null || !ID.matcher(c).matches())) {
      errors.add("coveredBy must be problem ids (lowercase letters, digits and dashes)");
    }
  }

  private static void checkPhases(ProblemFolder folder, List<String> errors) {
    List<Phase> phases = nullToEmpty(folder.problem().phases());
    if (phases.isEmpty()) {
      errors.add("a problem needs at least one phase");
      return;
    }
    int weights = 0;
    for (int i = 0; i < phases.size(); i++) {
      Phase phase = phases.get(i);
      if (phase.id() != i + 1) {
        errors.add("phase ids must run 1.." + phases.size() + " in order");
      }
      weights += phase.weight();
      requireFile(folder.dir(), phase.brief(), errors);
      if (requireFile(folder.dir(), phase.hints(), errors)) {
        checkHints(folder.dir().resolve(phase.hints()), phase.hints(), errors);
      }
    }
    int expected = isDsa(folder.problem()) ? DSA_CORRECTNESS_POINTS : CORRECTNESS_POINTS;
    if (weights != expected) {
      errors.add("phase weights sum to " + weights + ", must be " + expected);
    }
  }

  /** The headings every walkthrough has (docs/contracts/problem-format.md), in order. */
  static final List<String> WALKTHROUGH_HEADINGS =
      List.of(
          "## DSA reference",
          "### Theory",
          "### Code",
          "### Complexity",
          "### Practise the technique",
          "## Time budget",
          "## Follow-up answers");

  private static final Pattern FIGURE = Pattern.compile("\\]\\((figures/[^)\\s]+)\\)");

  /** The reference and closing sections exist in order, and every figure it shows exists. */
  private static void checkWalkthrough(Path dir, boolean dsa, List<String> errors) {
    String text = readString(dir.resolve("walkthrough.md"));
    if (dsa) {
      DsaShape.checkAnswer(text, errors);
    } else {
      int at = 0;
      for (String heading : WALKTHROUGH_HEADINGS) {
        int found = text.indexOf("\n" + heading + "\n", at);
        if (found < 0) {
          errors.add(
              "walkthrough.md needs the heading \"" + heading + "\" after the previous ones");
        } else {
          at = found;
        }
      }
    }
    java.util.regex.Matcher m = FIGURE.matcher(text);
    while (m.find()) {
      if (!Files.isRegularFile(dir.resolve(m.group(1)))) {
        errors.add("walkthrough.md shows " + m.group(1) + " which does not exist");
      }
    }
  }

  /** Three non-empty levels and no code: hints teach the approach, never the answer. */
  private static void checkHints(Path file, String name, List<String> errors) {
    String text = readString(file);
    if (text.contains("```")) {
      errors.add(name + " contains a code fence; hints must not contain code");
    }
    Map<Integer, String> levels = HintLevels.parse(text);
    for (int level = 1; level <= HintLevels.LEVELS; level++) {
      if (!levels.containsKey(level)) {
        errors.add(name + " is missing ## Level " + level);
      } else if (levels.get(level).isBlank()) {
        errors.add(name + " has an empty Level " + level);
      }
    }
  }

  private static void checkVariants(ProblemFolder folder, List<String> errors) {
    if (nullToEmpty(folder.problem().languages()).isEmpty()) {
      errors.add("a problem needs at least one language");
    }
    for (String language : nullToEmpty(folder.problem().languages())) {
      Variant variant = folder.variants().get(language);
      if (variant == null) {
        errors.add("language " + language + " has no variants/" + language + "/variant.json");
        continue;
      }
      if (!language.equals(variant.language())) {
        errors.add("variant.json language must be " + language);
      }
      Path starter = folder.variantDir(language).resolve("starter");
      for (String path : nullToEmpty(variant.readOnly())) {
        requireFile(starter, path, errors);
      }
      if (isDsa(folder.problem())) {
        DsaShape.checkStarterImports(starter, variant, errors);
      }
      for (Phase phase : nullToEmpty(folder.problem().phases())) {
        if (variant.tests() == null || !variant.tests().containsKey(String.valueOf(phase.id()))) {
          errors.add(language + " variant has no test class for phase " + phase.id());
        }
      }
    }
  }

  private static boolean requireFile(Path dir, String relative, List<String> errors) {
    if (relative == null || !Files.isRegularFile(dir.resolve(relative))) {
      errors.add("missing file " + relative);
      return false;
    }
    return true;
  }

  static String readString(Path file) {
    try {
      return Files.readString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
