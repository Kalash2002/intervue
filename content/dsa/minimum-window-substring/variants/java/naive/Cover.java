/** Finds the shortest stretch of a text that covers the characters of another string. */
public class Cover {

    /** Returns the shortest (leftmost on ties) stretch of the text that covers the need, or "". */
    static String smallest(String text, String need) {
        int bestStart = 0;
        int bestLength = Integer.MAX_VALUE;
        for (int start = 0; start < text.length(); start++) {
            int[] missing = new int[128];
            for (int i = 0; i < need.length(); i++) {
                missing[need.charAt(i)]++;
            }
            int totalMissing = need.length();
            for (int end = start; end < text.length() && totalMissing > 0; end++) {
                if (missing[text.charAt(end)]-- > 0) {
                    totalMissing--;
                }
                if (totalMissing == 0 && end - start + 1 < bestLength) {
                    bestLength = end - start + 1;
                    bestStart = start;
                }
            }
        }
        return bestLength == Integer.MAX_VALUE ? "" : text.substring(bestStart, bestStart + bestLength);
    }
}
