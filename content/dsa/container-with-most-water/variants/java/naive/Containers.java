/** Finds the largest tank that two of a row of walls can form. */
public class Containers {

    /** Returns the largest volume: distance between two walls times the height of the shorter one. */
    static int maxVolume(int[] heights) {
        int best = 0;
        for (int i = 0; i < heights.length; i++) {
            for (int j = i + 1; j < heights.length; j++) {
                best = Math.max(best, Math.min(heights[i], heights[j]) * (j - i));
            }
        }
        return best;
    }
}
