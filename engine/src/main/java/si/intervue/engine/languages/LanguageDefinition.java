package si.intervue.engine.languages;

import java.util.List;

/**
 * Identity of a language package; {@code extensions} are the source files the editor treats as
 * code.
 */
public record LanguageDefinition(
    String id, String displayName, int spiVersion, List<String> extensions) {}
