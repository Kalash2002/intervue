/** Measures balanced stretches in a string of round brackets. */
public class Brackets {

    /** Returns the length of the longest balanced run of consecutive characters in s. */
    static int longestBalanced(String s) {
        int best = 0;
        for (int start = 0; start < s.length(); start++) {
            int depth = 0;
            for (int end = start; end < s.length(); end++) {
                depth += s.charAt(end) == '(' ? 1 : -1;
                if (depth < 0) {
                    break;
                }
                if (depth == 0) {
                    best = Math.max(best, end - start + 1);
                }
            }
        }
        return best;
    }
}
