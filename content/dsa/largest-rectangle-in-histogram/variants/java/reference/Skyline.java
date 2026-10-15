/** Measures rectangles that fit inside a skyline of bars. */
public class Skyline {

    /** Returns the area of the largest rectangle that sits on the base and fits inside the bars. */
    static int biggestBlock(int[] heights) {
        int n = heights.length;
        int[] stack = new int[n + 1]; // positions of bars with non-decreasing heights
        int size = 0;
        int best = 0;
        for (int i = 0; i <= n; i++) {
            int current = i == n ? 0 : heights[i]; // a bar of height 0 at the end flushes the stack
            while (size > 0 && heights[stack[size - 1]] > current) {
                int height = heights[stack[--size]];
                int leftBoundary = size == 0 ? -1 : stack[size - 1]; // the bar below on the stack, or the start
                best = Math.max(best, height * (i - leftBoundary - 1));
            }
            stack[size++] = i;
        }
        return best;
    }
}
