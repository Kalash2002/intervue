package si.intervue.problemformat;

import java.util.List;

/** The language-neutral {@code problem.json}. */
public record Problem(
    int formatVersion,
    String id,
    int version,
    String section,
    String title,
    String summary,
    String difficulty,
    List<String> techniques,
    List<String> tags,
    String domain,
    int minutes,
    List<Phase> phases,
    List<String> followUps,
    List<String> languages) {

  /** {@code tags} is optional on disk; every reader sees a list. */
  public Problem {
    tags = tags == null ? List.of() : List.copyOf(tags);
  }
}
