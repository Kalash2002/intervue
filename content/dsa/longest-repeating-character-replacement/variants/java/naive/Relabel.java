/** Finds the longest run of one letter reachable with a limited number of changes. */
public class Relabel {

    /** Returns the longest run of one repeated letter after changing at most k characters. */
    static int longestRun(String s, int k) {
        int best = 0;
        for (int start = 0; start < s.length(); start++) {
            int[] count = new int[26];
            int highest = 0;
            for (int end = start; end < s.length(); end++) {
                highest = Math.max(highest, ++count[s.charAt(end) - 'A']);
                if (end - start + 1 - highest > k) {
                    break;
                }
                best = Math.max(best, end - start + 1);
            }
        }
        return best;
    }
}
