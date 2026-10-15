package si.intervue.engine.languages;

/**
 * A declaration for completion and go-to-symbol. {@code kind} is class, interface, record, enum,
 * method or field; {@code container} is the enclosing type ("" for top-level types); {@code detail}
 * is a short signature such as "int sum(int[] values)". Positions are 1-based.
 */
public record SourceSymbol(
    String name,
    String kind,
    String container,
    String detail,
    String path,
    long line,
    long column) {}
