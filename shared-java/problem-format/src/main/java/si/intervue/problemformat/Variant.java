package si.intervue.problemformat;

import java.util.List;
import java.util.Map;

/** One language's {@code variant.json}; {@code tests} maps a phase id to its test class. */
public record Variant(
    String language,
    String minToolchain,
    String main,
    List<String> readOnly,
    Map<String, String> tests,
    String verifiedAt,
    String verifiedWith,
    Map<String, Long> referenceMs) {}
