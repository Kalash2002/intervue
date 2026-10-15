import java.util.ArrayDeque;
import java.util.Deque;

/** Measures balanced stretches in a string of round brackets. */
public class Brackets {

    /** Returns the length of the longest balanced run of consecutive characters in s. */
    static int longestBalanced(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int best = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
                continue;
            }
            stack.pop();
            if (stack.isEmpty()) {
                stack.push(i);
            } else {
                best = Math.max(best, i - stack.peek());
            }
        }
        return best;
    }
}
