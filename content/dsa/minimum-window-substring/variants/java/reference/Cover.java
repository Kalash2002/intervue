/** Finds the shortest stretch of a text that covers the characters of another string. */
public class Cover {

    /** Returns the shortest (leftmost on ties) stretch of the text that covers the need, or "". */
    static String smallest(String text, String need) {
        int[] missing = new int[128]; // how many copies of each character the window still lacks
        for (int i = 0; i < need.length(); i++) {
            missing[need.charAt(i)]++;
        }
        int totalMissing = need.length();
        int bestStart = 0;
        int bestLength = Integer.MAX_VALUE;
        int left = 0;
        for (int right = 0; right < text.length(); right++) {
            if (missing[text.charAt(right)]-- > 0) {
                totalMissing--; // this character was really needed
            }
            while (totalMissing == 0) {
                if (right - left + 1 < bestLength) { // strictly shorter keeps the leftmost
                    bestLength = right - left + 1;
                    bestStart = left;
                }
                if (++missing[text.charAt(left)] > 0) {
                    totalMissing++; // dropping this character leaves a gap
                }
                left++;
            }
        }
        return bestLength == Integer.MAX_VALUE ? "" : text.substring(bestStart, bestStart + bestLength);
    }
}
