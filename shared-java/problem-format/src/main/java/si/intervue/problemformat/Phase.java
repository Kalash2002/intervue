package si.intervue.problemformat;

/** One phase of a problem; {@code brief} and {@code hints} are paths inside the problem folder. */
public record Phase(
    int id,
    String title,
    String brief,
    String hints,
    int weight,
    boolean allOrNothing,
    long timeoutRefMs) {}
