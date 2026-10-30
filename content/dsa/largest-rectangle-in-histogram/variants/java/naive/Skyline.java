/** Measures rectangles that fit inside a skyline of bars. */
public class Skyline {

    /** Returns the area of the largest rectangle that sits on the base and fits inside the bars. */
    static int biggestBlock(int[] heights) {
        int best = 0;
        for (int start = 0; start < heights.length; start++) {
            int lowest = Integer.MAX_VALUE;
            for (int end = start; end < heights.length; end++) {
                lowest = Math.min(lowest, heights[end]);
                best = Math.max(best, lowest * (end - start + 1));
            }
        }
        return best;
    }
}
