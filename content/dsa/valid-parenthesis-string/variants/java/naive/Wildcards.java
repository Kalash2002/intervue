import java.util.Arrays;

/** Decides whether the stars in a bracket string can be read so that the brackets balance. */
public class Wildcards {

    /** Returns true if some reading of the stars as (, ) or nothing balances s. */
    static boolean canBalance(String s) {
        int n = s.length();
        boolean[] can = new boolean[n + 2]; // can[k]: some reading so far leaves exactly k open
        boolean[] next = new boolean[n + 2];
        can[0] = true;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            Arrays.fill(next, 0, i + 2, false);
            for (int k = 0; k <= i; k++) { // after i characters at most i brackets are open
                if (can[k]) {
                    if (c != ')') {
                        next[k + 1] = true; // a ( or a star read as (
                    }
                    if (c != '(' && k > 0) {
                        next[k - 1] = true; // a ) or a star read as ), with a bracket to close
                    }
                    if (c == '*') {
                        next[k] = true; // a star read as nothing
                    }
                }
            }
            boolean[] swap = can;
            can = next;
            next = swap;
        }
        return can[0];
    }
}
