import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** Checks that brackets in build-log lines open and close in order. One pass per line. */
public class BracketChecker {

    static boolean isOpener(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    static boolean isCloser(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    static char closerFor(char opener) {
        return switch (opener) {
            case '(' -> ')';
            case '[' -> ']';
            default -> '}';
        };
    }

    public boolean isBalanced(String line) {
        return firstError(line).isEmpty();
    }

    public List<Integer> unbalancedLines(List<String> lines) {
        List<Integer> bad = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (!isBalanced(lines.get(i))) {
                bad.add(i + 1);
            }
        }
        return bad;
    }

    /**
     * A stack of the columns of open brackets; the bottom of the stack is the earliest still open.
     * An int array instead of a Deque, because a line can hold millions of brackets.
     */
    public Optional<BracketError> firstError(String line) {
        int[] open = new int[16];
        int depth = 0;
        int quote = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quote >= 0) {
                if (c == '\\') {
                    i++;
                } else if (c == '"') {
                    quote = -1;
                }
            } else if (c == '"') {
                quote = i;
            } else if (isOpener(c)) {
                if (depth == open.length) {
                    open = Arrays.copyOf(open, depth * 2);
                }
                open[depth++] = i;
            } else if (isCloser(c)) {
                if (depth == 0) {
                    return Optional.of(new BracketError(i, BracketError.Kind.UNEXPECTED_CLOSER));
                }
                if (closerFor(line.charAt(open[--depth])) != c) {
                    return Optional.of(new BracketError(i, BracketError.Kind.MISMATCH));
                }
            }
        }
        if (quote >= 0) {
            return Optional.of(new BracketError(quote, BracketError.Kind.UNCLOSED_STRING));
        }
        return depth > 0 ? Optional.of(new BracketError(open[0], BracketError.Kind.UNCLOSED)) : Optional.empty();
    }
}
