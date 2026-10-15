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
  private static final Set<String> DIFFICULTIES = Set.of("Easy", "Medium", "Hard");
  private static final Pattern ID = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");
  private static final Pattern TAG = ID;

  private ProblemValidator() {}

  public static List<String> validate(ProblemFolder folder) {
    List<String> errors = new ArrayList<>();
    checkIdentity(folder, errors);
    checkFields(folder.problem(), errors);
    checkPhases(folder, errors);
    checkVariants(folder, errors);
    for (String file : List.of("brief/intro.md", "walkthrough.md", "grading/NOTES.md")) {
      requireFile(folder.dir(), file, errors);
    }
    return errors;
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
    if (p.tags().stream().anyMatch(t -> !TAG.matcher(t).matches())) {
      errors.add("tags must be lowercase letters, digits and dashes");
    }
    if (p.tags().stream().distinct().count() != p.tags().size()) {
      errors.add("tags must not repeat");
    }
    if (p.minutes() < 1) {
      errors.add("minutes must be positive");
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
    if (weights != CORRECTNESS_POINTS) {
      errors.add("phase weights sum to " + weights + ", must be " + CORRECTNESS_POINTS);
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

  private static String readString(Path file) {
    try {
      return Files.readString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
