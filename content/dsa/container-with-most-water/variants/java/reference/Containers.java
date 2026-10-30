/** Finds the largest tank that two of a row of walls can form. */
public class Containers {

    /** Returns the largest volume: distance between two walls times the height of the shorter one. */
    static int maxVolume(int[] heights) {
        int left = 0;
        int right = heights.length - 1;
        int best = 0;
        while (left < right) {
            int level = Math.min(heights[left], heights[right]);
            best = Math.max(best, level * (right - left));
            if (heights[left] < heights[right]) {
                left++; // the shorter wall can never do better with a narrower tank
            } else {
                right--;
            }
        }
        return best;
    }
}
