/** Measures balanced stretches in a string of round brackets. Two counting passes, constant space. */
public class Brackets {

    /** Returns the length of the longest balanced run of consecutive characters in s. */
    static int longestBalanced(String s) {
        int best = 0;
        int open = 0, close = 0;
        for (int i = 0; i < s.length(); i++) { // left to right: an extra ) is a cut
            if (s.charAt(i) == '(') open++; else close++;
            if (open == close) best = Math.max(best, 2 * close);
            else if (close > open) { open = 0; close = 0; }
        }
        open = 0;
        close = 0;
        for (int i = s.length() - 1; i >= 0; i--) { // right to left: an extra ( is a cut
            if (s.charAt(i) == ')') close++; else open++;
            if (open == close) best = Math.max(best, 2 * open);
            else if (open > close) { open = 0; close = 0; }
        }
        return best;
    }
}
