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
    String domain,
    int minutes,
    List<Phase> phases,
    List<String> followUps,
    List<String> languages) {}
