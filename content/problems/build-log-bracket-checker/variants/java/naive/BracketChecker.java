import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Naive solution: correct, and slow on purpose. For each closing bracket it walks back to find the
 * nearest opener not yet matched, so deep nesting is O(n^2). Phases 1 and 2 pass; phase 3 must time out.
 */
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

    public Optional<BracketError> firstError(String line) {
        int n = line.length();
        boolean[] quoted = new boolean[n];
        int quote = -1;
        for (int i = 0; i < n; i++) {
            char c = line.charAt(i);
            if (quote >= 0) {
                quoted[i] = true;
                if (c == '\\' && i + 1 < n) {
                    quoted[++i] = true;
                } else if (c == '"') {
                    quote = -1;
                }
            } else if (c == '"') {
                quoted[i] = true;
                quote = i;
            }
        }
        boolean[] matched = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (quoted[i] || !isCloser(line.charAt(i))) {
                continue;
            }
            int j = i - 1;
            while (j >= 0 && (quoted[j] || !isOpener(line.charAt(j)) || matched[j])) {
                j--;
            }
            if (j < 0) {
                return Optional.of(new BracketError(i, BracketError.Kind.UNEXPECTED_CLOSER));
            }
            if (closerFor(line.charAt(j)) != line.charAt(i)) {
                return Optional.of(new BracketError(i, BracketError.Kind.MISMATCH));
            }
            matched[j] = true;
        }
        if (quote >= 0) {
            return Optional.of(new BracketError(quote, BracketError.Kind.UNCLOSED_STRING));
        }
        for (int i = 0; i < n; i++) {
            if (!quoted[i] && isOpener(line.charAt(i)) && !matched[i]) {
                return Optional.of(new BracketError(i, BracketError.Kind.UNCLOSED));
            }
        }
        return Optional.empty();
    }
}
