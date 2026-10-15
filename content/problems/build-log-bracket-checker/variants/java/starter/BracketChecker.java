import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/** Checks that brackets in build-log lines open and close in order. */
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

    /** Whether every bracket on the line is closed by its match, in order. */
    public boolean isBalanced(String line) {
        Deque<Character> open = new ArrayDeque<>();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isOpener(c)) {
                open.push(c);
            } else if (isCloser(c)) {
                if (open.isEmpty() || closerFor(open.pop()) != c) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Line numbers, counting from 1, of the lines that are not balanced. */
    public List<Integer> unbalancedLines(List<String> lines) {
        List<Integer> bad = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (!isBalanced(lines.get(i))) {
                bad.add(i + 1);
            }
        }
        return bad;
    }

    /** The first problem on the line, ignoring quoted text. See the brief. */
    public Optional<BracketError> firstError(String line) {
        throw new UnsupportedOperationException("firstError is not implemented yet");
    }
}
