package si.intervue.engine.sections;

/** Identity of a section package, as in its {@code section.json}. */
public record SectionDefinition(
    String id,
    String name,
    int version,
    int spiVersion,
    String runnerKind,
    String graderKind,
    String uiMode) {}
