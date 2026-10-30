import java.util.ArrayList;
import java.util.List;

/** Lists every balanced string of n opening and n closing brackets. */
public class Parentheses {

    /** Returns each balanced string of n pairs once; for n = 0 that is the empty string. */
    static List<String> generate(int n) {
        List<String> result = new ArrayList<>();
        grow(n, 0, 0, new StringBuilder(), result);
        return result;
    }

    // Extends built, which holds open '(' and closed ')' so far, with closed <= open <= n.
    private static void grow(int n, int open, int closed, StringBuilder built, List<String> result) {
        if (closed == n) {
            result.add(built.toString()); // closed == n forces open == n: the string is complete
            return;
        }
        if (open < n) { // an opening bracket is allowed while some are unused
            built.append('(');
            grow(n, open + 1, closed, built, result);
            built.setLength(built.length() - 1);
        }
        if (closed < open) { // a closing bracket needs an unmatched opening one before it
            built.append(')');
            grow(n, open, closed + 1, built, result);
            built.setLength(built.length() - 1);
        }
    }
}
