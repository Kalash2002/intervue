/** Where a line first goes wrong; {@code column} counts from 0. */
public record BracketError(int column, Kind kind) {

    public enum Kind {
        /** A closing bracket with nothing open. */
        UNEXPECTED_CLOSER,
        /** A closing bracket that does not match the most recent open one. */
        MISMATCH,
        /** The line ends with brackets still open; the column is the earliest of them. */
        UNCLOSED,
        /** The line ends inside double quotes; the column is the opening quote. */
        UNCLOSED_STRING
    }
}
