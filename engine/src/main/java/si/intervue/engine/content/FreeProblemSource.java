package si.intervue.engine.content;

import java.nio.file.Path;
import java.util.List;
import si.intervue.problemformat.ProblemFolder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * The problems shipped in the installer, listed in {@code free.json}, in its order. They need no
 * account and no download (ADR 0001: two free problems).
 */
public final class FreeProblemSource implements ContentStore {

  private record FreeList(int formatVersion, List<String> problems) {}

  private final List<String> ids;
  private final ProblemDirectory problems;

  public FreeProblemSource(Path freeJson, Path problemsRoot) {
    this.ids = readIds(freeJson);
    this.problems = new ProblemDirectory(problemsRoot);
  }

  /** The ids listed in a {@code free.json}, in order. */
  public static List<String> readIds(Path freeJson) {
    try {
      FreeList list =
          JsonMapper.builder()
              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
              .build()
              .readValue(freeJson.toFile(), FreeList.class);
      return list.problems() == null ? List.of() : List.copyOf(list.problems());
    } catch (JacksonException e) {
      throw new ContentException("Cannot read " + freeJson + ": " + e.getOriginalMessage());
    }
  }

  @Override
  public List<String> ids() {
    return ids.stream().filter(problems::isValid).toList();
  }

  @Override
  public boolean isFree(String id) {
    return ids.contains(id);
  }

  @Override
  public ProblemFolder load(String id) {
    if (!ids.contains(id)) {
      throw new ContentException("Problem " + id + " is not one of the free problems");
    }
    return problems.load(id);
  }
}
