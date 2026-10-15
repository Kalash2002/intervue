/** Finds the longest run of one letter reachable with a limited number of changes. */
public class Relabel {

    /** Returns the longest run of one repeated letter after changing at most k characters. */
    static int longestRun(String s, int k) {
        int[] count = new int[26];
        int left = 0;
        int highest = 0; // highest count of one letter seen in any window so far
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            highest = Math.max(highest, ++count[s.charAt(right) - 'A']);
            if (right - left + 1 - highest > k) { // too many changes needed: slide the window
                count[s.charAt(left) - 'A']--;
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
