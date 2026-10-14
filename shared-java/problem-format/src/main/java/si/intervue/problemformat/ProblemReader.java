package si.intervue.problemformat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Reads a problem folder into records. It only parses; whether the problem is valid is {@link
 * ProblemValidator}'s job.
 */
public final class ProblemReader {

  // Unknown fields are tolerated so a newer tool can add optional fields without breaking readers.
  private static final ObjectMapper JSON =
      JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

  private ProblemReader() {}

  public static ProblemFolder read(Path dir) {
    Problem problem = readJson(dir.resolve("problem.json"), Problem.class);
    Map<String, Variant> variants = new LinkedHashMap<>();
    for (String language : nullToEmpty(problem.languages())) {
      Path file = dir.resolve("variants").resolve(language).resolve("variant.json");
      if (Files.isRegularFile(file)) {
        variants.put(language, readJson(file, Variant.class));
      }
    }
    return new ProblemFolder(dir, problem, variants);
  }

  private static <T> T readJson(Path file, Class<T> type) {
    try {
      return JSON.readValue(file.toFile(), type);
    } catch (JacksonException e) {
      throw new ProblemFormatException("Cannot read " + file + ": " + e.getOriginalMessage(), e);
    }
  }

  static <T> List<T> nullToEmpty(List<T> list) {
    return list == null ? List.of() : list;
  }
}
