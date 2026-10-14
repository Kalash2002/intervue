package si.intervue.problemformat;

import java.nio.file.Path;
import java.util.Map;

/** A problem as read from disk: its folder, {@code problem.json}, and variants by language. */
public record ProblemFolder(Path dir, Problem problem, Map<String, Variant> variants) {

  public Path variantDir(String language) {
    return dir.resolve("variants").resolve(language);
  }
}
